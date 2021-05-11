package com.patilparag96.cowinhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.patilparag96.cowinhelper.CowinApi.ApiClient;
import com.patilparag96.cowinhelper.CowinApi.models.CenterList;
import com.patilparag96.cowinhelper.utils.MapAdapter;
import com.patilparag96.cowinhelper.utils.MultiSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Spinner stateSpinner;
    private Spinner districtSpinner;
    private MultiSpinner vaccineSpinner;
    private Button submit;
    private Intent backgroundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initResources();

        addItemsOnStateSpinner();
        addItemsOnVaccineSpinner();

        addListenerOnStateSpinnerItemSelection();
        addListenerOnButton();
    }

    private void initResources(){
        stateSpinner = (Spinner) findViewById(R.id.spinner_state);
        districtSpinner = (Spinner) findViewById(R.id.spinner_district);
        vaccineSpinner = (MultiSpinner) findViewById(R.id.spinner_vaccine);
        submit = (Button) findViewById(R.id.start_notification);
        backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
    }

    public void addItemsOnStateSpinner() {
        BackgroundTask.submit("fetch_states_list", () -> ApiClient.fetchStateList().idToname());
        Map<String,String> states = BackgroundTask.get("fetch_states_list");

        MapAdapter<String, String> dataAdapter = new MapAdapter<>(this, android.R.layout.simple_spinner_item,states);
        stateSpinner.setAdapter(dataAdapter);
    }

    public void addItemsOnVaccineSpinner() {
        List<String> items = new ArrayList<>();
        for(CenterList.Vaccine entry: CenterList.Vaccine.values()){
            items.add(entry.name());
        }
        vaccineSpinner.setItems(items, "", null);
    }

    public void addListenerOnStateSpinnerItemSelection() {
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stateId = ((Map.Entry<String, String>)stateSpinner.getSelectedItem()).getKey();

                BackgroundTask.submit("fetch_district_list", () -> ApiClient.fetchDistrictList(stateId).idToName());
                Map<String,String> districts = BackgroundTask.get("fetch_district_list");


                MapAdapter<String, String> dataAdapter = new MapAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,districts);
                districtSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    public void addListenerOnButton() {
        submit.setOnClickListener(v -> {
            stopService(backgroundServiceIntent);

            String stateId = ((Map.Entry<String, String>)stateSpinner.getSelectedItem()).getKey();
            String districtId = ((Map.Entry<String, String>)districtSpinner.getSelectedItem()).getKey();
            String vaccine = (String) vaccineSpinner.getSelectedItem();

            SharedData data = new SharedData(stateId,districtId,vaccine);
            backgroundServiceIntent.putExtra("data", data);

            startService(backgroundServiceIntent);
        });
    }
}