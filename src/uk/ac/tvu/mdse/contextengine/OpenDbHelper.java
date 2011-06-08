/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static String DB_NAME = "contextDB";
	private static final String CONTEXTTABLE_CREATE = "create table contexts (_id integer primary key autoincrement, "
			+ "name text not null" + "lastDateTime text not null"
			// + "count integer not null"
			+ "value text not null" + ");";

	public OpenDbHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CONTEXTTABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
