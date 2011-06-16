package uk.ac.tvu.mdse.contextengine.contexts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import uk.ac.tvu.mdse.contextengine.Component;

public class WifiContext extends Component {

	private static final long serialVersionUID = -6833408704101539915L;
	private WifiManager wm;

	public WifiContext(WifiManager wm, Context c) {
		super("WIFI", c);
		this.wm = wm;
		setupMonitor();
		checkContext();
	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				checkContext();
			}
		};
		context.registerReceiver(contextMonitor, new IntentFilter(
				"android.net.wifi.WIFI_STATE_CHANGED"));
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

	}


}
