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

//상경학관 1층 중앙입구에 대한 파일.
public class BeaconImageActivity3_1_C extends AppCompatActivity {

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

            toolbar.setNavigationOnClickListener(v -> {
                onBackPressed(); // 뒤로가기 버튼 클릭 시 onBackPressed() 호출
            });
        }


        // 자동완성 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RoomConstants.ROOM_NUMBERS);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        // 초기 이미지 설정
        mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
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
            startXRatio = 0.5f;
            startYRatio = 0.6f;
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
                }
            });
        }

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
                    Toast.makeText(BeaconImageActivity3_1_C.this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 검색어가 없는 경우에 대한 처리 (선택 사항)
            }
        });
        Log.d("BeaconImageActivity", "Received startX: " + startXFromQr + ", startY: " + startYFromQr);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BeaconImageActivity3_1_C.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 새로운 태스크로 시작하여 이전 액티비티 스택을 지운다.
        startActivity(intent);
        finish(); // 현재 액티비티 종료
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

        switch (roomNumber) {
            case RoomConstants.ROOM_3101:
                endXRatio = 0.127f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3102:
                endXRatio = 0.173f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3103:
                endXRatio = 0.217f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3104:
                endXRatio = 0.347f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3105:
                endXRatio = 0.396f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3106:
                endXRatio = 0.375f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3108:
                endXRatio = 0.443f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3109:
                endXRatio = 0.443f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3110:
                endXRatio = 0.553f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3111:
                endXRatio = 0.553f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3112:
                endXRatio = 0.620f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3113:
                endXRatio = 0.605f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3114:
                endXRatio = 0.665f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3115:
                endXRatio = 0.774f;
                endYRatio = 0.414f;
                break;
            case RoomConstants.ROOM_3116:
                endXRatio = 0.693f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3117:
                endXRatio = 0.892f;
                endYRatio = 0.548f;
                break;
            case RoomConstants.ROOM_3001:
            case RoomConstants.ROOM_3002:
            case RoomConstants.ROOM_3003:
            case RoomConstants.ROOM_3004:
            case RoomConstants.ROOM_3005:
                showToastWithCustomDuration(this, "현재 층은 1층입니다. 지하(좌측계단)로 내려가세요.", 2000);
                endXRatio = 0.086f;
                endYRatio = 0.3f;
                break;
            case RoomConstants.ROOM_3006:
            case RoomConstants.ROOM_3007:
                showToastWithCustomDuration(this, "현재 층은 1층입니다. 지하(우측계단)로 내려가세요.", 2000);
                endXRatio = 0.912f;
                endYRatio = 0.3f;
                break;
            default:
                Toast.makeText(this, "해당 건물에 없거나 강의실이 아닙니다.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss(); // 비정상적인 경우 ProgressDialog 종료
                return;
        }

        int startX = (int) (startXRatio * mapBitmap.getWidth());
        int startY = (int) (startYRatio * mapBitmap.getHeight());
        int endX = (int) (endXRatio * mapBitmap.getWidth());
        int endY = (int) (endYRatio * mapBitmap.getHeight());

        PathDrawer.drawPoint(pathBitmap, startX, startY, Color.BLUE, 20);
        PathDrawer.drawPoint(pathBitmap, endX, endY, Color.RED, 20);

        // PathFindingTask를 실행할 때 QR 코드로부터 받은 출발 좌표 비율을 넘겨줍니다.
        new PathFindingTask().execute(startXRatio, startYRatio, endXRatio, endYRatio, (float) roomNumber);
    }

    private void showPathToRoom(int roomNumber) {
        showPathToRoom(roomNumber, 0.5f, 0.6f); // 기존 하드코딩 값 사용 (비콘 스캔 시 기본 출발 지점)
    }

    private class PathFindingTask extends AsyncTask<Float, Void, List<Node>> {
        @Override
        protected List<Node> doInBackground(Float... params) {
            float startXRatio = params[0];
            float startYRatio = params[1];
            float endXRatio = params[2];
            float endYRatio = params[3];
            int roomNumber = Math.round(params[4]); // roomNumber를 정수로 변환

            PathFinding pathFinding = new PathFinding(mapBitmap);
            return pathFinding.findPath(startXRatio, startYRatio, endXRatio, endYRatio, roomNumber); // roomNumber 전달
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
                Toast.makeText(BeaconImageActivity3_1_C.this, "경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }

    }
}