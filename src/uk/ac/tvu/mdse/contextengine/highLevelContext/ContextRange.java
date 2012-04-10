/*
 * Copyright (C) 2011 The Context Engine Project
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

package uk.ac.tvu.mdse.contextengine.highLevelContext;

import android.util.Log;

public class ContextRange {
	
	public static final String LOG_TAG = "ContextRange";
	public static final boolean D = true;

	public long minValue;
	public long maxValue;
	public String contextHighValue;
	
	public ContextRange(long mv, long mxv, String value){
		if (D) Log.d(LOG_TAG, "constructor");
		this.minValue = mv;
		this.maxValue = mxv;
		this.contextHighValue = value;
	}
	
	public String getContextHighValue(long currentValue){
		if (D) Log.d(LOG_TAG, "getContextHighValue");
		return ((currentValue<maxValue)&&(currentValue>=minValue)) ? contextHighValue : null;
	}
}
