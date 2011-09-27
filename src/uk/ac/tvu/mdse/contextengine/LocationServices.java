package uk.ac.tvu.mdse.contextengine;

import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.contexts.LocationContext;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class LocationServices implements LocationListener{
	
		private LocationManager locationManager;	
		private String provider;		
		private Location location;
		
		Context context;

		//	in real life you *DO NOT* want to do this, it may consume too many resources
	    // (time less than 60000ms for minTime is NOT recommended, used only for testing)
		private int minTime = 3000; //in milliseconds
		private int minDistance = 1000; //in meters	
		
		private ArrayList<LocationContext> locationContexts = new ArrayList<LocationContext>();

		public LocationServices(Context c) {
			
			this.context = c;
			
		    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		    
		    Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		    criteria.setAltitudeRequired(false);
		    criteria.setBearingRequired(false);
		    criteria.setCostAllowed(true);
		    criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		    provider = locationManager.getBestProvider(criteria, false);
		    Log.v("LocationServices", "LocationServices3 "+provider.toString());
		    
		    if (provider != null && locationManager.isProviderEnabled(provider)) {
		    	//set minTime(milliseconds) and minDistance(meters)
			    locationManager.requestLocationUpdates(provider, minTime, minDistance, this);   
				
				location = locationManager.getLastKnownLocation(provider);
				Log.v("constr-LocationServices", "Latitude= " + location.getLatitude() + " Longitude= " + location.getLongitude());
				Log.v("LocationServices", "Location accuracy: " + location.getAccuracy());
		    }
		    else{
		    	Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            c.startActivity(myIntent);
		    }
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
			
			//if best provider disabled, make GPS default
			provider = locationManager.GPS_PROVIDER;
			
			//check if GPS enabled, if not - ask for it
			if(!locationManager.isProviderEnabled(provider)){
				Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	context.startActivity(gpsOptionsIntent);
			}
		}

		public void onProviderEnabled(String prv) {
			Log.v("LocationServices", "Provider " + prv + " enabled");
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(provider, minTime, minDistance, this);   
			
			location = locationManager.getLastKnownLocation(provider);
			Log.v("provE-LocationServices", "Latitude= " + location.getLatitude() + " Longitude= " + location.getLongitude());
			Log.v("LocationServices", "Location accuracy: " + location.getAccuracy());
		}

		public void onStatusChanged(String prv, int stat, Bundle extras) {	
			if (stat == 2){
				locationManager.removeUpdates(this);
				locationManager.requestLocationUpdates(provider, minTime, minDistance, this);   
				
				location = locationManager.getLastKnownLocation(provider);
				Log.v("statC-LocationServices", "Latitude= " + location.getLatitude() + " Longitude= " + location.getLongitude());
				Log.v("LocationServices", "Location accuracy: " + location.getAccuracy());
			}
		}	
		
		public void stop() {
			locationManager.removeUpdates(this);
			Log.v("LocationServices", "Stopping");
		}
}
