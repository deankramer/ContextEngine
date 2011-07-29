package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.IRemoteServiceCallback;

//Interface 1 for context engine 
// Deals with registering of contexts

interface IContextsDefinition {
  void registerCallback(IRemoteServiceCallback cb);
  void newComposite(in String compositeName);
  void registerComponent(in String componentName, in String compositeName);  
  void addRange(in String componentName, in String minValue, in String maxValue, in String contextValue);
  void addRule(in String componentName, in String[] condition, in String result);
  void unregisterCallback(IRemoteServiceCallback cb);
}

