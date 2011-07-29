package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;

public class BatteryContext extends MonitorComponent{
	
	private static final double HIGH_BAT_VALUE = 80;
	private static final double MEDIUM_BAT_VALUE = 30;
	int v = -1;
	int value = 0;
	private static final long serialVersionUID = -7034400776638700530L;
	
	public BatteryContext(String name, Context c) {
		super("BATTERY_LEVEL", c, "Intent.ACTION_BATTERY_CHANGED", "BatteryManager.EXTRA_STATUS");		
		this.addRange(0, 30, "LOW");
		this.addRange(31, 80, "MEDIUM");
		this.addRange(81, 200, "HIGH");
		this.contextInformation = obtainContextInformation();
	}	
	
	protected String obtainContextInformation(){
		BatteryManager bm = new BatteryManager();
		
		int rawlevel = Integer.valueOf(bm.EXTRA_LEVEL);
        int scale = Integer.valueOf(bm.EXTRA_SCALE);
        
        if (rawlevel >= 0 && scale > 0) 
            v = (rawlevel * 100) / scale; 
        
        return this.getContextInformation(v);
	}
	
	protected void checkContext(Bundle data) {
		int rawlevel = data.getInt(BatteryManager.EXTRA_LEVEL, -1);
        int scale = data.getInt(BatteryManager.EXTRA_SCALE, -1);
        
        if (rawlevel >= 0 && scale > 0) {
            v = (rawlevel * 100) / scale;        
			if ((v >= HIGH_BAT_VALUE) && (value != 3)) {
				value = 3;
				sendNotification("batterylevelLOW", false);
				sendNotification("batterylevelMEDIUM", false);
				sendNotification("batterylevelHIGH", true);
			} else if ((v >= MEDIUM_BAT_VALUE) && (value != 2)) {
				value = 2;
				sendNotification("batterylevelLOW", false);
				sendNotification("batterylevelMEDIUM", true);
				sendNotification("batterylevelHIGH", false);
			} else if ((v < MEDIUM_BAT_VALUE) && (value != 1)) {
				value = 1;
				sendNotification("batterylevelLOW", true);
				sendNotification("batterylevelMEDIUM", false);
				sendNotification("batterylevelHIGH", false);
			}
        }
        
      //send context value - 2nd approach
		String highContext = this.getContextInformation(v);
		if ((highContext.equals("HIGH")) && (!contextInformation.equals("HIGH"))) {				
			contextInformation = "HIGH";
			sendNotification();
		} else if ((highContext.equals("MEDIUM")) && (!contextInformation.equals("MEDIUM"))) {
			contextInformation = "MEDIUM";
			sendNotification();
		} else if ((highContext.equals("LOW")) && (!contextInformation.equals("LOW"))) {				
			contextInformation = "LOW";
			sendNotification();
		}
	}	
}

