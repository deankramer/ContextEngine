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

//Interface 1 for context engine 
// Deals with registering of contexts

interface IContextsDefinition {

  //***setup contexts using xml***
  void setupContexts(String path);
  
  //***application registers its unique key***
  boolean registerApplicationKey(String key);
  
  //***add an atomic component***
  boolean registerComponent(in String componentName);
  
  //***add context values to a component***
  boolean addContextValues(in String componentName, in String[] contextValues);
  
  //***add a context value***
  boolean addContextValue(in String componentName, in String contextValue);
  
  //***add a specific context value described by two numeric coordinates (e.g.location)***
  //TO DO: void addSpecificContextValues(in String componentName, in String contextValue, sets of values);
  
  //***add a set of specific context values described by two numeric coordinates***
  void addSpecificContextValue(in String componentName, in String contextValue, in String numericData1, in String numericData2);  
    
  //***define higher context value - in case of numeric values specify range of values***  
  void addRange(in String componentName, in String minValue, in String maxValue, in String contextValue);  
    
  //***create a composite component***
  boolean newComposite(in String compositeName);
  
  //***add context to a composite context component
  boolean addToComposite(in String componentName, in String compositeName);  
 
  //***specify context value of the composite context based on values of context it is composed of***
  void addRule(in String componentName, in String[] condition, in String result);
  
   //***notify that the composite context has been fully identified***
  boolean startComposite(in String compositeName);   
  
  //***taken out and replaced - REMOVE once tested!***
  //void addLocationComponent(in String key);
 //void addLocation(in String key, in String identifier, in String latitude, in String longitude);
}


