package jp.aoyama.it.movesenseloggerapp.activity01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsConnectionListener;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.aoyama.it.movesenseloggerapp.activity02.DataLoggerActivity;
import jp.aoyama.it.movesenseloggerapp.R;
import kotlinx.coroutines.ThreadContextElement;

public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = "ConnectionActivity";

    private static final int REQUEST_CODE_BLUETOOTH_SCAN = 2;

    // MDS
    private Mds mMds;

    //UI
    private RecyclerView movesenseRecyclerView;
    private Button btnConnect;
    private FloatingActionButton floatingActionButton;


    //movesense
    private MovesenseAdapter movesenseAdapter;
    private ArrayList<MovesenseModel> movesenseModelArrayList;

    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        // MDS オブジェクトの初期化
        mMds = Mds.builder().build(this);

        movesenseRecyclerView = findViewById(R.id.movesenseRecycleView);
        movesenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration dividerItemDecorationDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        movesenseRecyclerView.addItemDecoration(dividerItemDecorationDecoration);

        //UI
        btnConnect = findViewById(R.id.btnConnect);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // MDS オブジェクトの初期化
        mMds = Mds.builder().build(this);
        if(ConnectedListMovesense.connectMovesenseList!=null){
            for(MovesenseModel m: ConnectedListMovesense.connectMovesenseList){
                mMds.disconnect(m.getMacAddress());
            }
        }
        if(ConnectedListMovesense.connectMovesenseList!=null){
            Log.d(TAG,"not null!!");
        }
        ConnectedListMovesense.connectMovesenseList = new ArrayList<>();
        btnConnect.setEnabled(false);
        floatingActionButton.setEnabled(false);
        resetRecyclerView();
    }

    private void resetRecyclerView(){
        movesenseModelArrayList = new ArrayList<>();
        movesenseAdapter = new MovesenseAdapter(movesenseModelArrayList);
        movesenseRecyclerView.setAdapter(movesenseAdapter);

    }

    public void onScanButtonClick(View view) throws InterruptedException {
        if(view.getId()==R.id.btnScan){
            resetRecyclerView();
            scanBluetoothDevices();
        }
    }

    private void scanBluetoothDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 検出された端末情報を受信するための、BroadcastReceiver を登録
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        // 端末の検出を開始
        // 端末の検出を開始
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);

        }
        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "Discovering");
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        showToast("デバイスを探しています...");
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "接続");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(ConnectionActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                String deviceName = device.getName(); // Device name
                String deviceHardwareAddress = device.getAddress(); // MAC address

                // Movesense が見つかったときの処理
                if (deviceName != null && deviceName.contains("Movesense")) {
                    showToast("Movesenseが見つかりました");

                    MovesenseModel movesenseModel = new MovesenseModel(deviceName.split(" ")[1], deviceHardwareAddress);
                    if(!movesenseModelArrayList.contains(movesenseModel)){
                        movesenseAdapter.add(movesenseModel);
                    }
                    movesenseAdapter.notifyDataSetChanged();
                    btnConnect.setEnabled(true);//接続を可能にする
                }
            }
        }
    };

    public void onClickConnectButton(View view) throws InterruptedException {
        Log.d(TAG,"ClickConnect:"+movesenseModelArrayList.toString());
        if(movesenseModelArrayList != null){
            for (MovesenseModel m: movesenseModelArrayList) {
                Log.d(TAG, String.valueOf(m.isConnect));
                if(m.isConnect){
                    connectMovesense(m);
                }
            }
        }
    }

    private void connectMovesense(MovesenseModel m) {
        if(ConnectedListMovesense.connectMovesenseList == null) ConnectedListMovesense.connectMovesenseList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }


        Log.d(TAG, "movesenseModel:"+m.toString());

        // Movesense の MAC address と MdsConnectionListener を引数にして connect する
        mMds.connect(m.getMacAddress(), new MdsConnectionListener() {
            @Override
            public void onConnect(String macAddress) {
                Log.d(TAG, "onConnect: " + macAddress);
            }
            // onConnect から数秒間して onConnectionComplete が呼ばれる
            @Override
            public void onConnectionComplete(String macAddress, String serial) {
                Log.d(TAG, "onConnectionComplete: " + macAddress);
                showToast("Connection Complete\nserial:" + serial+"\nmacAddress:"+macAddress);
                //isConnectとBatteryLevelは最初からはめとく
                ConnectedListMovesense.connectMovesenseList.add(new MovesenseModel(serial,macAddress));
                Log.d(TAG, String.valueOf(ConnectedListMovesense.connectMovesenseList));
                floatingActionButton.setEnabled(true);
            }

            @Override
            public void onError(MdsException e) {
                Log.e(TAG, "onError:" + e);
                showConnectionError(e);
            }

            @Override
            public void onDisconnect(String bleAddress) {
                Log.d(TAG, "onDisconnect: " + bleAddress);
            }
        });
    }

    public void onTransitionLoggerClick(View view){
        if(view.getId() == R.id.floatingActionButton){
            startActivity(new Intent(ConnectionActivity.this,DataLoggerActivity.class));
        }
    }


    private void showConnectionError(MdsException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Connection Error:")
                .setMessage(e.getMessage());
        builder.create().show();
    }

    private void showToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(MovesenseModel m: ConnectedListMovesense.connectMovesenseList){
            mMds.disconnect(m.getMacAddress());
        }

        if(receiver !=null)
            unregisterReceiver(receiver);
    }

}