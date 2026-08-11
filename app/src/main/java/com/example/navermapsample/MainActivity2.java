package com.example.navermapsample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.beaconset.BluetoothChangedReceiver;
import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.ConnectionState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconConnection;
import com.minew.beaconset.MinewBeaconConnectionListener;
import com.minew.beaconset.MinewBeaconManager;
import com.minew.beaconset.MinewBeaconManagerListener;

import java.util.List;

public class MainActivity2 extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity2_DEBUG";
    private RecyclerView mRecycle;
    private MinewBeaconManager mMinewBeaconManager;
    private BeaconListAdapter mAdapter;
    private ProgressDialog mpDialog;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_FINE_LOCATION = 125;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Intent mEnableIntent;
    private ProgressDialog progressDialog;

    private boolean imageDisplayed = false;  // Flag to track if image is displayed
    private BluetoothChangedReceiver bluetoothChangedReceiver;

    private Runnable timeoutRunnable;
    private boolean isBeaconDetected = false; // 비콘 감지 여부를 추적하는 플래그

    // MainActivity로부터 전달받을 데이터를 저장할 변수들
    private String receivedRoomNumber = "";
    private double receivedBuildingLat = -1.0; // 유효하지 않은 초기값
    private double receivedBuildingLng = -1.0; // 유효하지 않은 초기값


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        // MainActivity로부터 전달받은 데이터 추출
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            receivedRoomNumber = receivedIntent.getStringExtra("room_number");
            receivedBuildingLat = receivedIntent.getDoubleExtra("building_lat", -1.0);
            receivedBuildingLng = receivedIntent.getDoubleExtra("building_lng", -1.0);

            Log.d(TAG, "MainActivity로부터 받은 강의실 번호: " + receivedRoomNumber);
            Log.d(TAG, "MainActivity로부터 받은 건물 좌표: Lat=" + receivedBuildingLat + ", Lng=" + receivedBuildingLng);
        }

        initView();
        initManager();
        initListener();
        dialogshow(); // ProgressDialog 초기화
        initPermission(); // 권한 요청 및 스캔 시작 로직

        // 5초 후 이전 액티비티로 돌아가는 타이머 시작
        startScanTimeout();
    }

    private void startScanTimeout() {
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // 비콘이 이미 감지되었는지 확인
                if (!isBeaconDetected) {
                    Log.d(TAG, "비콘 스캔 타임아웃: 이전 액티비티로 이동");
                    Toast.makeText(MainActivity2.this, "비콘을 찾을 수 없어 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 현재 액티비티 종료 (이전 액티비티로 돌아감)
                } else {
                    Log.d(TAG, "비콘이 이미 감지되어 타임아웃 무시됨");
                }
            }
        };
        handler.postDelayed(timeoutRunnable, 5000); // 5초 후 실행
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("비콘 스캔");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecycle = findViewById(R.id.main_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new BeaconListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("비콘을 찾는 중입니다");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        mMinewBeaconManager.setRangeInterval(10 * 100); // 1초 간격
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
                        // 블루투스가 켜지면 스캔 시작
                        scanLeDevice(true);
                        break;
                }
            }

            @Override
            public void onRangeBeacons(List<MinewBeacon> beacons) {
                Log.e(TAG, "감지된 비콘 수: " + beacons.size());
                mAdapter.setData(beacons);

                // imageDisplayed가 false이고 유효한 비콘이 있을 때만 액티비티를 시작하도록 처리
                if (!imageDisplayed && beacons.size() > 0) {
                    for (MinewBeacon beacon : beacons) {
                        String uuid = beacon.getUuid();
                        Log.d(TAG, "감지된 비콘 UUID: " + uuid);

                        Intent intent = null;
                        String entranceName = "";
                        // 비콘 UUID에 따라 목적지 액티비티 및 입구 이름 설정
                        if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0".equals(uuid)) {
                            intent = new Intent(MainActivity2.this, BeaconImageActivity2_2_L.class);
                            entranceName = "공학L관 2층";
                        } else if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E1".equals(uuid)) {
                            intent = new Intent(MainActivity2.this, BeaconImageActivity2_1_L.class);
                            entranceName = "공학L관 1층";
                        } else if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E3".equals(uuid)) {
                            intent = new Intent(MainActivity2.this, BeaconImageActivity3_1_L.class);
                            entranceName = "상경학관 좌측 입구";
                        } else if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E4".equals(uuid)) {
                            intent = new Intent(MainActivity2.this, BeaconImageActivity3_1_R.class);
                            entranceName = "상경학관 우측 입구";
                        } else if ("E2C56DB5-DFFB-48D2-B060-D0F5A71096E5".equals(uuid)) {
                            intent = new Intent(MainActivity2.this, BeaconImageActivity3_1_C.class);
                            entranceName = "상경학관 중앙 입구";
                        }

                        if (intent != null) {
                            // 비콘이 감지되었음을 표시
                            isBeaconDetected = true;

                            // 타임아웃 취소
                            handler.removeCallbacks(timeoutRunnable);

                            Log.d(TAG, "비콘 UUID " + uuid + " 감지! " + entranceName + " 액티비티 시작.");

                            // 1. 입구 이름 전달
                            intent.putExtra("entrance_name", entranceName);

                            // 2. MainActivity로부터 받은 강의실 번호 전달
                            if (receivedRoomNumber != null && !receivedRoomNumber.isEmpty()) {
                                intent.putExtra("room_number", receivedRoomNumber);
                                Log.d(TAG, "강의실 번호 전달: " + receivedRoomNumber);
                            }

                            // 3. MainActivity로부터 받은 건물 좌표를 start_x, start_y로 전달 (float로 변환)
                            // QR 코드로 시작하는 경우 이 좌표가 사용됩니다.
                            if (receivedBuildingLat != -1.0 && receivedBuildingLng != -1.0) {
                                intent.putExtra("start_x", (float) receivedBuildingLat);
                                intent.putExtra("start_y", (float) receivedBuildingLng);
                                Log.d(TAG, "QR 시작 좌표 전달: X=" + (float) receivedBuildingLat + ", Y=" + (float) receivedBuildingLng);
                            } else {
                                Log.d(TAG, "전달받은 건물 좌표가 유효하지 않습니다. 기본 비콘 시작 지점 사용.");
                            }

                            mRecycle.setVisibility(View.GONE); // 리사이클러 뷰 숨기기
                            progressDialog.dismiss(); // 로딩 다이얼로그 닫기
                            imageDisplayed = true; // 플래그 설정

                            startActivity(intent);
                            finish(); // MainActivity2를 종료하고 새로 시작된 액티비티로 이동

                            break; // 감지된 비콘 처리 후 루프 종료
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

    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "BLE를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                scanLeDevice(true); // 블루투스가 켜져 있으면 바로 스캔 시작
                break;
        }
    }

    private void initListener() {
        mAdapter.setOnItemClickLitener(new BeaconListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mpDialog.setMessage(getString(R.string.connecting) + mAdapter.getData(position).getName());
                // mpDialog.show(); // 비콘 설정 연결이 필요 없다면 주석 유지
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
                    Manifest.permission.ACCESS_COARSE_LOCATION, // 하위 호환성 유지
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            requestPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

        // 필요한 모든 권한이 부여되었는지 먼저 확인
        boolean allPermissionsGranted = true;
        for (String permission : requestPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            Log.d(TAG, "모든 권한이 이미 부여되었습니다. 블루투스 상태 확인.");
            checkBluetooth(); // 권한이 있으면 바로 블루투스 상태 확인
        } else {
            Log.d(TAG, "권한 요청 필요. onRequestPermissionsResult 처리.");
            ActivityCompat.requestPermissions(this, requestPermissions, REQUEST_FINE_LOCATION);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        handler.post(new Runnable() { // postDelayed 대신 post를 사용하여 즉시 실행
            @Override
            public void run() {
                BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = manager.getAdapter();
                boolean bluetoothEnable = bluetoothAdapter.isEnabled();
                if (bluetoothEnable) {
                    if (enable) {
                        Log.d(TAG, "BLE 스캔 시작");
                        mMinewBeaconManager.startScan();
                    } else {
                        Log.d(TAG, "BLE 스캔 중지");
                        mMinewBeaconManager.stopScan();
                        progressDialog.dismiss();
                    }
                } else {
                    Log.d(TAG, "블루투스가 비활성화되어 스캔을 시작할 수 없습니다.");
                    showBLEDialog(); // 블루투스 활성화 유도
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FINE_LOCATION) {
            boolean isGrant = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGrant = false;
                    break;
                }
            }
            if (isGrant) {
                Log.d(TAG, "모든 요청된 권한 부여됨. 블루투스 상태 확인.");
                checkBluetooth(); // 권한이 부여되면 블루투스 상태 확인 및 스캔 시작
            } else {
                Log.d(TAG, "권한이 거부되었습니다. 앱 기능이 제한될 수 있습니다.");
                Toast.makeText(this, "필수 권한이 거부되어 비콘 스캔을 할 수 없습니다.", Toast.LENGTH_LONG).show();
                finish(); // 권한 없으면 액티비티 종료
            }
        }
    }

    MinewBeaconConnectionListener minewBeaconConnectionListener = new MinewBeaconConnectionListener() {
        @Override
        public void onChangeState(MinewBeaconConnection connection, ConnectionState state) {
            switch (state) {
                case BeaconStatus_Connected:
                    if (mpDialog != null) mpDialog.dismiss();
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
                                Toast.makeText(getApplicationContext(), "비콘 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "onResume: 비콘 스캔 및 블루투스 리시버 등록");
        // BluetoothChangedReceiver 등록
        bluetoothChangedReceiver = new BluetoothChangedReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothChangedReceiver, filter);

        // 비콘 감지 상태 초기화 (onResume에서는 다시 시작하므로)
        isBeaconDetected = false;
        imageDisplayed = false;

        // 스캔 시작
        if (mMinewBeaconManager.checkBluetoothState() == BluetoothState.BluetoothStatePowerOn) {
            scanLeDevice(true);
        }

        // 재개 시에도 타임아웃 다시 시작 (기존 타이머는 onPause에서 취소됨)
        startScanTimeout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: 비콘 스캔 중지 및 블루투스 리시버 해제");
        mMinewBeaconManager.stopScan(); // 비콘 탐색 중지
        // BluetoothChangedReceiver 해제
        if (bluetoothChangedReceiver != null) {
            unregisterReceiver(bluetoothChangedReceiver);
        }
        handler.removeCallbacks(timeoutRunnable); // 액티비티가 일시정지되면 타임아웃도 취소
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼 클릭 시 onBackPressed() 호출
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 비콘 스캔 중지 및 진행 다이얼로그 dismiss
        mMinewBeaconManager.stopScan();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // 타임아웃 취소
        handler.removeCallbacks(timeoutRunnable);
        super.onBackPressed(); // 기본 뒤로가기 동작
    }

    protected void dialogshow() {
        mpDialog = new ProgressDialog(MainActivity2.this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpDialog.setTitle(null);
        mpDialog.setIcon(null);
        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                // 다이얼로그 취소 시 동작 (예: 스캔 중지)
                mMinewBeaconManager.stopScan();
                Log.d(TAG, "mpDialog 취소됨: 스캔 중지");
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
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "블루투스 활성화 성공. 스캔 시작.");
                scanLeDevice(true);
            } else {
                Log.d(TAG, "블루투스 활성화 거부. 액티비티 종료.");
                Toast.makeText(this, "블루투스 활성화가 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeoutRunnable);

        // mpDialog 및 progressDialog가 null이 아닐 경우 dismiss
        if (mpDialog != null && mpDialog.isShowing()) {
            mpDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void startBeaconScan() {
        mMinewBeaconManager.startScan();
    }

    public void stopBeaconScan() {
        mMinewBeaconManager.stopScan();
    }
}