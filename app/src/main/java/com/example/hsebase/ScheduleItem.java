package com.example.hsebase;

public class ScheduleItem {
    private String start;
    private String end;
    private String type;
    private String name;
    private String place;
    private String teacher;

    public void setStart(String s) {
        start = s;
    }

    public void setEnd(String s) {
        end = s;
    }

    public void setType(String s) {
        type = s;
    }

    public void setName(String s) {
        name = s;
    }

    public void setPlace(String s) {
        place = s;
    }

    public void setTeacher(String s) {
        teacher = s;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getType() {
        return type;
    }
}
