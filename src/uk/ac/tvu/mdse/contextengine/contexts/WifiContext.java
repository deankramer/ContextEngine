package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiContext extends MonitorComponent {

	private static final long serialVersionUID = -6833408704101539915L;
	private WifiManager wm;

	public WifiContext(WifiManager wm, Context c) {
		super("WIFI", c, "android.net.wifi.WIFI_STATE_CHANGED");
		this.wm = wm;		
		checkContext();
	}

	protected void checkContext() {
		Boolean wifiEnabled = wm.isWifiEnabled();
		if (wifiEnabled & (!contextValue)) {
			sendNotification("wifiON", true);
			contextValue = true;
		} else if ((!wifiEnabled) & (contextValue)) {
			sendNotification("wifiON", false);
			contextValue = false;
		}

		//send context value - 2nd approach
//		Boolean wifiEnabled = wm.isWifiEnabled();
//		if (wifiEnabled & (!contextValue.equals("ON"))) {			
//			contextValue = "ON";
//		} else if ((!wifiEnabled) & (!contextValue.equals("OFF"))) {			
//			contextValue = "OFF";
//		}
//		sendNotification();
	}


}
