package uk.ac.tvu.mdse.contextengine.test;

import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.R;
import uk.ac.tvu.mdse.contextengine.R.id;
import uk.ac.tvu.mdse.contextengine.R.layout;

import android.content.ContentResolver;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContextAdapter extends ArrayAdapter<TestCon> {

    private ArrayList<TestCon> items;
    Context context;
    ContentResolver cr;
    private LayoutInflater mInflater;
    
    private static final int CACHE_CAPACITY = 30;
    
    public ContextAdapter(Context context, int textViewResourceId, ArrayList<TestCon> events) {
            super(context, textViewResourceId, events);
            this.items = events;
            this.context = context;
            cr = this.context.getContentResolver();
            mInflater = LayoutInflater.from(context);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {	
            
    		ViewHolder holder;
    		
            if (convertView == null) {
                //LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //convertView = vi.inflate(R.layout.manageeventrow, null);
            	convertView = mInflater.inflate(R.layout.testactivityrow, parent, false);
            	
            	//Create ViewHolder
            	holder = new ViewHolder();
            	
            	holder.text = (TextView) convertView.findViewById(R.id.manageeventsname);
            	holder.value = (TextView) convertView.findViewById(R.id.manageeventsaddress);
            	holder.date = (TextView) convertView.findViewById(R.id.manageeventsdate);
            	convertView.setTag(holder);
            }else{
            	holder = (ViewHolder) convertView.getTag();
            }
            
            TestCon e = items.get(position);
            if (e != null) {  
                
                    
                holder.text.setText(e.name.toString());                            
            	if(e.value.length()==0)
            		holder.value.setText("Nothing");
            	else
            		holder.value.setText(e.value);    
                
            	holder.date.setText("Date: "+ e.date);
            }
            return convertView;
    }
    
    static class ViewHolder {
    	
        TextView text;
        TextView value;
        TextView date ;
    }
    
    
  

}

