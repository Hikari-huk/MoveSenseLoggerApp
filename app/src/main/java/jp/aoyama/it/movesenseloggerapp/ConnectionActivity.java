package jp.aoyama.it.movesenseloggerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.movesense.mds.Mds;
import com.polidea.rxandroidble2.RxBleClient;

import java.util.ArrayList;

public class ConnectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ConnectionActivity";

    //UI
    private ListView mScanResultListView;
    private static ArrayList<MyScanResult> mScanResArrayList = new ArrayList();
    ArrayAdapter<MyScanResult> mScanResArrayAdapter;

    // MDS singleton
    static Mds mMds;

    // BleClient singleton
    static private RxBleClient mBleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanResultListView = (ListView)findViewById(R.id.listScanResult);
        mScanResArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mScanResArrayList);
        mScanResultListView.setAdapter(mScanResArrayAdapter);
        mScanResultListView.setOnItemClickListener(this);
        // Initialize Movesense MDS library
        initMds();
    }

    private void initMds() {
        if (mMds == null) {
            mMds = Mds.builder().build(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}