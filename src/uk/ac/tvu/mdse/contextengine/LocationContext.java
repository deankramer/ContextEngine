package uk.ac.tvu.mdse.contextengine;

import java.util.Calendar;

import android.app.Activity;
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
	
	LocationContext(LocationManager loc){
		super();
		Log.v("Status", "done super()");
		
		locationManager = loc;
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		Log.v("Status", "done criteria");
		String provider = locationManager.getBestProvider(criteria, true);
		//this.contextEntity.value = locationManager.getLastKnownLocation(provider).toString();
		Log.v("Status", "done getLastKnownLocation");
		this.contextEntity.name = "moo";
		this.contextEntity.lastDateTime = Calendar.getInstance();
		this.contextEntity.value = "added value";
		sendNotification();
		//locationManager.requestLocationUpdates(provider, UPDATETIME, 1, this);
	}

	@Override
	public void getContextValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(android.location.Location location) {
		// TODO Auto-generated method stub
		
		this.contextEntity.value = location.toString();
		sendNotification();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
