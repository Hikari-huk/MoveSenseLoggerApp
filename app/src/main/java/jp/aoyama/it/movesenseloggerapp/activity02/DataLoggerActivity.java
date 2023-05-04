package jp.aoyama.it.movesenseloggerapp.activity02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.movesense.mds.Mds;

import java.text.MessageFormat;

import jp.aoyama.it.movesenseloggerapp.R;

public class DataLoggerActivity extends AppCompatActivity {

    public static final String SERIAL = "serial";
    public static final String MAC_ADDRESS = "macAddress";
    private final static String TAG = "DataLoggerActivity";
    private String serial ="";
    private String macAddress="";

    //Logger Path URI
    private static final String URI_MDS_LOGBOOK_ENTRIES = "suunto://MDS/Logbook/{0}/Entries";
    private static final String URI_MDS_LOGBOOK_DATA= "suunto://MDS/Logbook/{0}/ById/{1}/Data";

    private static final String URI_LOGBOOK_ENTRIES = "suunto://{0}/Mem/Logbook/Entries";
    private static final String URI_DATA_LOGGER_STATE = "suunto://{0}/Mem/DataLogger/State";
    private static final String URI_DATA_LOGGER_CONFIG = "suunto://{0}/Mem/DataLogger/Config";
    public static final String URI_EVENT_LISTENER = "suunto://MDS/EventListener";
    private static final String URI_LOGBOOK_DATA = "/Mem/Logbook/byId/{0}/Data";

    private static final String URI_DATA_ACC_CONFIG = "/Meas/Acc/13";

    //UI instance
    private TextView tvLogState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_logger);

        Intent intent = getIntent();
        serial = intent.getStringExtra(SERIAL);
        macAddress = intent.getStringExtra(MAC_ADDRESS);

        Log.d(TAG, serial);
        Log.d(TAG, macAddress);
    }

    public void onLogClickButton(View view){
        if(view.getId()==R.id.btnCreateLog) {
            displayToast("Create");
        }else if(view.getId()==R.id.btnStartLog){
            displayToast("Start");
        }else if(view.getId()==R.id.btnStopLog){
            displayToast("Stop");
        }
    }

    private void createDataLoggerConfig(){
        String configUri = MessageFormat.format(URI_DATA_LOGGER_CONFIG, serial);


    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
    }

}