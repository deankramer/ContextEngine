package uk.ac.tvu.mdse.contextengine.contexts;

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
	
	public LocationContext(String name, Context c) {
		super(name, c);
		// Get the location manager
				locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
				// Define the criteria how to select the locatioin provider -> use
				// default
				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, false);
				location = locationManager.getLastKnownLocation(provider);
		// TODO Auto-generated constructor stub
	}

	public void onLocationChanged(Location locale) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String prv) {
		// TODO Auto-generated method stub
		Log.v(contextName, "Provider " + prv + " disabled");
	}

	public void onProviderEnabled(String prv) {
		// TODO Auto-generated method stub
		Log.v(contextName, "Provider " + prv + " enabled");
	}

	public void onStatusChanged(String prv, int stat, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		locationManager.removeUpdates(this);
		Log.v(contextName, "Stopping");
	}

}
