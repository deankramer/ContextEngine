package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import uk.ac.tvu.mdse.contextengine.Component;
import uk.ac.tvu.mdse.contextengine.LocationServices;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class LocationContext extends Component{

	private static final long serialVersionUID = -6360309106992426663L;
	
	// Each application listens only to particular location context determined by its key
	public static final String CONTEXT_LOCATION_KEY = "context_location_key";		
		
	public LocationServices locationServices;

	//application specific data
	public String key;

	//	in real life you *DO NOT* want to do this, it may consume too many resources
    // (time less than 60000ms for minTime is NOT recommended, used only for testing)
	private int minTime = 3000; //in milliseconds
	private int minDistance = 1000; //in meters
	private Map<String, Location> locationSet;	
	
	//What do we define as nearby (in meters)
	private float distancebetween = 1000; 

	public LocationContext(Context c, String identifier, LocationServices locServices) {
		super("LocationContext", c);		
		this.contextValue = false;		
		this.key = key;		
		this.locationServices = locServices;
		locationSet = getLocationSet();		  
	}
	
	public void setUpdatesCriteria(int time, int distance){
		this.minTime = time;
		this.minDistance = distance;
		locationServices.setUpdatesCriteria(minTime, minDistance);
	}
	
	public void setDistanceBetween(float distance){
		this.distancebetween = distance;
	}
	
	public void addLocation(String identifier, double latitude, double longitude ){
		Location location = new Location(identifier);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		locationSet.put(identifier, location);
	}
	
	public void addLocationSet(Map<String, Location> locSet){
		this.locationSet = locSet;
	}
	
	public Map<String, Location> getLocationSet(){
		return locationSet;
	}
	
	public Location getLastLocation(){
		return locationServices.getLocation();
	}

	public void onLocationChanged(Location locale) {
		checkContext(locale);
	}

/*	
	protected Map<String,Location> isNearby(Location locale){
		Map<String, Location> nearbys = new HashMap<String, Location>();
		for(Map.Entry<String, Location> entry: locationSet.entrySet()){
			if(location.distanceTo(entry.getValue()) <= distancebetween)
				nearbys.put(entry.getidentifier(), entry.getValue());
		}
		return nearbys;
	}
*/	
	protected ArrayList<String> isNearby(Location locale){
		//ArrayList<String> nearbys = new ArrayList<String>();
		String[] nearbys = new String[locationSet.size()];
		float [] distances = new float[locationSet.size()];
		Hashtable<Float, String> h = new Hashtable<Float, String>();
		int i=0;
		for(Map.Entry<String, Location> entry: locationSet.entrySet()){
			if(locale.distanceTo(entry.getValue()) <= distancebetween)
				//nearbys.add(entry.getKey());
				nearbys[i] = entry.getKey();
				distances[i] = locale.distanceTo(entry.getValue());
				h.put(distances[i],nearbys[i]);				
		}
		return sortHashtable(h);
	}
	
	protected ArrayList<String> sortHashtable(Hashtable<Float, String> h){
		Vector<Float> v = new Vector<Float>(h.keySet());
	    Collections.sort(v);
	    ArrayList<String> nearbyPlaces = new ArrayList<String>();
	    Iterator<Float> it;
	    it = v.iterator();
	    while (it.hasNext()) {
	    	nearbyPlaces.add((String)h.get(it.next()));
	    }
	    return nearbyPlaces;
	}
	
	protected void checkContext(Location locale){
		ArrayList<String> nearbys = isNearby(locale);
		
		if((! contextValue) && (nearbys.size()>0)){
			contextValue=true;
			sendNotification(nearbys);
		}
		else if((contextValue) && (nearbys.size()<1)){
			contextValue=false;
			sendNotification(nearbys);
		}
		
	}
/*	
	protected void checkContext(Location locale){
		Map<String, Location> nearbys = isNearby(locale);
		
		if(nearbys.size()>0){
			sendNotification();
		}
		
	}
	*/
	public void sendNotification(ArrayList<String> nearbys) {
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, contextName);
		intent.putExtra(CONTEXT_LOCATION_KEY, this.key);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, contextValue);
		intent.putExtra(CONTEXT_INFORMATION, nearbys);
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}
	
	public void stop() {
		Log.v(contextName, "Stopping");
	}

}
