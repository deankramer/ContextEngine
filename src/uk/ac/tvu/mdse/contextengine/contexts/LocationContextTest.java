/*
 * Copyright (C) 2014 The Context Engine Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationContextTest extends Component implements LocationListener {

	private static final long serialVersionUID = -6360309106992426663L;

	private static final String LOG_TAG = "LocationContextTest";
	private boolean D = true;

	private LocationManager locationManager;
	private String provider;
	private Location location;

	// in real life you *DO NOT* want to do this, it may consume too many
	// resources
	// (time less than 60000ms for minTime is NOT recommended, used only for
	// testing)
	private int minTime = 3000; // in milliseconds
	private int minDistance = 1000; // in meters

	// What do we define as nearby (in meters)
	private float distancebetween = 100000000;

	public LocationContextTest(Context c) {
		super("LocationContextTest", c);
		valuesSets.remove(0);
		//
		// locationManager = (LocationManager)
		// c.getSystemService(Context.LOCATION_SERVICE);
		//
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// criteria.setAltitudeRequired(false);
		// criteria.setBearingRequired(false);
		// criteria.setCostAllowed(true);
		// criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		//
		// provider = locationManager.getBestProvider(criteria, false);
		//
		// if (provider != null && locationManager.isProviderEnabled(provider))
		// {
		// //set minTime(milliseconds) and minDistance(meters)
		// locationManager.requestLocationUpdates(provider, minTime,
		// minDistance, this);
		//
		// location = locationManager.getLastKnownLocation(provider);
		// Log.v("constr-LocationServices", "Latitude= " +
		// location.getLatitude() + " Longitude= " + location.getLongitude());
		// Log.v("LocationServices", "Location accuracy: " +
		// location.getAccuracy());
		// }
		// else{
		// Intent myIntent = new
		// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		// c.startActivity(myIntent);
		// }
		if (D)
			Log.d(LOG_TAG, "constructor");
	}

	public void setUpdatesCriteria(int time, int distance) {
		if (D)
			Log.d(LOG_TAG, "setUpdatesCriteria");
		if ((time < minTime) || (distance < minDistance)) {
			this.minTime = time;
			this.minDistance = distance;
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(provider, minTime,
					minDistance, this);
		}
	}

	public void onLocationChanged(Location locale) {
		checkContext(locale);
	}

	// to test location without moving phone
	public void onLocationChangedManually(Double latitude, Double longitude) {
		location = new Location("");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		checkContext(location);
	}

	protected void checkContext(Location locale) {
		ArrayList<String> nearbys = isNearby(locale);

		if ((nearbys.size() > 0)) {
			sendNotification(nearbys);
		}

	}

	protected ArrayList<String> isNearby(Location locale) {
		// ArrayList<String> nearbys = new ArrayList<String>();
		String[] nearbys = new String[this.valuesSets.get(0).locationsSet
				.size()];
		float[] distances = new float[this.valuesSets.get(0).locationsSet
				.size()];
		Hashtable<Float, String> h = new Hashtable<Float, String>();

		for (int i = 0; i < this.valuesSets.get(0).locationsSet.size(); i++) {// locationSet.entrySet()){
			if (locale
					.distanceTo(valuesSets.get(0).locationsSet.get(i).location) <= distancebetween) {
				// nearbys.add(entry.getKey());
				nearbys[i] = valuesSets.get(0).locationsSet.get(i).identifier;
				distances[i] = locale.distanceTo(valuesSets.get(0).locationsSet
						.get(i).location);
				h.put(distances[i], nearbys[i]);
			}
		}
		return sortHashtable(h);
	}

	protected ArrayList<String> sortHashtable(Hashtable<Float, String> h) {
		Vector<Float> v = new Vector<Float>(h.keySet());
		Collections.sort(v);
		ArrayList<String> nearbyPlaces = new ArrayList<String>();
		Iterator<Float> it;
		it = v.iterator();
		while (it.hasNext()) {
			nearbyPlaces.add((String) h.get(it.next()));
		}
		return nearbyPlaces;
	}

	public void sendNotification(ArrayList<String> nearbys) {
		if (D)
			Log.d(LOG_TAG, "sendNotification(nearbys)");
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, contextName);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_INFORMATION, nearbys.get(0));
		if (D)
			Log.d(LOG_TAG,
					"sendNotification(ContextValues).contextInformation:"
							+ nearbys.get(0));
		if (D)
			Log.d(LOG_TAG,
					"sendNotification(ContextValues).contextInformation:"
							+ nearbys.get(1));
		if (D)
			Log.d(LOG_TAG,
					"sendNotification(ContextValues).contextInformation:"
							+ nearbys.get(2));
		if (D)
			Log.d(LOG_TAG,
					"sendNotification(ContextValues).contextInformation:"
							+ nearbys.get(3));
		intent.putExtra(CONTEXT_APPLICATION_KEY, valuesSets.get(0)
				.getKeysList());
		if (D)
			Log.d(LOG_TAG, "sendNotification(ContextValues)-keylist0:"
					+ valuesSets.get(0).getKeysList());
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}

		// intent.putExtra(CONTEXT_INFORMATION, nearbys);

	}

	public void onProviderDisabled(String prv) {
		if (D)
			Log.v(LOG_TAG, "Provider " + prv + " disabled");

		// if best provider disabled, make GPS default
		provider = locationManager.GPS_PROVIDER;

		// check if GPS enabled, if not - ask for it
		if (!locationManager.isProviderEnabled(provider)) {
			Intent gpsOptionsIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(gpsOptionsIntent);
		}
	}

	public void onProviderEnabled(String prv) {
		if (D)
			Log.v(LOG_TAG, "Provider " + prv + " enabled");
		locationManager.removeUpdates(this);
		locationManager.requestLocationUpdates(provider, minTime, minDistance,
				this);

		location = locationManager.getLastKnownLocation(provider);
		if (D)
			Log.v(LOG_TAG, "Latitude= " + location.getLatitude()
					+ " Longitude= " + location.getLongitude());
		if (D)
			Log.v(LOG_TAG, "Location accuracy: " + location.getAccuracy());
	}

	public void onStatusChanged(String prv, int stat, Bundle extras) {
		if (stat == 2) {
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(provider, minTime,
					minDistance, this);

			location = locationManager.getLastKnownLocation(provider);
			if (D)
				Log.v(LOG_TAG, "Latitude= " + location.getLatitude()
						+ " Longitude= " + location.getLongitude());
			if (D)
				Log.v(LOG_TAG, "Location accuracy: " + location.getAccuracy());
		}
	}

	public void stop() {
		locationManager.removeUpdates(this);
		if (D)
			Log.v(LOG_TAG, "Stopping");
	}

	public void setDistanceBetween(float distance) {
		this.distancebetween = distance;
	}

}
