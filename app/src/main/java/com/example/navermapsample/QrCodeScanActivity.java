package com.example.navermapsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanOptions;

public class QrCodeScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);

        try {
            startQrCodeScanner();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void startQrCodeScanner() throws WriterException {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("QR 코드를 스캔하여 출발 위치를 설정합니다."); // 스캔 화면에 표시할 문구 설정 (선택 사항)
        integrator.setOrientationLocked(false); // 화면 방향 고정 해제 (선택 사항)
        integrator.initiateScan(); // 스캔 시작
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "스캔 취소!", Toast.LENGTH_SHORT).show();
                finish(); // QR코드 스캔 중 뒤로가기 하였을 때, 전 액티비티로 돌아가게 하기 위해 추가함.
            } else {
                String qrCodeData = result.getContents();
                processQrCodeData(qrCodeData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processQrCodeData(String qrCodeData) {
        // QR 코드 데이터 형식: 건물번호,층,x좌표f,y좌표f
        String[] parts = qrCodeData.split(",");
        if (parts.length == 4) {
            try {
                int buildingNumber = Integer.parseInt(parts[0]);
                int floor = Integer.parseInt(parts[1]);
                float startX = Float.parseFloat(parts[2]);
                float startY = Float.parseFloat(parts[3]);

                // TODO: 스캔한 정보를 바탕으로 해당 건물 및 층의 BeaconimageActivity를 시작하고
                // 출발 좌표 정보를 Intent에 담아 전달합니다.

                Intent intent = null;
                // 건물 번호와 층에 따라 적절한 BeaconimageActivity를 선택합니다.
                // 이 부분은 실제 건물 및 층 정보에 따라 수정해야 합니다.
                if (buildingNumber == 1/* 상경학관 건물 번호 */ && floor == 1) {
                    intent = new Intent(this, BeaconImageActivity2_2_L.class);
                }
                else if (buildingNumber == 2 && floor == 1) {
                    intent = new Intent(this, BeaconImageActivity2_1_L.class);
                    intent.putExtra("entrance_name", "공학L관 1층");
                }
                else if(buildingNumber == 2 && floor == 2){
                    intent = new Intent(this, BeaconImageActivity2_2_L.class);
                    intent.putExtra("entrance_name", "공학L관 2층");
                }
                else if(buildingNumber == 3 && floor == 1){
                    intent = new Intent(this, BeaconImageActivity3_1_C.class);
                    intent.putExtra("entrance_name", "상경학관 1층");
                }
                else{
                    intent = new Intent(this, BeaconImageActivity3_1_L.class);
                }

                if (intent != null) {
                    intent.putExtra("start_x", startX);
                    intent.putExtra("start_y", startY);
                    startActivity(intent);
                    finish(); // QR 코드 스캔 액티비티 종료
                } else {
                    Toast.makeText(this, "해당 건물 및 층의 약도 액티비티를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    try {
                        startQrCodeScanner(); // 스캔 재시작 (try-catch로 감싸기)
                    } catch (WriterException e) {
                        e.printStackTrace(); // 또는 다른 방식으로 예외 처리
                        Toast.makeText(this, "QR 코드 스캔 시작 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "QR 코드 데이터 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                try {
                    startQrCodeScanner(); // 스캔 재시작 (try-catch로 감싸기)
                } catch (WriterException e1) {
                    e.printStackTrace(); // 또는 다른 방식으로 예외 처리
                    Toast.makeText(this, "QR 코드 스캔 시작 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "QR 코드 데이터 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            try {
                startQrCodeScanner(); // 스캔 재시작 (try-catch로 감싸기)
            } catch (WriterException e) {
                e.printStackTrace(); // 또는 다른 방식으로 예외 처리
                Toast.makeText(this, "QR 코드 스캔 시작 중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        }
    }
}