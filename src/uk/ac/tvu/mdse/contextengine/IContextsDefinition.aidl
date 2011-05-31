package uk.ac.tvu.mdse.contextengine;

//Interface 1 for context engine 
// Deals with registering of contexts

interface IContextsDefinition {
  void newComposite(in String compositeName);
  void registerComponent(in String componentName, in String compositeName);  
}

