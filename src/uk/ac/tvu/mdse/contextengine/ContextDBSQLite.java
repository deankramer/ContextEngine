/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContextDBSQLite implements ContextDB{
	
	private OpenDbHelper dbHelper;
	private Context context;
	
	public ContextDBSQLite(Context context){
		this.context = context;
		dbHelper = new OpenDbHelper(context);
	}

	
	public boolean addContext(Component c) {
		try{
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
			
			ContentValues initialValues = new ContentValues();
			
			initialValues.put("name", c.contextName);
			initialValues.put("lastDateTime", c.getDateTimeString());
			//initialValues.put("count", c.count);
			initialValues.put("value", Boolean.toString(c.contextValue));
			
			sqlite.insert("contexts", null, initialValues);
			sqlite.close();
			
		}catch(Exception sqlerror){
			Log.v("Table insert error", sqlerror.getMessage());
			return false;
		}
		return true;
	}

	
	public Component getContext(int id) {
		Component component = new Component("name", context);
		try{
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery("Select * from contexts where _id="+id+";", null);
			crsr.moveToFirst();
			
			for (int i=0;i<crsr.getCount();i++){
				component.contextId = crsr.getInt(0);
				component.contextName = crsr.getString(1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Calendar cdate = Calendar.getInstance();
				cdate.setTime(dateFormat.parse(crsr.getString(2)));
				component.contextDate = cdate;
				//context.count = crsr.getInt(3);
				component.contextValue = Boolean.getBoolean(crsr.getString(4));
				crsr.moveToNext();
			}
			crsr.close();
			sqlite.close();
		}catch(Exception sqlerror){
			Log.v("Table insert error", sqlerror.getMessage());
		}
		return component;
		
	}

	
	public boolean removeContext(int id) {
		try{
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
			sqlite.execSQL("delete from contexts where _id="+id+";");
			sqlite.close();
			return true;
		}catch(Exception sqlerror){
			Log.v("Table insert error", sqlerror.getMessage());
			return false;
		}
	}


	public boolean updateContext(Component c) {
		try{
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
			ContentValues initialValues = new ContentValues();
			
			initialValues.put("name", c.contextName);
			initialValues.put("lastDateTime", c.getDateTimeString());
			//initialValues.put("count", c.count);
			initialValues.put("value", Boolean.toString(c.contextValue));
			
			sqlite.update("contexts", initialValues, "_id=?", new String[]{Integer.toString(c.contextId)});
			sqlite.close();
			return true;
			
		}catch(Exception sqlerror){
			Log.v("Table update error", sqlerror.getMessage());
			return false;
		}
	}
	

}
