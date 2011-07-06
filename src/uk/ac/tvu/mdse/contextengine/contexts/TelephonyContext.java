package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class TelephonyContext extends MonitorComponent {

	private boolean roaming = false;
	private int connection = 0;
	private static final long serialVersionUID = -6310232951343360172L;
	public TelephonyManager tm;

	public TelephonyContext(String name, Context c) {
		super("TELEPHONY", c, "android.net.conn.CONNECTIVITY_CHANGE");
		//checkContext();
		this.tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	protected void checkContext(Bundle data) {		
		checkRoaming();
		checkConnectionState();
	}

	private void checkConnectionState() {
		int v = tm.getDataState();
		if (v != connection){
			if(v == 2)
				sendNotification("telephonyConnectedON", true);
			else
				sendNotification("telephonyConnectedON", false);
			connection = v;
		}		
	}

	private void checkRoaming() {
		boolean r = tm.isNetworkRoaming();
		if (r && (!roaming)) {
			sendNotification("roamingON", true);
			roaming = true;
		} else if ((!r) && (roaming)) {
			sendNotification("roamingON", false);
			roaming = false;
		}
	}
}
