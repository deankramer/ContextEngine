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

package uk.ac.tvu.mdse.contextengine.parser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.tvu.mdse.contextengine.ContextEngine;

import android.util.Log;

public class ParserHandler extends DefaultHandler{
	
	//Unique application indentifier
	private static final String APP_ID = "AppKey";
	//Root for all context definitions
	private static final String CONTEXT_DEFINITION = "ContextDefinition";
	//Context container
	private static final String CONTEXT = "Context";
	//Name of Context
	private static final String CONTEXT_NAME = "ContextName";
	//Context Values
	private static final String CONTEXT_VALUE = "ContextValue";
	//Specific Context Values, for example for comparing locations
	private static final String SPECIFIC_CVALUE = "SpecificContextValue";
	//Name of Specific Context Value
	private static final String SPECIFIC_CNAME = "SpecificName";
	//Numeric value, part of specific context values
	private static final String NUMERIC_VALUE1 = "NumericValue1";
	//Second Numeric value, part of specific context values
	private static final String NUMERIC_VALUE2 = "NumericValue2";
	//Context Range container
	private static final String CONTEXT_RANGE = "Range";
	//Context Range Name
	private static final String CONTEXT_RANGE_NAME = "RangeName";
	//Minimum value in Context Range
	private static final String RANGE_MIN_VALUE = "Min";
	//Maximum value in Context Range
	private static final String RANGE_MAX_VALUE = "Max";
	//Composite Context container
	private static final String COMPOSITE_CONTEXT = "CompositeContext";
	//Context needed for the composite composition
	private static final String CONTEXT_CHILD = "ContextChild";
	//Value of the Child context
	private static final String CONTEXT_CHILD_VALUE = "ChildValue";
	//The value of the composite if rule is currect
	private static final String CONTEXT_PARENT_VALUE = "ParentValue";
	//Composite Context Rule container
	private static final String COMPOSITE_RULE = "Rule";
	
	private String appid;
	private String currentElementValue;
	private String currentElement;
	private String contextName;
	private XMLSCV scv;
	private XMLRange range;
	ArrayList<String> ruleCondition;
	String ruleParentValue;
	private ContextEngine ce;
	
	public boolean setContextEngine(ContextEngine aContextEngine){
		if(aContextEngine instanceof ContextEngine){
			this.ce = aContextEngine;
			return true;
		}
		return false;
		
	}
	
	@Override
    public void startDocument() throws SAXException{
		Log.v("ContextEngineParser", "Beginning XML Document");
       
    }

    @Override
    public void endDocument() throws SAXException{
    	Log.v("ContextEngineParser", "Finished XML Document");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    	
    	
    	if(localName.equals(CONTEXT_RANGE))
    		range = new XMLRange();
    	if(localName.equals(SPECIFIC_CVALUE))
    		scv = new XMLSCV();
    
    	currentElement = localName;
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
    	if(localName.equals(APP_ID))
    		appid = currentElementValue;
    	if(localName.equals(CONTEXT_DEFINITION))
    		Log.v("ContextEngine", "End of Contexts");
    	if(localName.equals(CONTEXT_NAME))
    		ce.newComponent(currentElementValue);
    	if(localName.equals(CONTEXT_RANGE_NAME))
    		range.name = currentElementValue;
    	if(localName.equals(RANGE_MAX_VALUE))
    		range.max = currentElementValue;
    	if(localName.equals(RANGE_MIN_VALUE))
    		range.min = currentElementValue;
    	if(localName.equals(CONTEXT_RANGE))
    		ce.newRange(contextName, range.min, range.max, range.name);
    	if(localName.equals(CONTEXT_CHILD))
    		ce.addToCompositeM(currentElementValue, contextName);
    	if(localName.equals(CONTEXT_CHILD_VALUE))
    		ruleCondition.add(currentElementValue);
    	if(localName.equals(CONTEXT_PARENT_VALUE))
    		ruleParentValue = currentElementValue;
    	if(localName.equals(COMPOSITE_RULE))
    		ce.newRule(contextName, (String[]) ruleCondition.toArray(), ruleParentValue);
    	if(localName.equals(COMPOSITE_CONTEXT))
    		ce.compositeReady(contextName);
    	if(localName.equals(SPECIFIC_CVALUE))
    		ce.newSpecificContextValue(contextName, scv.name, scv.value1, scv.value2);
    	if(localName.equals(SPECIFIC_CNAME))
    		scv.name = currentElementValue;
    	if(localName.equals(NUMERIC_VALUE1))
    		scv.value1 = currentElementValue;
    	if(localName.equals(NUMERIC_VALUE2))
    		scv.value2 = currentElementValue;
    	
    }
    
    @Override
    public void characters(char ch[], int start, int length){
    		currentElementValue = new String(ch, start, length);
    }

    static class XMLRange{
    	String name;
    	String min;
    	String max;
    }
    
    static class XMLSCV{
    	String name;
    	String value1;
    	String value2;
    }
}
