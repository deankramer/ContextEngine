package uk.ac.tvu.mdse.contextengine.contexts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import uk.ac.tvu.mdse.contextengine.Component;

public class BatteryContext extends Component{
	
	private static final double HIGH_BAT_VALUE = 80;
	private static final double MEDIUM_BAT_VALUE = 30;
	int v = -1;
	int value = 0;
	private static final long serialVersionUID = -7034400776638700530L;

	public BatteryContext(String name, Context c) {
		super("BATTERY", c);
		setupMonitor();
	}

	private void setupMonitor() {

		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				int rawlevel = in.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = in.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                
                if (rawlevel >= 0 && scale > 0) {
                    v = (rawlevel * 100) / scale;
                    checkContext();
                }	
				
			}

			
		};
		context.registerReceiver(contextMonitor, new IntentFilter(
		"Intent.ACTION_BATTERY_CHANGED"));
	}
	
	private void checkContext() {
		
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

