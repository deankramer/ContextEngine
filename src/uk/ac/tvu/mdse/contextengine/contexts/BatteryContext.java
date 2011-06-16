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
	}
	
	
}

