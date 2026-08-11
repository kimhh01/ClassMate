package com.example.navermapsample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TimetableActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private ImageButton addBtn;
    private TimetableView timetable;

    private TextView classroomEdit;
    private TextView professorEdit;

    private ArrayList<Schedule> timetableData;  // 시간표 데이터를 직접 관리하는 ArrayList
    private Schedule selectedSchedule;  // 현재 선택된 스케줄을 저장하는 변수 추가
    private int selectedScheduleIndex;  // 선택된 스케줄의 인덱스를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        init();

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("시간표"); // 타이틀 설정
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // or onBackPressed();
        return true;
    }

    private void init() {
        this.context = this;
        addBtn = findViewById(R.id.add_btn);
        timetable = findViewById(R.id.timetable);
        classroomEdit = findViewById(R.id.classroom_edit);
        professorEdit = findViewById(R.id.professor_edit);

        //timetable.setHeaderHighlight(0);  // Customize header color if needed
        loadSavedData();  // ✅ 앱 시작 시 자동 로드
        initView();
    }

    private void initView() {
        addBtn.setOnClickListener(this);

        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // schedules가 null 또는 비어있는지 확인
                if (schedules == null || schedules.isEmpty()) {
                    return;
                }

                // 선택된 스케줄 저장
                selectedSchedule = schedules.get(0);

                // 실제 timetableData에서 해당 스케줄의 인덱스 찾기
                selectedScheduleIndex = findScheduleIndex(selectedSchedule);

                // Schedule 객체에서 필요한 정보 추출
                String subjectName = selectedSchedule.getClassTitle();  // 강의명
                String professorName = selectedSchedule.getProfessorName();     // 교수명
                String room = selectedSchedule.getClassPlace() != null ? selectedSchedule.getClassPlace() : "미정";  // Room을 실제로 설정된 값으로 받기

                String timeStr = selectedSchedule.getDay() + " " +
                        (selectedSchedule.getStartTime().getHour() == 0 ? "" : selectedSchedule.getStartTime().getHour()) + ":" +
                        String.format("%02d", selectedSchedule.getStartTime().getMinute()) + " ~ " +
                        (selectedSchedule.getEndTime().getHour() == 0 ? "" : selectedSchedule.getEndTime().getHour()) + ":" +
                        String.format("%02d", selectedSchedule.getEndTime().getMinute());

                // 데이터 표시
                if (classroomEdit != null) {
                    classroomEdit.setText(room);
                }
                if (professorEdit != null) {
                    professorEdit.setText(professorName);
                }

                // 다이얼로그 띄우기
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_schedule_options, null);

                TextView title = dialogView.findViewById(R.id.dialog_title);
                TextView professor = dialogView.findViewById(R.id.dialog_professor);
                TextView location = dialogView.findViewById(R.id.dialog_location);
                TextView time = dialogView.findViewById(R.id.dialog_time);
                Button navBtn = dialogView.findViewById(R.id.nav_btn);
                Button editBtn = dialogView.findViewById(R.id.edit_btn);
                Button deleteBtn = dialogView.findViewById(R.id.delete_btn);

                // 기존 강의명 표시
                if (title != null) title.setText(subjectName);
                if (professor != null) professor.setText("교수명: " + professorName);
                if (location != null) location.setText("장소: " + room);
                if (time != null) time.setText("시간: " + timeStr);

                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                navBtn.setOnClickListener(v -> {
                    // MainActivity로 강의실 이름을 전달
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("classroom", room);
                    startActivity(intent);
                    dialog.dismiss();
                });

                editBtn.setOnClickListener(v -> {
                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra("mode", REQUEST_EDIT);
                    i.putExtra("schedules", schedules); // 수정할 스케줄 데이터 전달 (현재 클릭된 스티커의 스케줄)
                    startActivityForResult(i, REQUEST_EDIT);
                    dialog.dismiss();
                });

                deleteBtn.setOnClickListener(v -> {
                    if (selectedScheduleIndex != -1) {
                        // 찾은 인덱스로 직접 제거
                        timetableData.remove(selectedScheduleIndex);
                        saveByPreference(timetableData);     // 변경된 timetableData 저장
                        timetable.removeAll();               // TimetableView에서 모든 스티커 제거
                        loadSavedData();                     // 저장된 데이터로 TimetableView 다시 그리기
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기존 방식으로 삭제 시도 (fallback)
                        for (Schedule s : schedules) {
                            timetableData.removeIf(item ->
                                    item.getClassTitle().equals(s.getClassTitle()) &&
                                            item.getDay() == s.getDay() &&
                                            item.getStartTime().getHour() == s.getStartTime().getHour() &&
                                            item.getStartTime().getMinute() == s.getStartTime().getMinute()
                            );
                        }
                        saveByPreference(timetableData);
                        timetable.removeAll();
                        loadSavedData();
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });

                dialog.show();
            }
        });
    }

    /**
     * timetableData에서 주어진 스케줄과 일치하는 항목의 인덱스를 찾는 메서드
     * @param schedule 찾을 스케줄
     * @return 일치하는 스케줄의 인덱스, 없으면 -1 반환
     */
    private int findScheduleIndex(Schedule schedule) {
        if (schedule == null || timetableData == null) {
            return -1;
        }

        for (int i = 0; i < timetableData.size(); i++) {
            Schedule current = timetableData.get(i);

            // 강의명, 요일, 시작 시간이 모두 일치하는지 확인
            if (current.getClassTitle().equals(schedule.getClassTitle()) &&
                    current.getDay() == schedule.getDay() &&
                    current.getStartTime().getHour() == schedule.getStartTime().getHour() &&
                    current.getStartTime().getMinute() == schedule.getStartTime().getMinute()) {
                return i;
            }
        }

        return -1;  // 일치하는 스케줄을 찾지 못함
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.add_btn) {
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra("mode", REQUEST_ADD);
            startActivityForResult(i, REQUEST_ADD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == EditActivity.RESULT_OK_ADD) {
                    ArrayList<Schedule> newSchedules = (ArrayList<Schedule>) data.getSerializableExtra("schedules");

                    // 새로 추가된 시간표가 기존 시간표와 겹치는지 체크
                    boolean addOverlaps = false; // REQUEST_ADD용 overlap 변수 선언
                    if (newSchedules != null && !newSchedules.isEmpty()) {
                        for (Schedule newSchedule : newSchedules) {
                            int selectedDay = newSchedule.getDay();
                            int startTotal = newSchedule.getStartTime().getHour() * 60 + newSchedule.getStartTime().getMinute();
                            int endTotal = newSchedule.getEndTime().getHour() * 60 + newSchedule.getEndTime().getMinute();

                            // 시간표 겹침 체크
                            for (Schedule existingSchedule : timetableData) {
                                if (existingSchedule.getDay() != selectedDay) continue;

                                int existingStart = existingSchedule.getStartTime().getHour() * 60 + existingSchedule.getStartTime().getMinute();
                                int existingEnd = existingSchedule.getEndTime().getHour() * 60 + existingSchedule.getEndTime().getMinute();

                                // 겹치는 시간 체크
                                if (startTotal < existingEnd && endTotal > existingStart) {
                                    String classTitle = existingSchedule.getClassTitle();
                                    Toast.makeText(context, "다른 과목 \"" + classTitle + "\" 와 시간이 겹칩니다. 다시 설정해주세요.", Toast.LENGTH_LONG).show();
                                    addOverlaps = true; // 겹침 발생
                                    break; // 겹침 발생 시 내부 for 루프 종료
                                }
                            }
                            if (addOverlaps) break; // 겹침 발생 시 외부 for 루프 종료
                        }

                        if (!addOverlaps) { // 겹침이 없으면 추가 진행
                            // 시간표에 추가
                            for (Schedule s : newSchedules) {
                                timetableData.add(s); // timetableData에 추가
                            }
                            saveByPreference(timetableData); // timetableData 변경 사항 저장
                            timetable.removeAll();           // TimetableView에서 모든 스티커 제거
                            loadSavedData();                 // 저장된 데이터로 TimetableView 다시 그리기
                            Toast.makeText(context, "시간표가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case REQUEST_EDIT:
                if (resultCode == EditActivity.RESULT_OK_EDIT) {
                    ArrayList<Schedule> updatedSchedules = (ArrayList<Schedule>) data.getSerializableExtra("schedules");

                    if (updatedSchedules == null || updatedSchedules.isEmpty()) {
                        return;
                    }

                    Schedule newSchedule = updatedSchedules.get(0);

                    // 선택된 스케줄이 유효한지 확인
                    if (selectedSchedule == null || selectedScheduleIndex == -1) {
                        Toast.makeText(context, "수정할 시간표를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 수정된 시간표가 다른 시간표와 겹치는지 체크
                    boolean overlaps = false;
                    for (int i = 0; i < timetableData.size(); i++) {
                        // 자기 자신과는 비교하지 않음 (selectedScheduleIndex와 i가 같으면 건너뜀)
                        if (i == selectedScheduleIndex) {
                            continue;
                        }

                        Schedule existingSchedule = timetableData.get(i);
                        if (existingSchedule.getDay() != newSchedule.getDay()) continue;

                        int newStartTotal = newSchedule.getStartTime().getHour() * 60 + newSchedule.getStartTime().getMinute();
                        int newEndTotal = newSchedule.getEndTime().getHour() * 60 + newSchedule.getEndTime().getMinute();
                        int existingStart = existingSchedule.getStartTime().getHour() * 60 + existingSchedule.getStartTime().getMinute();
                        int existingEnd = existingSchedule.getEndTime().getHour() * 60 + existingSchedule.getEndTime().getMinute();

                        if (newStartTotal < existingEnd && newEndTotal > existingStart) {
                            String classTitle = existingSchedule.getClassTitle();
                            Toast.makeText(context, "다른 과목 \"" + classTitle + "\" 와 시간이 겹칩니다. 다시 설정해주세요.", Toast.LENGTH_LONG).show();
                            overlaps = true;
                            break;
                        }
                    }

                    if (overlaps) {
                        return; // 겹침이 감지되면 수정 취소
                    }

                    // 겹침이 없으면 timetableData 업데이트
                    timetableData.set(selectedScheduleIndex, newSchedule);

                    // 데이터 변경 후 TimetableView를 완전히 새로고침하여 UI와 데이터 동기화
                    saveByPreference(timetableData); // timetableData 변경 사항 저장
                    timetable.removeAll();           // TimetableView에서 모든 스티커 제거
                    loadSavedData();                 // 저장된 데이터로 TimetableView 다시 그리기
                    Toast.makeText(context, "시간표가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("timetable", MODE_PRIVATE);
        String savedData = prefs.getString("schedule_data", "[]");

        timetableData = new Gson().fromJson(savedData, new TypeToken<ArrayList<Schedule>>(){}.getType());
        if (timetableData == null) {
            timetableData = new ArrayList<>();
        }

        // 각 Schedule을 개별 리스트로 만들어 TimetableView에 추가
        // (removeAll()을 호출했으므로 기존 스티커들은 이미 제거된 상태)
        for (Schedule schedule : timetableData) {
            ArrayList<Schedule> singleList = new ArrayList<>();
            singleList.add(schedule);
            timetable.add(singleList);  // 개별 스티커로 추가
        }
    }

    private void saveByPreference(ArrayList<Schedule> data) {
        SharedPreferences prefs = getSharedPreferences("timetable", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String jsonData = new Gson().toJson(data);  // ArrayList를 JSON으로 변환
        editor.putString("schedule_data", jsonData);
        editor.apply();
    }
}