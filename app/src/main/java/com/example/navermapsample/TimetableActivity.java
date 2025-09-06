package com.example.navermapsample;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TimetableActivity extends AppCompatActivity {

    private GridLayout gridTimetable;
    private ImageButton btnAdd, btnEdit;
    private Set<Integer> usedColors = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        gridTimetable = findViewById(R.id.gridTimetable);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);

        btnAdd.setOnClickListener(v -> showAddClassDialog());
        btnEdit.setOnClickListener(v ->
                Toast.makeText(TimetableActivity.this, "수정 모드 활성화", Toast.LENGTH_SHORT).show()
        );
    }

    private void showAddClassDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);

        EditText editClassName = dialogView.findViewById(R.id.editClassName);
        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        TextView textStartTime = dialogView.findViewById(R.id.textStartTime);
        TextView textEndTime = dialogView.findViewById(R.id.textEndTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.weekdays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);

        textStartTime.setOnClickListener(v -> showTimePickerDialog(textStartTime));
        textEndTime.setOnClickListener(v -> showTimePickerDialog(textEndTime));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("수업 추가")
                .setPositiveButton("완료", (dialog, which) -> {
                    String className = editClassName.getText().toString();
                    String selectedDay = spinnerDay.getSelectedItem().toString();
                    String startTime = textStartTime.getText().toString();
                    String endTime = textEndTime.getText().toString();

                    if (validateInputs(className, startTime, endTime)) {
                        addClassToTimetable(className, selectedDay, startTime, endTime);
                    } else {
                        Toast.makeText(this, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showTimePickerDialog(TextView timeField) {
        int hour = 9, minute = 0; // 기본값: 9시
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    selectedMinute = (selectedMinute / 5) * 5;
                    timeField.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private boolean validateInputs(String className, String startTime, String endTime) {
        if (className.isEmpty()) return false;

        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);

        if (startHour < 9 || startHour > 23 || endHour < 9 || endHour > 23) return false;
        return startHour < endHour; // 시작 시간이 종료 시간보다 빨라야 함
    }

    private void addClassToTimetable(String className, String day, String startTime, String endTime) {
        int dayColumn = getDayColumn(day);
        int startRow = getTimeRow(startTime)+1;
        int endRow = getTimeRow(endTime)+1;

        if (dayColumn == -1 || startRow == -1 || endRow == -1 || startRow >= endRow) {
            Toast.makeText(this, "시간이나 요일이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView classView = new TextView(this);
        classView.setText(className);
        classView.setBackgroundColor(generateUniqueColor());
        classView.setGravity(Gravity.CENTER);
        classView.setPadding(0, 0, 0, 0);
        classView.setTextColor(Color.WHITE);

        int rowHeight = getResources().getDimensionPixelSize(R.dimen.row_height); // 행 높이를 dimens에서 불러오기
        classView.setMinHeight(rowHeight * (endRow - startRow)); // 행 높이에 따른 최소 높이 설정

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.rowSpec = GridLayout.spec(startRow, endRow - startRow); // 시간 범위
        params.columnSpec = GridLayout.spec(dayColumn, 1f); // 요일 열, 1f는 가중치를 의미

        params.height = GridLayout.LayoutParams.MATCH_PARENT; // 높이를 부모에 맞추기
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(4, 4, 4, 4);

        gridTimetable.addView(classView, params);
    }

    private int getDayColumn(String day) {
        switch (day) {
            case "월": return 1;
            case "화": return 2;
            case "수": return 3;
            case "목": return 4;
            case "금": return 5;
            default: return -1;
        }
    }

    private int getTimeRow(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        return hour >= 9 && hour <= 23 ? hour - 9 : -1;
    }

    private int generateUniqueColor() {
        Random random = new Random();
        int color;
        do {
            color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while (usedColors.contains(color));
        usedColors.add(color);
        return color;
    }
}
