package com.example.navermapsample;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class BeaconImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private AutoCompleteTextView searchEditText;
    private ImageButton searchButton;
    private ImageButton backButton; // Add ImageButton for back navigation
    private RelativeLayout parentLayout;
    private View redDot;

    private Bitmap mapBitmap;
    private Bitmap pathBitmap;

    private int imageViewWidth;
    private int imageViewHeight;
    private static final float MIRICANVAS_ITEM_COPY_KEY = 0.5f; // Replace 0.5f with the appropriate value


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
        setContentView(R.layout.activity_beacon_image);

        imageView = findViewById(R.id.imageViewBeacon);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        parentLayout = findViewById(R.id.parentLayout);

        // 자동완성 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RoomConstants.ROOM_NUMBERS);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        // 초기 이미지 설정
        mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
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
                        Toast.makeText(BeaconImageActivity.this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
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

        float endXRatio = MIRICANVAS_ITEM_COPY_KEY, endYRatio = 0.47f;
        float startXRatio = 0.16f, startYRatio = 0.47f;

        switch (roomNumber) {
            case RoomConstants.ROOM_201:
                endXRatio = 0.96f;
                endYRatio = 0.5f;
                break;
            case RoomConstants.ROOM_202:
                endXRatio = 0.51f;
                endYRatio = 0.455f;
                break;
            case RoomConstants.ROOM_203:
                endXRatio = 0.75f;
                endYRatio = 0.5f;
                break;
            case RoomConstants.ROOM_205:
                endXRatio = 0.71f;
                endYRatio = 0.5f;
                break;
            case RoomConstants.ROOM_204:
                endXRatio = 0.46f;
                endYRatio = 0.455f;
                break;
            case RoomConstants.ROOM_206:
                endXRatio = 0.43f;
                endYRatio = 0.455f;
                break;
            case RoomConstants.ROOM_209:
                endXRatio = 0.475f;
                endYRatio = 0.5f;
                break;
            case RoomConstants.ROOM_301:
            case RoomConstants.ROOM_302:
            case RoomConstants.ROOM_303:
            case RoomConstants.ROOM_304:
            case RoomConstants.ROOM_306:
            case RoomConstants.ROOM_308:
            case RoomConstants.ROOM_309:
            case RoomConstants.ROOM_310:
                showToastWithCustomDuration(this, "현재 층은 2층입니다 "+" 3층으로 올라가세요", 10000);
                endXRatio = 0.16f;
                endYRatio = 0.5f;
                break;
            case RoomConstants.ROOM_102:
            case RoomConstants.ROOM_103:
            case RoomConstants.ROOM_104:
            case RoomConstants.ROOM_105:
            case RoomConstants.ROOM_106:
            case RoomConstants.ROOM_107:
            case RoomConstants.ROOM_109:
                showToastWithCustomDuration(this, "현재 층은 2층입니다 "+" 1층으로 내려가세요", 10000);
                endXRatio = 0.16f;
                endYRatio = 0.5f;
                break;

            default:
                Toast.makeText(this, "잘못된 강의실 번호를 입력하였습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss(); // 비정상적인 경우 ProgressDialog 종료
                return;
        }

        int startX = (int) (startXRatio * mapBitmap.getWidth());
        int startY = (int) (startYRatio * mapBitmap.getHeight());
        int endX = (int) (endXRatio * mapBitmap.getWidth());
        int endY = (int) (endYRatio * mapBitmap.getHeight());

        // Draw start and end points
        PathDrawer.drawPoint(pathBitmap, startX, startY, Color.RED, 25);
        PathDrawer.drawPoint(pathBitmap, endX, endY, Color.BLUE, 25);

        // Find the path in a background thread
        new PathFindingTask().execute(startXRatio, endYRatio, startXRatio, endYRatio);
    }

    private class PathFindingTask extends AsyncTask<Float, Void, List<Node>> {
        @Override
        protected List<Node> doInBackground(Float... params) {
            float startXRatio = params[0];
            float startYRatio = params[1];
            float endXRatio = params[2];
            float endYRatio = params[3];

            PathFinding pathFinding = new PathFinding(mapBitmap);
            return pathFinding.findPath(startXRatio, startYRatio, endXRatio, endYRatio);
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
