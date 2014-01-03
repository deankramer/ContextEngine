/*
 * Copyright (C) 2014 The Context Engine Project
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

import android.location.Location;
import android.util.Log;

public class LocationIdentifier {

	public static final String LOG_TAG = "LocationIdentifier";
	public static final boolean D = true;

	public String identifier;
	public Location location;
	public double latitude;
	public double longitude;

	public LocationIdentifier(String identifier, double latitude,
			double longitude) {
		if (D)
			Log.d(LOG_TAG, "constructor");

		this.identifier = identifier;
		this.latitude = latitude;
		this.longitude = longitude;

		location = new Location("");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
	}
}
