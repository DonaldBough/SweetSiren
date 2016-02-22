package edu.purdue.dbough.sweetsignal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */

public class SugarEntryFragment extends ListFragment {
    public static ArrayList<SugarEntry> entryList = new ArrayList<>();
    EntryAdapter adapter;
    
    public static SugarEntryFragment newInstance() {
        SugarEntryFragment fragment = new SugarEntryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new EntryAdapter(getActivity(), R.layout.row_layout, SugarEntryFragment.entryList);
        setListAdapter(adapter);
        Context context = view.getContext();

        //Get data from getFilesDir/BloodSugarLevels.csv if existing
        File fileDir = new File(context.getFilesDir() + File.separator);
        if(!fileDir.exists()){
            try{
                fileDir.mkdir();
            } catch (Exception e) {
            }
        }
        File file = new File(fileDir + "BloodSugarLevels.csv");
        if(!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
            }
        }

        //Read csv values and generate rows
        String line;
        String[] values;
        int len = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        try {
            FileInputStream fis = new FileInputStream (new File(String.valueOf(file)));
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            ArrayList<SugarEntry> tempList = new ArrayList<>();

            //Assumes csv contains sugar,time,date
            while((line = r.readLine()) != null) {
                values = line.split(",");
                len = values.length;
                k = len - 1;
                j = len - 2;
                i = len - 3;
                for (int z = len -1; z >= 0; z-= 3) {
                    SugarEntry entry = new SugarEntry(values[i], values[j], values[k]);
                    tempList.add(entry);
                    i-=3;
                    j-=3;
                    k-=3;
                }
                entryList.clear();
                entryList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Problem Reading File", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    //Converts a list of events into rows of views to be put in the ListView
    private class EntryAdapter extends ArrayAdapter<SugarEntry> {

        private ArrayList<SugarEntry> values;

        public EntryAdapter(Context context, int resource, ArrayList<SugarEntry> entries) {
            super(context, resource, entries);
            values = entries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Get the inflater. This is used to convert xml layout files into an actual View
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //rowView represents one row in our ListView
            View rowView;

            if (convertView == null)
                rowView = inflater.inflate(R.layout.row_layout,parent,false);
            else
                rowView = convertView;

            //Setting text or whatever else we need in each row
            TextView textView = (TextView)rowView.findViewById(R.id.sugarLevel);
            textView.setText(values.get(position).getSugarLevel());

            TextView textView2 = (TextView)rowView.findViewById(R.id.time);
            textView2.setText(values.get(position).getTime());

            TextView textView3 = (TextView)rowView.findViewById(R.id.date);
            textView3.setText(values.get(position).getDate());

            return rowView;
        }
    }

}
