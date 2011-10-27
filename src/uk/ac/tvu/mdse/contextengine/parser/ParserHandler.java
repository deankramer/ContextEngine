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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	//Context Range container
	private static final String CONTEXT_RANGE = "Range";
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
	
	private String currentElementValue;
	
	@Override
    public void startDocument() throws SAXException{
        
    }

    @Override
    public void endDocument() throws SAXException{

    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

    }
    
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
    	
    }
    
    @Override
    public void characters(char ch[], int start, int length){
    	if(currentElementValue==null)
    		currentElementValue = new String(ch, start, length);
    	else
    		currentElementValue = String.valueOf(ch, start, length);
    }

}
