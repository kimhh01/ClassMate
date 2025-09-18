package com.example.navermapsample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class BeaconImageActivity2 extends AppCompatActivity {

    private ImageView imageView;
    private AutoCompleteTextView searchEditText;
    private ImageButton searchButton;
    private RelativeLayout parentLayout;
    private Bitmap mapBitmap;
    private Bitmap pathBitmap;
    private int imageViewWidth;
    private int imageViewHeight;
    private ProgressDialog progressDialog;

    private void showToastWithCustomDuration(final Context context, final String message, final int duration) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300); // Set Toast position

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime < duration) {
                    toast.show();
                    handler.postDelayed(this, 3500); // Toast.LENGTH_SHORT duration is approximately 3.5 seconds
                } else {
                    toast.cancel();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_image2);

        imageView = findViewById(R.id.imageViewBeacon2);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        parentLayout = findViewById(R.id.parentLayout);

        // 자동완성 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RoomConstants.ROOM_NUMBERS);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        // 초기 이미지 설정
        mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        imageView.setImageBitmap(mapBitmap);

        // 이미지 뷰의 크기를 가져오기 위해 ViewTreeObserver 사용
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imageViewWidth = imageView.getWidth();
                    imageViewHeight = imageView.getHeight();
                }
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();  // 현재 액티비티를 종료합니다.
        });

        // Set OnClickListener for searchButton
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = searchEditText.getText().toString();
                if (!input.isEmpty()) {
                    try {
                        int number = Integer.parseInt(input);
                        showPathToRoom(number);
                        hideKeyboard();
                    } catch (NumberFormatException e) {
                        Toast.makeText(BeaconImageActivity2.this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showPathToRoom(int roomNumber) {
        if (imageViewWidth == 0 || imageViewHeight == 0 || mapBitmap == null) {
            return; // 이미지 뷰의 크기를 아직 가져오지 못하거나 이미지가 설정되지 않은 경우
        }

        // ProgressDialog 생성
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("경로를 찾는 중 입니다");
        progressDialog.setCancelable(false); // 사용자가 취소할 수 없도록 설정
        progressDialog.show();

        pathBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);

        float startXRatio = 0.1f, startYRatio = 0.47f;
        float endXRatio = 0.09f, endYRatio = 0.42f;

        switch (roomNumber) {
            case RoomConstants.ROOM_102:
                startXRatio = 0.62f;
                startYRatio = 0.4f;
                break;
            case RoomConstants.ROOM_103:
                startXRatio = 0.756f;
                startYRatio = 0.442f;
                break;
            case RoomConstants.ROOM_104:
                startXRatio = 0.43f;
                startYRatio = 0.4f;
                break;
            case RoomConstants.ROOM_105:
                startXRatio = 0.498f;
                startYRatio = 0.442f;
                break;
            case RoomConstants.ROOM_106:
                startXRatio = 0.23f;
                startYRatio = 0.4f;
                break;
            case RoomConstants.ROOM_107:
                startXRatio = 0.454f;
                startYRatio = 0.442f;
                break;
            case RoomConstants.ROOM_109:
                startXRatio = 0.8f;
                startYRatio = 0.442f;
                break;
            case RoomConstants.ROOM_301:
            case RoomConstants.ROOM_302:
            case RoomConstants.ROOM_303:
            case RoomConstants.ROOM_304:
            case RoomConstants.ROOM_306:
            case RoomConstants.ROOM_308:
            case RoomConstants.ROOM_309:
            case RoomConstants.ROOM_310:
                showToastWithCustomDuration(this, "현재 층은 1층입니다 "+" 3층으로 올라가세요", 10000);
                startXRatio = 0.12f;
                startYRatio = 0.442f;
                break;
            case RoomConstants.ROOM_201:
            case RoomConstants.ROOM_202:
            case RoomConstants.ROOM_203:
            case RoomConstants.ROOM_204:
            case RoomConstants.ROOM_205:
            case RoomConstants.ROOM_206:
            case RoomConstants.ROOM_209:
                showToastWithCustomDuration(this, "현재 층은 1층입니다 "+" 2층으로 올라가세요", 10000);
                startXRatio = 0.12f;
                startYRatio = 0.442f;
                break;

            default:
                Toast.makeText(this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss(); // 비정상적인 경우 ProgressDialog 종료
                return;
        }

        // Draw start and end points
        int startX = (int) (startXRatio * mapBitmap.getWidth());
        int startY = (int) (startYRatio * mapBitmap.getHeight());
        int endX = (int) (endXRatio * mapBitmap.getWidth());
        int endY = (int) (endYRatio * mapBitmap.getHeight());

        PathDrawer.drawPoint(pathBitmap, startX, startY, Color.RED, 25);
        PathDrawer.drawPoint(pathBitmap, endX, endY, Color.BLUE, 25);

        // Find the path in a background thread
        new PathFindingTask().execute(startXRatio, startYRatio, endXRatio, endYRatio, (float) roomNumber); // roomNumber를 float로 변환하여 전달
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

            if (path != null) {
                PathDrawer.drawPath(pathBitmap, path);
                imageView.setImageBitmap(pathBitmap);
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
