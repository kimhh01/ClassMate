package com.example.navermapsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private Context context;

    private Button deleteBtn;
    private Button submitBtn;
    private EditText subjectEdit;
    private EditText classroomEdit;
    private EditText professorEdit;
    private Spinner daySpinner;

    private Spinner startHourSpinner;
    private Spinner startMinuteSpinner;
    private Spinner endHourSpinner;
    private Spinner endMinuteSpinner;

    private int mode;
    private Schedule schedule;
    private int editIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        context = this;

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("강의 수정"); // 타이틀 설정
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        initViews(); // 뷰 초기화
        initScheduleDefaults(); // 기본 시간표 세팅
        setupTimeSpinners(); // 시간 스피너 세팅
        checkModeAndLoadData(); // 모드에 맞는 데이터 로드
        setupListeners(); // 리스너 설정


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // or onBackPressed();
        return true;
    }

    private void initViews() {
        deleteBtn = findViewById(R.id.delete_btn);
        submitBtn = findViewById(R.id.submit_btn);
        subjectEdit = findViewById(R.id.subject_edit);
        classroomEdit = findViewById(R.id.classroom_edit);
        professorEdit = findViewById(R.id.professor_edit);
        daySpinner = findViewById(R.id.day_spinner);
        startHourSpinner = findViewById(R.id.start_hour_spinner);
        startMinuteSpinner = findViewById(R.id.start_minute_spinner);
        endHourSpinner = findViewById(R.id.end_hour_spinner);
        endMinuteSpinner = findViewById(R.id.end_minute_spinner);
    }

    private void initScheduleDefaults() {
        schedule = new Schedule();
        schedule.setStartTime(new Time(10, 0));
        schedule.setEndTime(new Time(13, 30));
    }

    private void setupTimeSpinners() {
        // 시간 스피너와 분 스피너를 설정하기 위한 어댑터 생성
        ArrayAdapter<Integer> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getNumbers(9, 19));
        ArrayAdapter<Integer> minuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getNumbers(0, 59, 5));

        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 어댑터 설정
        startHourSpinner.setAdapter(hourAdapter);
        startMinuteSpinner.setAdapter(minuteAdapter);
        endHourSpinner.setAdapter(hourAdapter);
        endMinuteSpinner.setAdapter(minuteAdapter);
    }

    private ArrayList<Integer> getNumbers(int from, int to) {
        return getNumbers(from, to, 1);
    }

    private ArrayList<Integer> getNumbers(int from, int to, int step) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = from; i <= to; i += step) {
            list.add(i);
        }
        return list;
    }

    private void checkModeAndLoadData() {
        Intent i = getIntent();
        mode = i.getIntExtra("mode", TimetableActivity.REQUEST_ADD);

        if (mode == TimetableActivity.REQUEST_EDIT) {
            deleteBtn.setVisibility(View.VISIBLE);
            editIdx = i.getIntExtra("idx", -1);
            ArrayList<Schedule> schedules = (ArrayList<Schedule>) i.getSerializableExtra("schedules");
            if (schedules != null && !schedules.isEmpty()) {
                schedule = schedules.get(0);
                loadScheduleData();
            }
        }
    }

    private void loadScheduleData() {
        subjectEdit.setText(schedule.getClassTitle());
        classroomEdit.setText(schedule.getClassPlace());
        professorEdit.setText(schedule.getProfessorName());
        daySpinner.setSelection(schedule.getDay());

        int startHour = schedule.getStartTime().getHour();
        int startMinute = schedule.getStartTime().getMinute();
        int endHour = schedule.getEndTime().getHour();
        int endMinute = schedule.getEndTime().getMinute();

        // 시간 값 -> 인덱스로 변환
        startHourSpinner.setSelection(startHour - 9); // 9시부터 시작
        endHourSpinner.setSelection(endHour - 9);

        startMinuteSpinner.setSelection(startMinute / 5);
        endMinuteSpinner.setSelection(endMinute / 5);
    }


    private void setupListeners() {
        submitBtn.setOnClickListener(v -> {

            // 강의명 확인
            String subject = subjectEdit.getText().toString().trim();
            if (subject.isEmpty()) {
                Toast.makeText(context, "강의명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 선택된 시간 추출
            int startHour = (Integer) startHourSpinner.getSelectedItem();
            int startMinute = (Integer) startMinuteSpinner.getSelectedItem();
            int endHour = (Integer) endHourSpinner.getSelectedItem();
            int endMinute = (Integer) endMinuteSpinner.getSelectedItem();

            int startTotal = startHour * 60 + startMinute;
            int endTotal = endHour * 60 + endMinute;

            // 시간 순서 체크
            // 시간 순서 체크
            if (startTotal == endTotal) {
                Toast.makeText(context, "시작 시간과 종료 시간이 같습니다. 시간을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (startTotal > endTotal) {
                Toast.makeText(context, "종료 시간이 시작 시간보다 빠릅니다. 시간을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 겹치는 시간 체크 (수정 모드가 아닌 경우만)
            if (mode == TimetableActivity.REQUEST_ADD || mode == TimetableActivity.REQUEST_EDIT) {
                int selectedDay = daySpinner.getSelectedItemPosition();
                ArrayList<Schedule> schedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("all_schedules");
                if (schedules != null) {
                    for (int i = 0; i < schedules.size(); i++) {
                        if (mode == TimetableActivity.REQUEST_EDIT && i == editIdx) continue; // 수정 중인 자기 자신은 제외

                        Schedule s = schedules.get(i);
                        if (s.getDay() != selectedDay) continue;

                        int sStart = s.getStartTime().getHour() * 60 + s.getStartTime().getMinute();
                        int sEnd = s.getEndTime().getHour() * 60 + s.getEndTime().getMinute();

                        boolean isOverlap = startTotal < sEnd && endTotal > sStart;
                        if (isOverlap) {
                            String classTitle = s.getClassTitle();
                            Toast.makeText(context, "다른 과목 \"" + classTitle + "\" 와 시간이 겹칩니다. 다시 설정해주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            // 정상 저장
            inputDataProcessing();
            Intent resultIntent = new Intent();
            ArrayList<Schedule> schedules = new ArrayList<>();
            schedules.add(schedule);
            resultIntent.putExtra("schedules", schedules);

            if (mode == TimetableActivity.REQUEST_ADD) {
                setResult(RESULT_OK_ADD, resultIntent);
            } else if (mode == TimetableActivity.REQUEST_EDIT) {
                resultIntent.putExtra("idx", editIdx);
                setResult(RESULT_OK_EDIT, resultIntent);
            }

            finish();
        });



        deleteBtn.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("idx", editIdx);
            setResult(RESULT_OK_DELETE, resultIntent);
            finish();
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void inputDataProcessing() {
        schedule.setClassTitle(subjectEdit.getText().toString().trim());
        schedule.setClassPlace(classroomEdit.getText().toString().trim());
        schedule.setProfessorName(professorEdit.getText().toString().trim());

        int startHour = (Integer) startHourSpinner.getSelectedItem();
        int startMinute = (Integer) startMinuteSpinner.getSelectedItem();
        int endHour = (Integer) endHourSpinner.getSelectedItem();
        int endMinute = (Integer) endMinuteSpinner.getSelectedItem();

        schedule.setStartTime(new Time(startHour, startMinute));
        schedule.setEndTime(new Time(endHour, endMinute));
    }
}
