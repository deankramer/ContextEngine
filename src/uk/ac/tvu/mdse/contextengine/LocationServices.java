package uk.ac.tvu.mdse.contextengine;

import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.contexts.LocationContext;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationServices implements LocationListener{
	
		private LocationManager locationManager;	
		private String provider;		
		private Location location;

		//	in real life you *DO NOT* want to do this, it may consume too many resources
	    // (time less than 60000ms for minTime is NOT recommended, used only for testing)
		private int minTime = 3000; //in milliseconds
		private int minDistance = 1000; //in meters	
		
		private ArrayList<LocationContext> locationContexts = new ArrayList<LocationContext>();

		public LocationServices(Context c) {			
		    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
//		    set minTime(milliseconds) and minDistance(meters)
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);   
			
			location = locationManager.getLastKnownLocation(provider);
			Log.v("LocationServices", "Latitude= " + location.getLatitude() + " Longitude= " + location.getLongitude());
			Log.v("LocationServices", "Location accuracy: " + location.getAccuracy());
		}
		
		public void setUpdatesCriteria(int time, int distance){
			if ((time<minTime)||(distance<minDistance)){
				this.minTime = time;
				this.minDistance = distance;
				locationManager.removeUpdates(this);
				locationManager.requestLocationUpdates(provider, minTime, minDistance, this); 
			}
		}
		
		public void addLocationContext(LocationContext locationContext){
			locationContexts.add(locationContext);
		}
		
		public Location getLocation(){
			return location;
		}
		
		public void onLocationChanged(Location locale) {
			for (LocationContext locationContext:locationContexts){
				locationContext.onLocationChanged(locale);
			}			
			this.location = locale;
		}

		public void onProviderDisabled(String prv) {
			Log.v("LocationServices", "Provider " + prv + " disabled");
		}

		public void onProviderEnabled(String prv) {
			Log.v("LocationServices", "Provider " + prv + " enabled");
		}

		public void onStatusChanged(String prv, int stat, Bundle extras) {	
		}	
		
		public void stop() {
			locationManager.removeUpdates(this);
			Log.v("LocationServices", "Stopping");
		}
}
