package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Timer;
import java.util.TimerTask;

import uk.ac.tvu.mdse.contextengine.Component;
import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ExternalStorageSpaceContext extends Component {

	private static final long serialVersionUID = -4618383874590867306L;
	public static final String LOG_TAG = "ExternalStorageSpaceContext";
	public static final boolean D = true;
	private static final long interval = 1000; //in milliseconds
	private Timer intervalTimer;
	StatFs stat;
	
	public ExternalStorageSpaceContext(Context c){
		super("ExternalStorageSpaceContext", c);
		if (D) Log.d(LOG_TAG, "constructor");
		intervalTimer = new Timer();
		intervalTimer.schedule(new TimerTask() {
			@Override
			public void run(){
				checkContext();
			}
		}, interval);
		this.contextInformation = obtainContextInformation();
	}
	
	
	protected void checkContext() {
		if (D) Log.d(LOG_TAG, "check context");
		if(stat==null)
			stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		else
			stat.restat(Environment.getExternalStorageDirectory().getPath());
		long v = (long)stat.getAvailableBlocks() *(long)stat.getBlockSize();
		
		for (ContextValues cv: this.valuesSets){
			if (cv.setNewContextValue(v))
				sendNotification(cv);
		}    
	}
	
	protected String obtainContextInformation(){
		if (D) Log.d(LOG_TAG, "obtainContextInformation");
		checkContext();
		return  this.valuesSets.get(1).contextInformation;
	}
	
}
