package com.example.Gardener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.Gardener.model.AbstractSchedule;
import com.example.Gardener.model.ScheduleManager;
import com.example.Gardener.ui.ScheduleListFragment;
import com.example.Gardener.ui.ScheduleSelectionListener;
import com.example.Gardener.util.ArduinoConnectionService;
import com.example.Gardener.util.ScheduleDataParser;

public class BrowseActivity extends Activity implements ScheduleSelectionListener {

    private AbstractSchedule selectedSchedule;
    ScheduleListFragment listFragment;
    private Boolean editSelected; // true if edit, false if new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        listFragment = new ScheduleListFragment();
        listFragment.setRetainInstance(true);
        editSelected = false;
        getFragmentManager().beginTransaction()
                .add(R.id.schedules, listFragment)
                .commit();
    }

    @Override
    public void onScheduleListItemSelected(AbstractSchedule selected){
        this.selectedSchedule = selected;
    }

    /**
     * If selected schedule is not null: sets editing state to true,
     * puts selected schedule and editing state into intent, and starts the edit activity.
     * @param view: pressed view
     */
    public void onEditPressed(View view){
        if(selectedSchedule!=null) {
            setEditSelected(true);
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra(getString(R.string.key_edit_selected), editSelected);
            i.putExtra(getString(R.string.key_selected_schedule_name), selectedSchedule.getName());
            startActivity(i);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.toast_no_device_selected),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets editing state to false and starts the edit activity
     * @param view: pressed view
     */
    public void onCreatePressed(View view){
        setEditSelected(false);
        Intent i = new Intent(this, EditActivity.class);
        startActivity(i);
    }

    /**
     * If selected schedule is not null, deletes it from the schedule manager instance
     * @param view: pressed view
     */
    public void onDeletePressed(View view){
        if(selectedSchedule!=null) {
            ScheduleManager.getInstance().removeSchedule(selectedSchedule);
            listFragment.notifyAdapterOfChanges();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.toast_no_device_selected),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onUploadPressed(View view){
        if(selectedSchedule!=null) {
            Intent i = new Intent(this, ArduinoConnectionService.class);
            i.putExtra(getString(R.string.key_connection_intent), getString(R.string.msg_write));
            byte[] data = ScheduleDataParser.setMsg(selectedSchedule);
            i.putExtra(getString(R.string.key_data), data);
            startService(i);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.toast_no_device_selected),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setEditSelected(Boolean editSelected) {
        this.editSelected = editSelected;
    }
}
