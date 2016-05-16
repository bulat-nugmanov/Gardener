package com.example.Gardener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.Gardener.exception.ExistingItemException;
import com.example.Gardener.model.ScheduleManager;

/**
 * Activity used for simple schedule creation and editing
 */
public class EditActivity extends Activity {

    EditText nameField;            //name of the schedule being editing or operated
    TextView editMstField;         //text indicating moisture level selection
    NumberPicker freqPickerDaily;
    NumberPicker freqPickerWeekly;
    SeekBar mstLvlSeekBar;

    private int selectedFreqDaily;  //chosen frequency of hydration per day
    private int selectedFreqWeekly; //chosen frequency of schedules per week
    private int selectedMstLvl;     //target moisture level
    private Boolean editing;        //true if currently editing an existing schedule

    private static String D_TAG = "DialogFragment";
    private static int MAX_MST;
    private static int MIN_MST;
    private static int MAX_DFREQ;
    private static int MIN_DFREQ;

    /**
     * initializes values and performs first set up of all widgets
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        initializeValues();
        setUpTextFields();
        freqPickerDaily = (NumberPicker) findViewById(R.id.number_picker_edit_set_freq);
        setUpFreqPicker(freqPickerDaily, MIN_DFREQ, MAX_DFREQ, new DailyFrequencyValueChangeListener());
        freqPickerWeekly = (NumberPicker) findViewById(R.id.number_picker_edit_set_freq_weekly);
        setUpFreqPicker(freqPickerWeekly, 1, 7, new WeeklyFrequencyValueChangeListener());
        setUpMoistureLevelSeekBar();
        processIntent(getIntent());
    }

    /**
     * Obtains "constants" from resources and sets non-static primitive
     * fields to default values
     */
    private void initializeValues(){
        MAX_DFREQ = getResources().getInteger(R.integer.max_daily_freq);
        MIN_DFREQ = getResources().getInteger(R.integer.min_daily_freq);
        MAX_MST = getResources().getInteger(R.integer.max_mst_lvl);
        MIN_MST = getResources().getInteger(R.integer.min_mst_lvl);
        selectedFreqDaily = MIN_DFREQ;
        selectedFreqWeekly = 1;
        selectedMstLvl = (MAX_MST + MIN_MST) / 2;
        editing = false;
    }

    /**
     * update intent dependent fields
     */
    @Override
    public void onStart(){
        super.onStart();
        processIntent(getIntent());
    }

    /**
     * Extracts and manages selected schedule name and current editing state from passed intent
     * @param i: received intent
     */
    private void processIntent(Intent i){
        editing = i.getBooleanExtra(getResources().getString(R.string.key_edit_selected), false);
        if(editing){
            String selectedScheduleName = i.getStringExtra(getResources().getString(R.string.key_selected_schedule_name));
            nameField.setText(selectedScheduleName);
        } else {
            nameField.setText(getResources().getString(R.string.text_default_schedule_name));
        }
    }

    private void setUpMoistureLevelSeekBar(){
        mstLvlSeekBar = (SeekBar) findViewById(R.id.seekbar_moisture_level);
        mstLvlSeekBar.setMax(MAX_MST - MIN_MST);
        mstLvlSeekBar.setProgress(selectedMstLvl);
        mstLvlSeekBar.setOnSeekBarChangeListener(new MoistureLevelSeekBarListener());
    }

    private void setUpFreqPicker(NumberPicker picker, int min, int max, NumberPicker.OnValueChangeListener listener) {
        picker.setMaxValue(max);
        picker.setMinValue(min);
        picker.setWrapSelectorWheel(true);
        picker.setOnValueChangedListener(listener);
        int numberOfValues = max - min + 1;
        String[] values = new String[numberOfValues];
        int acc = min;
        for(int i = 0; i < values.length; i++){
            values[i] = Integer.toString(acc);
            acc++;
        }
        picker.setDisplayedValues(values);
    }

    private void setUpTextFields(){
        nameField = (EditText) findViewById(R.id.field_edit_name);
        nameField.setText(R.string.text_default_schedule_name);
        editMstField = (TextView) findViewById(R.id.text_edit_set_level);
    }

    /**
     * Called when the confirm button is pressed:
     * If editing a schedule, updates it as specified by current user selections.
     * Otherwise tries to create a schedule as specified:
     *      if no schedule with selected name exists, confirms changes and goes back to browse
     *      else prompts for a different name.
     */
    public void onConfirm (View view){
        Intent i = new Intent(this, BrowseActivity.class);
        if(editing){
            ScheduleManager.getInstance().
                    editDailyScheduleWithName(getEnteredName(), selectedFreqDaily, selectedMstLvl, null);
            startActivity(i);
        } else {
            try {
                ScheduleManager.getInstance().
                        createAndAddDailySchedule(getEnteredName(), selectedFreqDaily, selectedMstLvl, null);
                startActivity(i);
            } catch (ExistingItemException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.toast_existing_schedule),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private String getEnteredName(){
        return nameField.getText().toString();
    }

    public class MoistureLevelSeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            selectedMstLvl = i + MIN_MST;
            String newEditLvlText = getResources().getString(R.string.text_edit_set_level) + " " + Integer.toString(selectedMstLvl);
            editMstField.setText(newEditLvlText);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Listens for changes in the daily watering frequency picker widget
     */
    public class DailyFrequencyValueChangeListener implements NumberPicker.OnValueChangeListener{

        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            selectedFreqDaily = i1;
        }
    }

    /**
     * Listens for changes in the weekly watering frequency picker widget
     */
    public class WeeklyFrequencyValueChangeListener implements NumberPicker.OnValueChangeListener{

        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            selectedFreqWeekly = i1;
        }
    }
}
