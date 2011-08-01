package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class BluetoothContext extends MonitorComponent {

	private static final long serialVersionUID = -8852296839608708684L;
	BluetoothAdapter bluetoothAdapter;
	static NetworkInfo netInfo;


	public BluetoothContext(Context c) {
		super("BluetoothContext", c, "android.bluetooth.adapter.action.STATE_CHANGED", "bluetoothAdapter.getState()" );	
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.contextInformation = obtainContextInformation();
		//this.contextInformation = "ON";
		Log.d("BluetoothContext", this.contextInformation);
	}
	
	protected String obtainContextInformation(){
		int bluetoothValue = bluetoothAdapter.getState();
		if (bluetoothValue == BluetoothAdapter.STATE_ON) 
			return "ON";
		else
			return "OFF";
	}
	
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules
		//set contextValue	
		int bluetoothValue = bluetoothAdapter.getState();
//		if (bluetoothValue == BluetoothAdapter.STATE_ON) {
//			sendNotification("bluetoothON", true);
//			sendNotification("bluetoothOFF", false);
//		} else {
//			sendNotification("bluetoothON", false);
//			sendNotification("bluetoothOFF", true);
//		}
		
		//send context information - 2nd approach
		if ((bluetoothValue == BluetoothAdapter.STATE_ON)&&(!contextInformation.equals("ON"))) {
			contextInformation = "ON";
			sendNotification();
		} 
		if ((bluetoothValue == BluetoothAdapter.STATE_OFF)&&(!contextInformation.equals("OFF"))) {
			contextInformation = "OFF";
			sendNotification();
		}		
	}
}
