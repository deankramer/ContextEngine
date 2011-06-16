package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;

public class BluetoothContext extends MonitorComponent {

	private static final long serialVersionUID = -8852296839608708684L;
	BluetoothAdapter bluetoothAdapter;
	static NetworkInfo netInfo;


	public BluetoothContext(BluetoothAdapter ba, Context c) {
		super("BLUETOOTH", c, "android.bluetooth.adapter.action.STATE_CHANGED", "bluetoothAdapter.getState()" );	
		this.bluetoothAdapter = ba;
	}
	
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules
		//set contextValue	
		int bluetoothValue = bluetoothAdapter.getState();
		if (bluetoothValue == BluetoothAdapter.STATE_ON) {
			sendNotification("bluetoothON", true);
			sendNotification("bluetoothOFF", false);
		} else {
			sendNotification("bluetoothON", false);
			sendNotification("bluetoothOFF", true);
		}
		
		//send context value - 2nd approach
//		if ((bluetoothValue == BluetoothAdapter.STATE_ON)&&(!contextValue.equals("ON"))) {
//			contextValue = "ON";
//			sendNotification();
//		} 
//		if ((bluetoothValue == BluetoothAdapter.STATE_OFF)&&(!contextValue.equals("OFF"))) {
//			contextValue = "OFF";
//			sendNotification();
//		}		
	}
}
