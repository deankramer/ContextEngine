/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ContextEntity implements Serializable{


	//attributes
	public int id;
	public String name;
	public Calendar lastDateTime;
	public int count;
	public String value;	
	
	//constructors
	public ContextEntity(){	
		id=0;
		name="unknown";
		lastDateTime = Calendar.getInstance();
		count=0;
		value="unknown";
	}
	
	public ContextEntity(int anId, String aName, Calendar aDateTime, int aCount, String aValue){
		id=anId;
		name=aName;
		lastDateTime=aDateTime;
		count=aCount;
		value=aValue;
	}
	
	//methods
	@Override
	public String toString(){return name;}
	
	public String getDateToString(){
		StringBuffer date = new StringBuffer();
		date.append(lastDateTime.get(Calendar.DATE));
		date.append("-");
		date.append(lastDateTime.get(Calendar.MONTH)+1);
		date.append("-");
		date.append(lastDateTime.get(Calendar.YEAR));
		return date.toString();
	}
	
	public String getTimeToString(){
		int mMinute = lastDateTime.get(Calendar.MINUTE);
		int mHour = lastDateTime.get(Calendar.HOUR_OF_DAY);
		
		String min="";
		if(mMinute==0)
			min = Integer.toString(mMinute) + "0";
		else if(mMinute<10)
			min = "0" + Integer.toString(mMinute);
		else
			min = Integer.toString(mMinute);
		return mHour + ":" + min;
	}
	
	public String getDateTimeString(){
		StringBuffer date = new StringBuffer();
		date.append(lastDateTime.get(Calendar.DATE));
		date.append("-");
		date.append(lastDateTime.get(Calendar.MONTH)+1);
		date.append("-");
		date.append(lastDateTime.get(Calendar.YEAR));
		date.append(" ");
		date.append(lastDateTime.get(Calendar.HOUR_OF_DAY));
		date.append(":");
		date.append(lastDateTime.get(Calendar.MINUTE));
		date.append(":");
		date.append(lastDateTime.get(Calendar.SECOND));
		return date.toString();
	}
	
}
