package jp.aoyama.it.movesenseloggerapp.activity02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.sax.Element;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.aoyama.it.movesenseloggerapp.R;








public class DataLoggerActivity extends AppCompatActivity {

    public static final String SERIAL = "serial";
    public static final String MAC_ADDRESS = "macAddress";
    private final static String TAG = "DataLoggerActivity";

    //movesense information
    private String serial ="";
    private String macAddress="";

    //DataLogger
    private DataLoggerState mDLState;//movesenseの状態
    private MdsLogbookEntriesResponse logBook;//movesense内の全てのログを格納


    //Logger Path URI
    private static final String URI_MDS_LOGBOOK_DATA= "suunto://MDS/Logbook/{0}/byId/{1}/Data";

    private static final String URI_LOGBOOK_ENTRIES = "suunto://{0}/Mem/Logbook/Entries";
    private static final String URI_DATA_LOGGER_STATE = "suunto://{0}/Mem/DataLogger/State";
    private static final String URI_DATA_LOGGER_CONFIG = "suunto://{0}/Mem/DataLogger/Config";

    private static final String URI_DATA_ACC_CONFIG = "/Meas/Acc/13";
    private static final String URI_DATA_GYRO_CONFIG = "/Meas/Gyro/13";

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
        tvLogState = findViewById(R.id.tvLogState);

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
        }else if(view.getId()==R.id.btnDelete){
            displayToast("Delete");
            deleteLog();
        }else if(view.getId()==R.id.btnFetchLog){
            displayToast("Fetch");
            fetchAllEntriesLog();

        }
    }

    private void createDataLoggerConfig(){
        String configUri = MessageFormat.format(URI_DATA_LOGGER_CONFIG, serial);

        DataLoggerConfig.DataEntry[] entries = {new DataLoggerConfig.DataEntry(URI_DATA_ACC_CONFIG), new DataLoggerConfig.DataEntry(URI_DATA_GYRO_CONFIG)};
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
        mDLState = new DataLoggerState(newState);
        tvLogState.setText(mDLState.toString());

        Mds.builder().build(this).put(stateUri, payload, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, "Put DataLogger/State state successful: "+data);

                if(!isStartLogging){
//                    fetchAllEntriesLog();
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


                Gson gson = new Gson();
                logBook = gson.fromJson(data, MdsLogbookEntriesResponse.class);

                Log.d(TAG,logBook.toString());
                for(MdsLogbookEntriesResponse.LogEntry e: logBook.logContent.logEntries){
                    Log.d(TAG,"Id:"+e.id+", Time:"+e.modificationTimestamp+", size:"+e.size);
                }
                readLog();

            }
            @Override
            public void onError(MdsException e) {

            }
        });
    }

    private void readLog(){
        Log.d(TAG, String.valueOf(logBook.logContent.logEntries.get(0).id));
        if(logBook != null) {
            String readLogUri = MessageFormat.format(URI_MDS_LOGBOOK_DATA, serial, logBook.logContent.logEntries.get(0).id);

            Mds.builder().build(this).get(readLogUri, null, new MdsResponseListener() {

                @Override
                public void onSuccess(String data) {

                    Log.d(TAG, "read :" + data);
                    Gson gson = new Gson();
                    LogData logData = gson.fromJson(data,LogData.class);
                    Log.d(TAG,"Java Object:"+logData.toString());

                }

                @Override
                public void onError(MdsException e) {

                }
            });
        }
    }

    private void deleteLog(){
        String deleteLogUri = MessageFormat.format(URI_LOGBOOK_ENTRIES,serial);

        Mds.builder().build(this).delete(deleteLogUri, null, new MdsResponseListener() {

            @Override
            public void onSuccess(String data){
                Log.d(TAG, "success delete");

            }
            @Override
            public void onError(MdsException e) {

            }
        });

    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
    }

    class MyData {
        @SerializedName("Content")
        Content content;

        @Override
        public String toString() {
            return "MyData{" +
                    "content=" + content +
                    '}';
        }
    }

    class Content {
        List<Element> elements;

        @Override
        public String toString() {
            return "Content{" +
                    "elements=" + elements +
                    '}';
        }
    }

    class Element {
        @SerializedName("Id")
        int id;

        @SerializedName("ModificationTimestamp")
        int modificationTimestamp;

        @SerializedName("Size")
        Integer size;

        @Override
        public String toString() {
            return "Element{" +
                    "id=" + id +
                    ", modificationTimestamp=" + modificationTimestamp +
                    ", size=" + size +
                    '}';
        }
    }

}

