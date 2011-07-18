package uk.ac.tvu.mdse.contextengine;

//Interface 2 for context engine 
// Deals with synchronous communication

interface ISynchronousCommunication {
  List<String> getContextList();
  boolean getContextValue(in String componentName);
}