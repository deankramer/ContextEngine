package uk.ac.tvu.mdse.contextengine.test;

/**
 * @project ContextEngine
 * @date 26 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

import uk.ac.tvu.mdse.contextengine.IContextsDefinition;
import uk.ac.tvu.mdse.contextengine.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {

	private IContextsDefinition contextService;
	private boolean started = false;
	private boolean mIsBound = false;

	BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

	// private BroadcastReceiver contextMonitor;
	// private IntentFilter filter;
	// private LightContext lightcontext;
	// private CompositeComponent sync;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		// String[] menu = new String[] {
		// / "Testing Context"
		// };
		// setListAdapter(new ArrayAdapter<String>(this, R.layout.main, menu));
		// filter = new
		// IntentFilter("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED");

		// SensorManager sm = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// onnectivityManager wifi = (ConnectivityManager)
		// getSystemService(Context.CONNECTIVITY_SERVICE);
		// wifi = new ConnectionContext(wifi, getApplicationContext());
		// filter = new
		// IntentFilter("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
		// setupContextMonitor();

		// lightcontext = new LightContext(sm, getApplicationContext());
		// sync= new CompositeComponent("datasync_ON", getApplicationContext());
		// sync.registerComponent("lightlevelHIGH");
		// StringBuilder builder = new StringBuilder();
		//
		// builder.append("\n"+ sp.getBoolean("Main_Option",false));
		// builder.append("\n"+ sp.getString("listPref","-1"));
		// builder.append("\n"+ sp.getBoolean("Advanced_Option",false));
		// builder.append("\n"+ sp.getInt("customPref",-1));
		// builder.append("\n"+ sp.getString("pref_dialog","NULL"));
		//
		// TextView view = (TextView)findViewById(R.id.viewbox);
		// view.setText(builder.toString());
		//
		// LocationManager locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// locationContext = new LocationContext(locationManager,
		// this.getApplicationContext());

	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.

			try {
				// contextService =
				// ((ContextEngine.LocalBinder)service).getService();
				contextService = IContextsDefinition.Stub.asInterface(service);
//				try {
//
//					contextService.newComposite("datasync_ON");
//					contextService.registerComponent("datasync_ON",
//							"lightlevelHIGH");
//				} catch (RemoteException e) {
//
//					e.printStackTrace();
//				}

			} catch (NotFoundException e) {

				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			Log.d("value", "onServiceDisconnected");
			contextService = null;
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).

		bindService(new Intent(IContextsDefinition.class.getName()),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {

		unbindService(mConnection);
		// bindService(new Intent(IContextsDefinition.class.getName()),
		// mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = false;
	}

	// private void setupContextMonitor() {
	// Log.v("value", "add contextMonitor");
	// contextMonitor = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context,Intent intent) {
	// if
	// (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED"))
	// {
	// Bundle bundle = intent.getExtras();
	// String changeName = bundle.getString(Component.CONTEXT_NAME);
	// boolean currentcontext = bundle.getBoolean(Component.CONTEXT_VALUE);
	// if(changeName.equalsIgnoreCase("datasync_ON") &&( currentcontext ) )
	// getListView().setBackgroundResource(color.black);
	// else if (changeName.equalsIgnoreCase("datasync_ON") &&( !currentcontext )
	// )
	// getListView().setBackgroundResource(color.white);
	// }
	// }
	//
	// };
	// registerReceiver(contextMonitor, filter);
	// }
	@Override
	public void onStop() {
		// lightcontext.stop();
		// sync.stop();
		// unregisterReceiver(contextMonitor);
		super.onStop();
	}

	// update message
	private void updateServiceStatus() {
		String startStatus = started ? "started" : "not started";
		String statusText = "Server status: " + startStatus;
		TextView t = (TextView) findViewById(R.id.servicestatus);
		t.setText(statusText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// super.onCreateOptionsMenu(menu);
		MenuItem i = menu.add(1, 1, 1, "Preferences");
		i.setIcon(android.R.drawable.ic_menu_preferences);
		// CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent preferencesIntent = new Intent(TestActivity.this,
				TestPreferenceActivity.class);
		startActivity(preferencesIntent);
		return true;
	}

	public void startService(View v) {
		if (started) {
			Toast.makeText(TestActivity.this, "Service already started",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				Intent i = new Intent();
				i.setClassName("uk.ac.tvu.mdse.contextengine",
						"uk.ac.tvu.mdse.contextengine.ContextEngine");
				startService(i);
				started = true;
				updateServiceStatus();
				doBindService();
			} catch (Exception e) {
				Toast.makeText(TestActivity.this, "Error is " + e,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void stopService(View v) {
		if (!started) {
			Toast.makeText(TestActivity.this, "Service not yet started",
					Toast.LENGTH_SHORT).show();
		} else {

			try {
				Intent i = new Intent();
				i.setClassName("uk.ac.tvu.mdse.contextengine",
						"uk.ac.tvu.mdse.contextengine.ContextEngine");
				stopService(i);

				started = false;
				updateServiceStatus();
				doUnbindService();
			} catch (Exception e) {
				Toast.makeText(TestActivity.this, "Error is " + e,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void bluetoothChange(View v) {
		try {
			if (bt.isEnabled()) {
				bt.disable();
				// bluetooth.setText(String.valueOf(bt.getState()));
			} else {
				bt.enable();
				// bluetooth.setText(String.valueOf(bt.getState()));
			}

		} catch (Exception e) {
			Toast.makeText(TestActivity.this, "Error is " + e,
					Toast.LENGTH_LONG).show();
		}
	}
}
