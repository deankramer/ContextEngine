/*
 * Copyright (C) 2013 The Context Engine Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.tvu.mdse.contextengine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "contextDB";
	public static final String CONTEXTTABLE = "usable_contexts";
	private static final String CONTEXTTABLE_CREATE = "create table usable_contexts (_id integer primary key autoincrement, "
			+ "packagename text,"
			+ "name text,"
			+ "owner text,"
			+ "permission int not null," + "dex_file text);";

	public OpenDbHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CONTEXTTABLE_CREATE);
		insertStandardContexts(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	private void insertStandardContexts(SQLiteDatabase db) {
		db.execSQL("insert into usable_contexts values (1, 'uk.ac.tvu.mdse.contextengine.contexts', 'BatteryContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (2, 'uk.ac.tvu.mdse.contextengine.contexts', 'BluetoothContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (3, 'uk.ac.tvu.mdse.contextengine.contexts', 'ExternalStorageSpaceContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (4, 'uk.ac.tvu.mdse.contextengine.contexts', 'LightContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (5, 'uk.ac.tvu.mdse.contextengine.contexts', 'LocationContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (6, 'uk.ac.tvu.mdse.contextengine.contexts', 'TelephonyContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (7, 'uk.ac.tvu.mdse.contextengine.contexts', 'UserPreferenceContext', 'contextengine', 0, 'classes.dex')");
		db.execSQL("insert into usable_contexts values (8, 'uk.ac.tvu.mdse.contextengine.contexts', 'WifiContext', 'contextengine', 0, 'classes.dex')");

	}

}
