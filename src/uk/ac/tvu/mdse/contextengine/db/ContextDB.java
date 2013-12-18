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

import java.util.List;

public interface ContextDB {

	List<String> getUsableContextList(String applicationId);

	List<String> getAppContextList(String applicationId);

	String getDexFile(String componentName);

	int getPermission(String componenName);

	String getPackageName(String componentName);

	boolean insertComponent(String packageName, String name, String owner,
			int permission, String dex_file);
	
	boolean removeComponent(String name, String owner);

	List<String> getLoadComponentInfo(String applicationId, String componentName);

	void closeDB();

}
