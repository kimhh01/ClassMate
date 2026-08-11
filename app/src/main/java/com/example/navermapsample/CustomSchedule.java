package com.example.navermapsample;

import com.github.tlaabs.timetableview.Schedule;

public class CustomSchedule {
    private String subjectName;
    private String professor;
    private String room;

    // 생성자
    public CustomSchedule(String subjectName, String professor, String room) {
        this.subjectName = subjectName;
        this.professor = professor;
        this.room = room;
    }

    // Getter 메소드들
    public String getSubjectName() {
        return subjectName;
    }

    public String getProfessor() {
        return professor;
    }

    public String getRoom() {
        return room;
    }
}

