package jp.aoyama.it.movesenseloggerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DataLoggerActivity extends AppCompatActivity {

    public static final String SERIAL = "serial";
    public static final String MAC_ADDRESS = "macAddress";
    private final static String TAG = "DataLoggerActivity";

    private String serial ="";
    private String macAddress="";

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
}