package jp.aoyama.it.movesenseloggerapp.activity02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.aoyama.it.movesenseloggerapp.R;
import jp.aoyama.it.movesenseloggerapp.activity01.ConnectedListMovesense;
import jp.aoyama.it.movesenseloggerapp.activity01.MovesenseModel;


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

    private Mds mMds;

    private static ArrayList<DataLoggerModel> movesenseList;
    private DataLoggerAdapter dataLoggerAdapter;

    //Logger Path URI
    private static final String URI_MDS_LOGBOOK_DATA= "suunto://MDS/Logbook/{0}/byId/{1}/Data";

    private static final String URI_LOGBOOK_ENTRIES = "suunto://{0}/Mem/Logbook/Entries";
    private static final String URI_DATA_LOGGER_STATE = "suunto://{0}/Mem/DataLogger/State";
    private static final String URI_DATA_LOGGER_CONFIG = "suunto://{0}/Mem/DataLogger/Config";

    private static final String URI_DATA_ACC_CONFIG = "/Meas/Acc/104";
    private static final String URI_DATA_GYRO_CONFIG = "/Meas/Gyro/104";

    private static final String URI_DATA_HR_CONFIG = "/Meas/Hr";

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

        mMds = Mds.builder().build(this);
        movesenseList = new ArrayList<>();

        if(ConnectedListMovesense.connectMovesenseList!=null){
            for(MovesenseModel m: ConnectedListMovesense.connectMovesenseList){
                movesenseList.add(new DataLoggerModel(m.getSerial(),m.getMacAddress()));
            }
        }else {
            finish();
        }
        RecyclerView dataLoggerRecyclerView = findViewById(R.id.dataloggerRecycleView);
        dataLoggerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration dividerItemDecorationDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dataLoggerRecyclerView.addItemDecoration(dividerItemDecorationDecoration);

        dataLoggerAdapter = new DataLoggerAdapter(movesenseList);
        dataLoggerRecyclerView.setAdapter(dataLoggerAdapter);
        Log.d(TAG, movesenseList.toString());
    }

    public void onLogClickButton(View view){
        if(view.getId()==R.id.btnStartLog){
            Log.d(TAG,movesenseList.toString());
        }
    }
//    public void onLogClickButton(View view){
//        if(view.getId()==R.id.btnCreateLog) {
//            displayToast("Create");
//            createDataLoggerConfig();
//        }else if(view.getId()==R.id.btnStartLog){
//            displayToast("Start");
//            setDataLoggerState(true);
//            btnStart.setVisibility(View.GONE);
//            btnStop.setVisibility(View.VISIBLE);
//        }else if(view.getId()==R.id.btnStopLog){
//            displayToast("Stop");
//            setDataLoggerState(false);
//            btnStart.setVisibility(View.VISIBLE);
//            btnStop.setVisibility(View.GONE);
//        }else if(view.getId()==R.id.btnDelete){
//            displayToast("Delete");
//            deleteLog();
//        }else if(view.getId()==R.id.btnFetchLog){
//            displayToast("Fetch");
//            fetchAllEntriesLog();
//            Log.d(TAG, "Fetch前"+String.valueOf(System.currentTimeMillis()));
//        }
//    }
//
//    private void createDataLoggerConfig(){
//        String configUri = MessageFormat.format(URI_DATA_LOGGER_CONFIG, serial);
//
//        DataLoggerConfig.DataEntry[] entries = {new DataLoggerConfig.DataEntry(URI_DATA_ACC_CONFIG),new DataLoggerConfig.DataEntry(URI_DATA_GYRO_CONFIG),new DataLoggerConfig.DataEntry(URI_DATA_HR_CONFIG)};
//        DataLoggerConfig config = new DataLoggerConfig(new DataLoggerConfig.Config(new DataLoggerConfig.DataEntries(entries)));
//        String jsonConfig = new Gson().toJson(config,DataLoggerConfig.class);
//
//        Log.d(TAG,jsonConfig);
//        mMds.put(configUri, jsonConfig, new MdsResponseListener() {
//
//            @Override
//            public void onSuccess(String data){
//                Log.d(TAG, data);
//            }
//
//            @Override
//            public void onError(MdsException e) {
//
//            }
//        });
//    }
//
//    private void setDataLoggerState(final boolean isStartLogging){
//        String stateUri = MessageFormat.format(URI_DATA_LOGGER_STATE, serial);
//        int newState = isStartLogging ? 3 : 2;
//        String payload = "{\"newState\":" + newState + "}";
//        mDLState = new DataLoggerState(newState);
//        tvLogState.setText(mDLState.toString());
//
//        mMds.put(stateUri, payload, new MdsResponseListener() {
//
//            @Override
//            public void onSuccess(String data){
//                Log.d(TAG, "Put DataLogger/State state successful: "+data);
//
//                if(!isStartLogging){
////                    fetchAllEntriesLog();
//                }
//
//            }
//
//            @Override
//            public void onError(MdsException e) {
//                Log.d(TAG,"set error");
//                if (e.getStatusCode()==423 && isStartLogging) {
//                    // Handle "LOCKED" from NAND variant
//                    new AlertDialog.Builder(getApplicationContext())
//                            .setTitle("DataLogger Error")
//                            .setMessage("Can't start logging due to error 'locked'. Possibly too low battery on the sensor.")
//                            .show();
//
//                }
//
//            }
//        });
//    }
//
//    private void fetchAllEntriesLog(){
//
//        String fetchUri = MessageFormat.format(URI_LOGBOOK_ENTRIES,serial);
//
//        mMds.get(fetchUri, null, new MdsResponseListener() {
//
//            @Override
//            public void onSuccess(String data){
//                Log.d(TAG, "Success fetch: "+data);
//
//                Gson gson = new Gson();
//                logBook = gson.fromJson(data, MdsLogbookEntriesResponse.class);
//
//                Log.d(TAG, "Fetch後:"+String.valueOf(System.currentTimeMillis()));
//                Log.d(TAG,logBook.toString());
//                for(MdsLogbookEntriesResponse.LogEntry e: logBook.logContent.logEntries){
//                    Log.d(TAG,"Id:"+e.id+", Time:"+e.modificationTimestamp+", size:"+e.size);
//                }
//
//                Log.d(TAG, "read前:"+String.valueOf(System.currentTimeMillis()));
//                readLog("test");
//
//            }
//            @Override
//            public void onError(MdsException e) {
//                Log.d(TAG,"fetch error");
//            }
//        });
//    }
//
//    private void readLog(String SerialAndSensorPosition){
//
//        // Get Current Timestamp in format suitable for file names (i.e. no : or other bad chars)
//        Date date = new Date();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        String currentTimestamp = formatter.format(date);
//        Context context = getApplicationContext();
//
//
//        Log.d(TAG, String.valueOf(logBook.logContent.logEntries.get(0).id));
//        if(logBook != null) {
//            String readLogUri = MessageFormat.format(URI_MDS_LOGBOOK_DATA, serial, logBook.logContent.logEntries.get(0).id);
//            Log.d(TAG,readLogUri);
//            mMds.get(readLogUri, null, new MdsResponseListener() {
//
//                @Override
//                public void onSuccess(String data) {
//
//                    Log.d(TAG, "read :" + data);
//                    Gson gson = new Gson();
//                    LogData logData = gson.fromJson(data,LogData.class);
//
//                    Log.d(TAG, "read後"+String.valueOf(System.currentTimeMillis()));
//                    Log.d(TAG,"Java Object:"+logData.toString());
//
//                    if(logData.meas.acc != null){
//                        StringBuilder sbAcc = new StringBuilder();
//                        sbAcc.append(currentTimestamp).append("_").append(SerialAndSensorPosition).append("_").append("acc").append(".csv");
//                        File fileAcc = new File(context.getFilesDir(),sbAcc.toString());
//                        CsvLogger csvLoggerAcc = new CsvLogger(fileAcc);
//                        csvLoggerAcc.appendHeader("SensorTime (ms),X (m/s^2),Y (m/s^2),Z (m/s^2)");
//
//                        for(LogData.Acc acc: logData.meas.acc){
//                            for(LogData.Data d: acc.arrayAcc){
//                                csvLoggerAcc.appendLine(String.format(Locale.getDefault(),"%d,%.6f,%.6f,%.6f",acc.timestamp,d.x,d.y,d.z));
//                            }
//                        }
//                        csvLoggerAcc.finishSavingLogs();
//                    }
//
//                    if(logData.meas.gyro != null){
//                        StringBuilder sbGyro = new StringBuilder();
//                        sbGyro.append(currentTimestamp).append("_").append(SerialAndSensorPosition).append("_").append("gyro").append(".csv");
//                        File fileGyro = new File(context.getFilesDir(),sbGyro.toString());
//                        CsvLogger csvLoggerGyro = new CsvLogger(fileGyro);
//                        csvLoggerGyro.appendHeader("SensorTime (ms),X (m/s^2),Y (m/s^2),Z (m/s^2)");
//                        for(LogData.Gyro gyro: logData.meas.gyro){
//                            for(LogData.Data d: gyro.arrayGyro){
//                                csvLoggerGyro.appendLine(String.format(Locale.getDefault(),"%d,%.6f,%.6f,%.6f",gyro.timestamp,d.x,d.y,d.z));
//                            }
//                        }
//                        csvLoggerGyro.finishSavingLogs();
//                    }
//
//                    if(logData.meas.hr != null){
//                        StringBuilder sbHr = new StringBuilder();
//                        sbHr.append(currentTimestamp).append("_").append(SerialAndSensorPosition).append("_").append("hr").append(".csv");
//                        File fileHr = new File(context.getFilesDir(),sbHr.toString());
//                        CsvLogger csvLoggerHr = new CsvLogger(fileHr);
//                        csvLoggerHr.appendHeader("HR Average,HR rrData");
//                        for(LogData.Hr hr: logData.meas.hr){
//                            csvLoggerHr.appendLine(String.format(Locale.getDefault(),"%.6f,%d",hr.average,hr.rrData.get(0)));
//                        }
//                        csvLoggerHr.finishSavingLogs();
//                    }
//
//                }
//
//                @Override
//                public void onError(MdsException e) {
//                    Log.d(TAG,"read error");
//                }
//            });
//        }
//    }
//
//    private void deleteLog(){
//        String deleteLogUri = MessageFormat.format(URI_LOGBOOK_ENTRIES,serial);
//
//        mMds.delete(deleteLogUri, null, new MdsResponseListener() {
//
//            @Override
//            public void onSuccess(String data){
//                Log.d(TAG, "success delete");
//
//            }
//            @Override
//            public void onError(MdsException e) {
//
//            }
//        });
//
//    }
//
//    private void getBatteryInfo(String serial){
//        mMds.get("suunto://"+serial+"/System/Energy/Level", null, new MdsResponseListener() {
//            @Override
//            public void onSuccess(String s) {
//                Log.d(TAG,s);
//                try {
//                    JSONObject jsonObject = new JSONObject(s);
//                    int batteryLevel = jsonObject.getInt("Content");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(MdsException e) {
//                Log.e(TAG, "Error getting battery info: " + e.getMessage());
//            }
//        });
//    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
    }

    public static void fetchAllEntriesLog(int position){
        Log.d(TAG, movesenseList.get(position).toString());
    }


}

