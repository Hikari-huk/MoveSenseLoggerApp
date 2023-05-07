package jp.aoyama.it.movesenseloggerapp.activity02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsResponseListener;

import java.text.MessageFormat;

import jp.aoyama.it.movesenseloggerapp.R;

public class DataLoggerActivity extends AppCompatActivity {

    public static final String SERIAL = "serial";
    public static final String MAC_ADDRESS = "macAddress";
    private final static String TAG = "DataLoggerActivity";

    //movesense information
    private String serial ="";
    private String macAddress="";

    //DataLogger
    private DataLoggerState mDLState;

    //Logger Path URI
    private static final String URI_MDS_LOGBOOK_DATA= "suunto://MDS/Logbook/{0}/byId/{1}/Data";

    private static final String URI_LOGBOOK_ENTRIES = "suunto://{0}/Mem/Logbook/Entries";
    private static final String URI_DATA_LOGGER_STATE = "suunto://{0}/Mem/DataLogger/State";
    private static final String URI_DATA_LOGGER_CONFIG = "suunto://{0}/Mem/DataLogger/Config";

    private static final String URI_DATA_ACC_CONFIG = "/Meas/Acc/13";

    //UI instance
    private TextView tvLogState;
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_logger);

        btnStart = findViewById(R.id.btnStartLog);
        btnStop = findViewById(R.id.btnStopLog);

        Intent intent = getIntent();
        serial = intent.getStringExtra(SERIAL);
        macAddress = intent.getStringExtra(MAC_ADDRESS);

        Log.d(TAG, serial);
        Log.d(TAG, macAddress);
    }

    public void onLogClickButton(View view){
        if(view.getId()==R.id.btnCreateLog) {
            displayToast("Create");
            createDataLoggerConfig();
        }else if(view.getId()==R.id.btnStartLog){
            displayToast("Start");
            setDataLoggerState(true);
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        }else if(view.getId()==R.id.btnStopLog){
            displayToast("Stop");
            setDataLoggerState(false);
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        }
    }

    private void createDataLoggerConfig(){
        String configUri = MessageFormat.format(URI_DATA_LOGGER_CONFIG, serial);

        DataLoggerConfig.DataEntry[] entries = {new DataLoggerConfig.DataEntry(URI_DATA_ACC_CONFIG)};
        DataLoggerConfig config = new DataLoggerConfig(new DataLoggerConfig.Config(new DataLoggerConfig.DataEntries(entries)));
        String jsonConfig = new Gson().toJson(config,DataLoggerConfig.class);

        Log.d(TAG,jsonConfig);
        Mds.builder().build(this).put(configUri, jsonConfig, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, data);
            }

            @Override
            public void onError(MdsException e) {

            }
        });
    }

    private void setDataLoggerState(final boolean isStartLogging){
        String stateUri = MessageFormat.format(URI_DATA_LOGGER_STATE, serial);
        int newState = isStartLogging ? 3 : 2;
        String payload = "{\"newState\":" + newState + "}";

        Mds.builder().build(this).put(stateUri, payload, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, "Put DataLogger/State state successful: "+data);

                if(!isStartLogging){
                    fetchAllEntriesLog();
                }

            }

            @Override
            public void onError(MdsException e) {

                if (e.getStatusCode()==423 && isStartLogging) {
                    // Handle "LOCKED" from NAND variant
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("DataLogger Error")
                            .setMessage("Can't start logging due to error 'locked'. Possibly too low battery on the sensor.")
                            .show();

                }

            }
        });
    }

    private void fetchAllEntriesLog(){
        String fetchUri = MessageFormat.format(URI_LOGBOOK_ENTRIES,serial);

        Mds.builder().build(this).get(fetchUri, null, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, "Success fetch: "+data);

                readLogData();
            }
            @Override
            public void onError(MdsException e) {

            }
        });
    }

    private void readLogData(){
        String readLogUri = MessageFormat.format(URI_MDS_LOGBOOK_DATA,serial,342);

        Mds.builder().build(this).get(readLogUri, null, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, data);
            }
            @Override
            public void onError(MdsException e) {

            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
    }

}