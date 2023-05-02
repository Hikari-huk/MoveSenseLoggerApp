package jp.aoyama.it.movesenseloggerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.MessageFormat;

public class DataLoggerActivity extends AppCompatActivity {

    public static final String SERIAL = "serial";
    public static final String MAC_ADDRESS = "macAddress";
    private final static String TAG = "DataLoggerActivity";
    private String serial ="";
    private String macAddress="";

    //Logger Path URI
    private static final String URI_DATA_LOGGER_CONFIG = "suunto://{0}/Mem/DataLogger/Config";

    private static final String URI_DATA_ACC_CONFIG = "/Meas/Acc/13";

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
        if(view.getId()==R.id.btnLogStart){
            displayToast("Start");
        }else if(view.getId()==R.id.btnLogStop){
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