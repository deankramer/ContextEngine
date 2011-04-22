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
	
	public ContextDBSQLite(Context context){
		dbHelper = new OpenDbHelper(context);
	}

	
	public boolean addContext(ContextEntity c) {
		try{
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
			
			ContentValues initialValues = new ContentValues();
			
			initialValues.put("name", c.name);
			initialValues.put("lastDateTime", c.getDateTimeString());
			initialValues.put("count", c.count);
			
			sqlite.insert("contexts", null, initialValues);
			sqlite.close();
			
		}catch(Exception sqlerror){
			Log.v("Table insert error", sqlerror.getMessage());
			return false;
		}
		return true;
	}

	
	public ContextEntity getContext(int id) {
		ContextEntity context = new ContextEntity();
		try{
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery("Select * from contexts where _id="+id+";", null);
			crsr.moveToFirst();
			
			for (int i=0;i<crsr.getCount();i++){
				context.id = crsr.getInt(0);
				context.name = crsr.getString(1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Calendar cdate = Calendar.getInstance();
				cdate.setTime(dateFormat.parse(crsr.getString(2)));
				context.lastDateTime = cdate;
				context.count = crsr.getInt(3);
				crsr.moveToNext();
			}
			crsr.close();
			sqlite.close();
		}catch(Exception sqlerror){
			Log.v("Table insert error", sqlerror.getMessage());
		}
		return context;
		
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


	public boolean updateContext(ContextEntity c) {
		try{
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
			ContentValues initialValues = new ContentValues();
			
			initialValues.put("name", c.name);
			initialValues.put("lastDateTime", c.getDateTimeString());
			initialValues.put("count", c.count);
			
			sqlite.update("contexts", initialValues, "_id=?", new String[]{Integer.toString(c.id)});
			sqlite.close();
			return true;
			
		}catch(Exception sqlerror){
			Log.v("Table update error", sqlerror.getMessage());
			return false;
		}
	}
	

}
