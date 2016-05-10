package com.example.Gardener.ui;

import android.app.ListFragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.Gardener.R;
import com.example.Gardener.model.AbstractSchedule;
import com.example.Gardener.model.ScheduleManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Bulat on 2016-04-24.
 */
public class ScheduleListFragment extends ListFragment {

    private ScheduleListAdapter adapter;
    private View selectedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ScheduleListAdapter(ScheduleManager.getInstance().getSchedules());
        setListAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        notifyAdapterOfChanges();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.schedule_list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        setSelectedView(v);
        AbstractSchedule schedule = adapter.getItem(position);
        ((ScheduleSelectionListener) getActivity()).onScheduleListItemSelected(schedule);
    }

    private void setSelectedView(View v){
        updateSelectedViewBackground(v);
        selectedView = v;
    }

    public void updateSelectedViewBackground(View v){
        if(selectedView!=null){
            selectedView.setBackgroundColor(getResources().getColor(R.color.schedule_list_unselected_item));
            selectedView.invalidate();
        }
        v.setBackgroundColor(getResources().getColor(R.color.schedule_list_selected_item));
        v.invalidate();
    }

    public void notifyAdapterOfChanges(){
        adapter.notifyDataSetChanged();
    }

    private class ScheduleListAdapter extends ArrayAdapter<AbstractSchedule> {

        public ScheduleListAdapter(ArrayList<AbstractSchedule> schedules){
            super(getActivity(), 0, schedules);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.schedule_item, null);
            }

            AbstractSchedule schedule = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.schedule_name);
            name.setText(schedule.getName());
            return convertView;
        }

    }
}
