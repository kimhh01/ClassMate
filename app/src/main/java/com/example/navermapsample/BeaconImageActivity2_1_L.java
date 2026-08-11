package com.example.navermapsample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

//상경학관 1층 좌측입구에 대한 파일.
public class BeaconImageActivity2_1_L extends AppCompatActivity {

    private ImageView imageView;
    private AutoCompleteTextView searchEditText;
    private ImageButton searchButton;
    private ImageButton backButton; // Add ImageButton for back navigation
    private RelativeLayout parentLayout;
    private Bitmap mapBitmap;
    private Bitmap pathBitmap;

    private int imageViewWidth;
    private int imageViewHeight;
    private static final float MIRICANVAS_ITEM_COPY_KEY = 0.5f; // 적절한 값으로 교체

    private ProgressDialog progressDialog;
    private String entranceName = "";

    private void showToastWithCustomDuration(final Context context, final String message, final int duration) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300); // 토스트 위치 설정

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime < duration) {
                    toast.show();
                    handler.postDelayed(this, 3500); // 토스트의 지속시간
                } else {
                    toast.cancel();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_image);

        imageView = findViewById(R.id.imageViewBeacon);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        parentLayout = findViewById(R.id.parentLayout);

        TextView entranceText = findViewById(R.id.entranceInfoText); // Layout폴더에 activity_beacon_image.xml에 텍스트를 변경하기 위한 코드
        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra("entrance_name")) {
            entranceName = receivedIntent.getStringExtra("entrance_name");
            entranceText.setText(entranceName);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("강의실 찾기"); // 여기에 원하는 타이틀 이름을 넣어주세요.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 자동완성 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RoomConstants.ROOM_NUMBERS);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        // 초기 이미지 설정
        mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        imageView.setImageBitmap(mapBitmap);

        // QR 코드로 받은 좌표
        float startXFromQr = getIntent().getFloatExtra("start_x", -1f);
        float startYFromQr = getIntent().getFloatExtra("start_y", -1f);

        // start_gif 이미지 로드
        Bitmap startGif = BitmapFactory.decodeResource(getResources(), R.drawable.start_gif);

        float startXRatio;
        float startYRatio;

        // QR 코드로부터 좌표를 받았다면 해당 좌표 사용, 아니면 비콘 기본 출발 지점 사용
        if (startXFromQr != -1f && startYFromQr != -1f) {
            startXRatio = startXFromQr;
            startYRatio = startYFromQr;
        } else {
            // 비콘 스캔 시 기본 출발 지점 (하드코딩 값)
            startXRatio = 0.113f;
            startYRatio = 0.421f;
        }
        // 좌표가 유효하고 이미지들이 로드되었으면 start_gif 그리기
        if (startGif != null && mapBitmap != null) {
            // 비율 값을 실제 픽셀 좌표로 변환
            int startXPixel = (int) (startXRatio * mapBitmap.getWidth());
            int startYPixel = (int) (startYRatio * mapBitmap.getHeight());

            Bitmap mutableBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            // PathDrawer를 사용하여 start_gif 그리기 (픽셀 좌표 사용)
            PathDrawer.drawGifMarker(canvas, startGif, (float) startXPixel, (float) startYPixel);

            // 수정한 Bitmap을 ImageView에 설정
            imageView.setImageBitmap(mutableBitmap);
        }

        // 이미지 뷰의 크기를 가져오기 위해 ViewTreeObserver 사용
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imageViewWidth = imageView.getWidth();
                    imageViewHeight = imageView.getHeight();

                    // QR 코드로 받은 좌표가 있다면 자동으로 경로 표시 (특정 강의실로 임시 설정)
                    float startXFromQr = getIntent().getFloatExtra("start_x", -1f);
                    float startYFromQr = getIntent().getFloatExtra("start_y", -1f);
                    Log.d("QR_CODE_DATA", "Received startX: " + startXFromQr + ", Received startY: " + startYFromQr);

//                    if (startXFromQr != -1f && startYFromQr != -1f) {
//                        // 임시로 106호로 설정. 실제로는 QR 코드 또는 다른 방식으로 강의실 정보를 받아와야 함
//                        int roomNumberFromQr = 106;
//                        showPathToRoom(roomNumberFromQr, startXFromQr, startYFromQr);
//                    }
                }
            });
        }

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // 뒤로가기 버튼 클릭 시 onBackPressed() 호출
        });

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(v -> {
            String input = searchEditText.getText().toString();
            if (!input.isEmpty()) {
                try {
                    int number = Integer.parseInt(input);
                    // QR 코드 스캔으로 받은 좌표가 있다면 해당 좌표를 사용하고, 없다면 기본값 사용
//                    float startXFromQr = getIntent().getFloatExtra("start_x", -1f);
//                    float startYFromQr = getIntent().getFloatExtra("start_y", -1f);
                    if (startXFromQr != -1f && startYFromQr != -1f) {
                        showPathToRoom(number, startXFromQr, startYFromQr);
                    } else {
                        showPathToRoom(number, startXRatio + 0.001f, startYRatio); // 위에 하드코딩한 출발지점과 근소한 차이를 두어 비콘을 스캔했을 때 start_gif 이미지가 보이지 않던 문제 해결.
                    }
                    hideKeyboard();
                } catch (NumberFormatException e) {
                    Toast.makeText(BeaconImageActivity2_1_L.this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 검색어가 없는 경우에 대한 처리 (선택 사항)
            }
        });
        Log.d("BeaconImageActivity", "Received startX: " + startXFromQr + ", startY: " + startYFromQr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // 뒤로가기 버튼 클릭 시 onBackPressed() 호출
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPathToRoom(int roomNumber, float startXRatio, float startYRatio) {
        if (imageViewWidth == 0 || imageViewHeight == 0 || mapBitmap == null) {
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("경로를 찾는 중 입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();

        pathBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // QR 코드로부터 전달받은 비율 값을 사용합니다.
        // float startXRatio = 0.113f; // 기존 하드코딩 제거
        // float startYRatio = 0.421f; // 기존 하드코딩 제거
        float endXRatio = 0.16f;
        float endYRatio = 0.47f;

        if (startXRatio < 0.462f) {  // 스캔한 QR코드의 x좌표가 공학L관 1층 약도 이미지 가운데를 기준으로 왼쪽에 있을 때
            switch (roomNumber) {
                case RoomConstants.ROOM_102:
                    endXRatio = 0.625f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_103:
                    endXRatio = 0.554f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_104:
                    endXRatio = 0.44f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_105:
                    endXRatio = 0.497f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_106:
                    endXRatio = 0.235f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_107:
                    endXRatio = 0.460f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_109:
                    endXRatio = 0.801f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_201:
                case RoomConstants.ROOM_202:
                case RoomConstants.ROOM_203:
                case RoomConstants.ROOM_205:
                case RoomConstants.ROOM_204:
                case RoomConstants.ROOM_206:
                case RoomConstants.ROOM_209:
                    showToastWithCustomDuration(this, "해당 강의실은 2층입니다. 가까운 계단으로 올라가세요.", 2000);
                    endXRatio = 0.113f;
                    endYRatio = 0.697f;
                    roomNumber = 1; // 0으로 넘겨주지 않으면 1층에서 2층 강의실을 입력했을 때의 노드가 불러와짐. 0으로 넘기면 계단까지 직선으로 경로가 표시됨.
                    break;
                default:
                    Toast.makeText(this, "해당 건물에 없거나 강의실이 아닙니다.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss(); // 비정상적인 경우 ProgressDialog 종료
                    return;
            }

        } else { // 스캔한 QR코드의 x좌표가 공학L관 1층 약도 이미지 가운데를 기준으로 오른쪽에 있을 때
            switch (roomNumber) {
                case RoomConstants.ROOM_102:
                    endXRatio = 0.625f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_103:
                    endXRatio = 0.554f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_104:
                    endXRatio = 0.44f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_105:
                    endXRatio = 0.497f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_106:
                    endXRatio = 0.235f;
                    endYRatio = 0.306f;
                    break;
                case RoomConstants.ROOM_107:
                    endXRatio = 0.460f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_109:
                    endXRatio = 0.801f;
                    endYRatio = 0.54f;
                    break;
                case RoomConstants.ROOM_201:
                case RoomConstants.ROOM_202:
                case RoomConstants.ROOM_203:
                case RoomConstants.ROOM_205:
                case RoomConstants.ROOM_204:
                case RoomConstants.ROOM_206:
                case RoomConstants.ROOM_209:
                    showToastWithCustomDuration(this, "해당 강의실은 2층입니다. 가까운 계단으로 올라가세요.", 2000);
                    endXRatio = 0.812f;
                    endYRatio = 0.206f;
                    roomNumber = 2; // 0으로 넘겨주지 않으면 1층에서 2층 강의실을 입력했을 때의 노드가 불러와짐. 0으로 넘기면 계단까지 직선으로 경로가 표시됨.
                    break;
                default:
                    Toast.makeText(this, "해당 건물에 없거나 강의실이 아닙니다.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss(); // 비정상적인 경우 ProgressDialog 종료
                    return;
            }
        }

        int startX = (int) (startXRatio * mapBitmap.getWidth());
        int startY = (int) (startYRatio * mapBitmap.getHeight());
        int endX = (int) (endXRatio * mapBitmap.getWidth());
        int endY = (int) (endYRatio * mapBitmap.getHeight());

        PathDrawer.drawPoint(pathBitmap, startX, startY, Color.BLUE, 15);
        PathDrawer.drawPoint(pathBitmap, endX, endY, Color.RED, 15);

        // PathFindingTask를 실행할 때 QR 코드로부터 받은 출발 좌표 비율을 넘겨줍니다.
        new PathFindingTask().execute(startXRatio, startYRatio, endXRatio, endYRatio, (float) roomNumber);
    }

    private void showPathToRoom(int roomNumber) {
        showPathToRoom(roomNumber, 0.113f, 0.421f); // 기존 하드코딩 값 사용 (비콘 스캔 시 기본 출발 지점)
    }

    private class PathFindingTask extends AsyncTask<Float, Void, List<Node>> {
        @Override
        protected List<Node> doInBackground(Float... params) {
            float startXRatio = params[0];
            float startYRatio = params[1];
            float endXRatio = params[2];
            float endYRatio = params[3];
            int roomNumber = Math.round(params[4]);

            PathFinding pathFinding = new PathFinding(mapBitmap);
            return pathFinding.findPath(startXRatio, startYRatio, endXRatio, endYRatio, roomNumber);
        }

        @Override
        protected void onPostExecute(List<Node> path) {
            progressDialog.dismiss(); // 작업 완료 후 ProgressDialog 종료
            Bitmap destinationGif = BitmapFactory.decodeResource(getResources(), R.drawable.destination_gif);
            Bitmap startGif = BitmapFactory.decodeResource(getResources(), R.drawable.start_gif);

            if (path != null) {
                PathDrawer.drawPath(pathBitmap, path, imageView, startGif, destinationGif); // 경로는 파란색
                imageView.setImageBitmap(pathBitmap);
            } else {
                Toast.makeText(BeaconImageActivity2_1_L.this, "경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BeaconImageActivity2_1_L.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 새로운 태스크로 시작하여 이전 액티비티 스택을 지운다.
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
