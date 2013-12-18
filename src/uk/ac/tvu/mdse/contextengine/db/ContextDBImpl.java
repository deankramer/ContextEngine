package uk.ac.tvu.mdse.contextengine.db;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContextDBImpl implements ContextDB {

	private OpenDbHelper dbHelper;
	public static final String CONTEXTTABLE = "usable_contexts";

	public ContextDBImpl(Context context) {
		dbHelper = new OpenDbHelper(context);
	}

	public void getDB(Context context) {
		dbHelper = new OpenDbHelper(context);
	}

	public void closeDB() {
		dbHelper.close();
	}

	@Override
	public List<String> getUsableContextList(String applicationId) {
		List<String> contexts = new Vector<String>();

		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery(
					"Select name, owner, permission from usable_contexts;",
					null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			for (int i = 0; numRows > 0; i++) {
				if (crsr.getString(1).equalsIgnoreCase(applicationId)) {
					contexts.add(crsr.getString(i));
				} else {
					if (crsr.getInt(3) == 0) {
						contexts.add(crsr.getString(i));
					}
				}
				crsr.moveToNext();
			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());

		}

		return contexts;
	}

	@Override
	public String getDexFile(String componentName) {
		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery(
					"Select dex_file from usable_contexts where name='"
							+ componentName + "';", null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			if (numRows > 0) {
				return crsr.getString(0);
			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());

		}

		return "";
	}

	@Override
	public int getPermission(String componentName) {
		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery(
					"Select permission from usable_contexts where name='"
							+ componentName + "';", null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			if (numRows > 0) {
				return crsr.getInt(0);
			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());

		}

		return 1;
	}

	@Override
	public boolean insertComponent(String packageName, String name,
			String owner, int permission, String dex_file) {
		try {
			SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

			ContentValues initialValues = new ContentValues();
			initialValues.put("packagename", packageName);
			initialValues.put("name", name);
			initialValues.put("owner", owner);
			initialValues.put("permission", permission);
			initialValues.put("dex_file", dex_file);
			sqlite.insert(CONTEXTTABLE, null, initialValues);
			sqlite.close();
			return true;

		} catch (Exception sqlerror) {
			Log.v("Table insert error", sqlerror.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean removeComponent(String name, String owner) {
		try {
			List<String> component = getLoadComponentInfo(owner, name);
			if (component.size() == 0 || component == null) {
				return false;
			} else {
				SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
				
				sqlite.delete(CONTEXTTABLE, "name = ?", new String[] {name});
				return true;
			}
				
		} catch(Exception sqlerror) {
			Log.e("Error" , sqlerror.getMessage());
			return false;
		}
		
	}

	@Override
	public String getPackageName(String componentName) {
		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery(
					"Select packageName from usable_contexts where name='"
							+ componentName + "';", null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			if (numRows > 0) {
				return crsr.getString(0);
			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());

		}

		return "";
	}

	@Override
	public List<String> getLoadComponentInfo(String applicationId,
			String componentName) {
		List<String> returnValues = new Vector<String>();
		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite
					.rawQuery(
							"Select dex_file, packageName, owner, permission from usable_contexts where name='"
									+ componentName + "';", null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			if (numRows > 0) {
				if (crsr.getString(2).equalsIgnoreCase(applicationId)) {
					returnValues.add(crsr.getString(0));
					returnValues.add(crsr.getString(1));
				} else {
					if (crsr.getInt(3) == 0) {
						returnValues.add(crsr.getString(0));
						returnValues.add(crsr.getString(1));
					}
				}

			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());
			return null;
		}

		return returnValues;
	}

	@Override
	public List<String> getAppContextList(String applicationId) {
		List<String> contexts = new Vector<String>();

		try {
			SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
			Cursor crsr = sqlite.rawQuery(
					"Select name from usable_contexts where owner ='"
							+ applicationId + "';", null);
			crsr.moveToFirst();

			int numRows = crsr.getCount();
			for (int i = 0; numRows > 0; i++) {
				contexts.add(crsr.getString(i));
				crsr.moveToNext();
			}

		} catch (Exception sqlerror) {
			Log.v("Table read error", sqlerror.getMessage());

		}

		return contexts;
	}

	

}
