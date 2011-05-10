package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Calendar;

import uk.ac.tvu.mdse.contextengine.Component;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationContext extends Component implements LocationListener{
	
	
	public LocationManager locationManager;
	Criteria criteria;
	private static final long UPDATETIME = 3000;
	
	public LocationContext(LocationManager loc, Context c){
		super();
		Log.v("Status", "done super()");
		this.context = c;
		locationManager = loc;
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		Log.v("Status", "done criteria");
		String provider = locationManager.getBestProvider(criteria, true);
		this.contextEntity.value = locationManager.getLastKnownLocation(provider).toString();
		Log.v("Location", this.contextEntity.value);
		Log.v("Status", "done getLastKnownLocation");
		this.contextEntity.name = "location";
		this.contextEntity.lastDateTime = Calendar.getInstance();
		//this.contextEntity.value = "added value";
		sendNotification();
		//locationManager.requestLocationUpdates(provider, UPDATETIME, 1, this);
	}

	
	public void getContextValue() {
		// TODO Auto-generated method stub
		
	}

	public void onLocationChanged(android.location.Location location) {
		// TODO Auto-generated method stub
		
		this.contextEntity.value = location.toString();
		this.contextEntity.lastDateTime = Calendar.getInstance();
		sendNotification();
		
	}

	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
