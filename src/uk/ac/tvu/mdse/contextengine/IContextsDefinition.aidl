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

package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.IRemoteServiceCallback;

//Interface 1 for context engine 
// Deals with registering of contexts

interface IContextsDefinition {
  void registerCallback(IRemoteServiceCallback cb);
  void newComposite(in String compositeName);
  void registerComponent(in String componentName);
  void addLocationComponent(in String key);
  void addLocation(in String key, in String identifier, in String latitude, in String longitude);
  void addToComposite(in String componentName, in String compositeName);  
  void startComposite(in String compositeName);
  void addRange(in String componentName, in String minValue, in String maxValue, in String contextValue);
  void addRule(in String componentName, in String[] condition, in String result);
  void unregisterCallback(IRemoteServiceCallback cb);
}

