package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.HashMap;
import java.util.Map;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationContext extends Component implements LocationListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6360309106992426663L;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Map<String, Location> locationSet;
	//What do we define as nearby (in meters)
	private float distancebetween = 1000; 
	
	public LocationContext(String name, Context c) {
		super(name, c);
		
	    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);
		Log.v(contextName, "Latitude= " + location.getLatitude() + " Longitude= " + location.getLongitude());
		Log.v(contextName, "Location accuracy: " + location.getAccuracy());
	}

	public void onLocationChanged(Location locale) {
		checkContext(locale);
	}

	public void onProviderDisabled(String prv) {
		Log.v(contextName, "Provider " + prv + " disabled");
	}

	public void onProviderEnabled(String prv) {
		Log.v(contextName, "Provider " + prv + " enabled");
	}

	public void onStatusChanged(String prv, int stat, Bundle extras) {	
	}
	
	protected Map<String,Location> isNearby(Location locale){
		Map<String, Location> nearbys = new HashMap<String, Location>();
		for(Map.Entry<String, Location> entry: locationSet.entrySet()){
			if(location.distanceTo(entry.getValue()) <= distancebetween)
				nearbys.put(entry.getKey(), entry.getValue());
		}
		return nearbys;
	}
	
	protected void checkContext(Location locale){
		Map<String, Location> nearbys = isNearby(locale);
		
		if(nearbys.size()>0){
			sendNotification();
		}
		
	}
	
	public void stop() {
		locationManager.removeUpdates(this);
		Log.v(contextName, "Stopping");
	}

}
