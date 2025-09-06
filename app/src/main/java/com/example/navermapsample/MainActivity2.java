package com.example.navermapsample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.ConnectionState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconConnection;
import com.minew.beaconset.MinewBeaconConnectionListener;
import com.minew.beaconset.MinewBeaconManager;
import com.minew.beaconset.MinewBeaconManagerListener;

import java.util.List;

public class MainActivity2 extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "chen_tag";
    private RecyclerView mRecycle;
    private MinewBeaconManager mMinewBeaconManager;
    private BeaconListAdapter mAdapter;
    private ProgressDialog mpDialog;
    private static final int REQUEST_ENABLE_BT = 2;
    private final int PERMISSION_COARSE_LOCATION = 122;

    private static final int REQUEST_BLUETOOTH_SCAN = 123;
    private static final int REQUEST_BLUETOOTH_CONNECT = 124;
    private static final int REQUEST_FINE_LOCATION = 125;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Intent mEnableIntent;
    private ProgressDialog progressDialog;

    private boolean imageDisplayed = false;  // Flag to track if image is displayed

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initManager();
        initListener();
        dialogshow();
        initPermission();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecycle = findViewById(R.id.main_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new BeaconListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("비콘을 찾는 중입니다");
        progressDialog.setCancelable(false);
    }

    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        mMinewBeaconManager.setRangeInterval(10 * 1000);
        mMinewBeaconManager.setMinewbeaconManagerListener(new MinewBeaconManagerListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onUpdateBluetoothState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "블루투스 꺼짐", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "블루투스 켜짐", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onRangeBeacons(List<MinewBeacon> beacons) {
                Log.e("test", "size=" + beacons.size());
                mAdapter.setData(beacons);

                if (!imageDisplayed) {
                    for (MinewBeacon beacon : beacons) {
                        String uuid = beacon.getUuid();
                        Log.d(TAG, "Detected beacon UUID: " + uuid);

                        if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0".equals(uuid)) {
                            Log.d(TAG, "Launching BeaconImageActivity for UUID: " + uuid);
                            Intent intent = new Intent(MainActivity2.this, BeaconImageActivity.class);
                            startActivity(intent);
                            imageDisplayed = true;
                            progressDialog.dismiss();
                            break;
                        } else if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E1".equals(uuid)) {
                            Log.d(TAG, "Launching BeaconImageActivity2 for UUID: " + uuid);
                            Intent intent = new Intent(MainActivity2.this, BeaconImageActivity2.class);
                            startActivity(intent);
                            imageDisplayed = true;
                            progressDialog.dismiss();
                            break;
                        }
                    }
                }
            }


            @Override
            public void onAppearBeacons(List<MinewBeacon> beacons) {
            }

            @Override
            public void onDisappearBeacons(List<MinewBeacon> beacons) {
            }
        });
    }

    private void startService() {
        mMinewBeaconManager.startService();
        mMinewBeaconManager.registerBleChangeBroadcast();
        progressDialog.show();
    }

    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                scanLeDevice(true);
                break;
        }
    }

    private void initListener() {
        mAdapter.setOnItemClickLitener(new BeaconListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mpDialog.setMessage(getString(R.string.connecting) + mAdapter.getData(position).getName());
                mpDialog.show();
                mMinewBeaconManager.stopScan();
                MinewBeacon minewBeacon = mAdapter.getData(position);
                MinewBeaconConnection minewBeaconConnection = new MinewBeaconConnection(MainActivity2.this, minewBeacon);
                minewBeaconConnection.setMinewBeaconConnectionListener(minewBeaconConnectionListener);
                minewBeaconConnection.connect();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermission() {
        String[] requestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions = new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            requestPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

        ActivityCompat.requestPermissions(this, requestPermissions, REQUEST_FINE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = manager.getAdapter();
                boolean bluetoothEnable = bluetoothAdapter.isEnabled();
                if (bluetoothEnable) {
                    if (enable) {
                        Log.d(TAG, "scanLeDevice   开启扫描 ");
                        mMinewBeaconManager.startScan();
                        progressDialog.show();
                    } else {
                        mMinewBeaconManager.stopScan();
                        progressDialog.dismiss();
                    }
                }
            }
        }, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                boolean isGrant = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false;
                        break;
                    }
                }
                if (isGrant) {
                    startService();
                    checkBluetooth();
                }
                break;
        }
    }

    MinewBeaconConnectionListener minewBeaconConnectionListener = new MinewBeaconConnectionListener() {
        @Override
        public void onChangeState(MinewBeaconConnection connection, ConnectionState state) {
            switch (state) {
                case BeaconStatus_Connected:
                    mpDialog.dismiss();
                    Intent intent = new Intent(MainActivity2.this, DetilActivity.class);
                    intent.putExtra("mac", connection.setting.getMacAddress());
                    startActivity(intent);
                    break;
                case BeaconStatus_ConnectFailed:
                case BeaconStatus_Disconnect:
                    if (mpDialog != null) {
                        mpDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "비콘 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }

        @Override
        public void onWriteSettings(MinewBeaconConnection minewBeaconConnection, boolean b, boolean b1) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void dialogshow() {
        mpDialog = new ProgressDialog(MainActivity2.this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpDialog.setTitle(null);
        mpDialog.setIcon(null);
        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
            }
        });
        mpDialog.setCancelable(true);
        mpDialog.setCanceledOnTouchOutside(false);
    }

    private void showBLEDialog() {
        mEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(mEnableIntent, REQUEST_ENABLE_BT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    scanLeDevice(true);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMinewBeaconManager != null) {
            mMinewBeaconManager.stopService();
            mMinewBeaconManager.unRegisterBleChangeBroadcast();
        }
    }
    public void startBeaconScan() {
        mMinewBeaconManager.startScan();
    }

    public void stopBeaconScan() {
        mMinewBeaconManager.stopScan();
    }

}
