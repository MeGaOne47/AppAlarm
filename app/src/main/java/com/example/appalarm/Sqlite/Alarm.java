// Alarm.java
package com.example.appalarm.Sqlite;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private boolean enabled;

    public Alarm() {
    }

    public Alarm(int id, int hour, int minute, boolean enabled) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }
}


