package com.mfahproj.webapp.models;

public class ExhibitionAttendanceReport {
    private String attendanceTitle;
    private int attendance;

    public ExhibitionAttendanceReport(String attendanceTitle, int attendance) {
        this.attendanceTitle = attendanceTitle;
        this.attendance = attendance;
    }

    public  ExhibitionAttendanceReport(){
        this.attendanceTitle = "";
        this.attendance = 0;
    }

    public String getAttendanceTitle() {
        return attendanceTitle;
    }

    public void setAttendanceTitle(String attendanceTitle) {
        this.attendanceTitle = attendanceTitle;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

//String representation of the model=
    @Override
    public String toString() {
        return "ExhibitionAttendanceReport{" +
                "attendanceTitle='" + attendanceTitle + '\'' +
                ", attendance=" + attendance +
                '}';
    }
}
