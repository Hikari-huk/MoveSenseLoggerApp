package jp.aoyama.it.movesenseloggerapp.activity01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.widget.Toast;

import com.movesense.mds.Mds;
import com.movesense.mds.MdsConnectionListener;
import com.movesense.mds.MdsException;

import jp.aoyama.it.movesenseloggerapp.activity02.DataLoggerActivity;
import jp.aoyama.it.movesenseloggerapp.R;

public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = "ConnectionActivity";

    private static final int REQUEST_CODE_BLUETOOTH_SCAN = 2;

    // MDS
    private Mds mMds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        // MDS オブジェクトの初期化
        mMds = Mds.builder().build(this);
    }

    public void onConnectButtonClick(View view){
        if(view.getId()==R.id.btnConnect){
            scanBluetoothDevices();
        }
    }

    private void scanBluetoothDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                showToast("接続できました");
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
                    unregisterReceiver(receiver);

                    // Movesense の MAC address と MdsConnectionListener を引数にして connect する
                    mMds.connect(deviceHardwareAddress, new MdsConnectionListener() {
                        @Override
                        public void onConnect(String macAddress) {
                            Log.d(TAG, "onConnect: " + macAddress);
                        }

                        // onConnect から数秒間して onConnectionComplete が呼ばれる
                        @Override
                        public void onConnectionComplete(String macAddress, String serial) {
                            Log.d(TAG, "onConnectionComplete: " + serial);
                            showToast("接続しました\nSerial: " + serial);
                            Intent intent = new Intent(ConnectionActivity.this, DataLoggerActivity.class);
                            intent.putExtra(DataLoggerActivity.SERIAL, serial);
                            intent.putExtra(DataLoggerActivity.MAC_ADDRESS, macAddress);
                            startActivity(intent);
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
            }
        }
    };

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

}