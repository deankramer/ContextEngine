package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Calendar;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.util.Log;
import uk.ac.tvu.mdse.contextengine.Component;

public class BluetoothContext extends Component {

	
	private static final long serialVersionUID = -8852296839608708684L;
	static BluetoothAdapter bluetoothAdapter;
	static NetworkInfo netInfo;

	public BluetoothContext(BluetoothAdapter ba, Context c) {
		super("BLUETOOTH", c);
		bluetoothAdapter = ba;
		setupMonitor();	
	}

	private void setupMonitor() {
		
		contextMonitor = new BroadcastReceiver(){
			
			@Override
			public void onReceive(Context c, Intent in) {
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				int bluetoothValue = bluetoothAdapter.getState();
				if(bluetoothValue==BluetoothAdapter.STATE_ON){
					sendNotification("bluetoothON", true);
					sendNotification("bluetoothOFF", false);
				}else{
					sendNotification("bluetoothON", false);
					sendNotification("bluetoothOFF", true);
				}
			}};
			context.registerReceiver(contextMonitor, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
		
	}
	
	public void sendNotification(String name, boolean value){
		Intent intent = new Intent();
		
		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, name);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, value);
		try{
		    context.sendBroadcast(intent);
		}catch(Exception e){
		    Log.e(contextName,"not working");
		}
	} 


}
