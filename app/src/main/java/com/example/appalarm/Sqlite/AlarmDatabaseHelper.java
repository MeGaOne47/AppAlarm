package com.example.appalarm.Sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.appalarm.Sqlite.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarm_database";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ALARMS = "alarms";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HOUR = "hour";
    private static final String COLUMN_MINUTE = "minute";
    private static final String COLUMN_ENABLED = "enabled";


    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_ALARMS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HOUR + " INTEGER, " +
                COLUMN_MINUTE + " INTEGER, " +
                COLUMN_ENABLED + " INTEGER" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }

    public long addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_ENABLED, alarm.isEnabled() ? 1 : 0);
        return db.insert(TABLE_ALARMS, null, values);
    }

    public int updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_ENABLED, alarm.isEnabled() ? 1 : 0);
        return db.update(TABLE_ALARMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(alarm.getId())});
    }

    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, COLUMN_ID + " = ?", new String[]{String.valueOf(alarm.getId())});
        db.close();
    }

    @SuppressLint("Range")
    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                alarm.setHour(cursor.getInt(cursor.getColumnIndex(COLUMN_HOUR)));
                alarm.setMinute(cursor.getInt(cursor.getColumnIndex(COLUMN_MINUTE)));
                alarm.setEnabled(cursor.getInt(cursor.getColumnIndex(COLUMN_ENABLED)) == 1);
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alarmList;
    }

    // Thêm phương thức để lấy một báo thức theo ID
    @SuppressLint("Range")
    public Alarm getAlarmById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARMS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        Alarm alarm = null;
        if (cursor.moveToFirst()) {
            alarm = new Alarm();
            alarm.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            alarm.setHour(cursor.getInt(cursor.getColumnIndex(COLUMN_HOUR)));
            alarm.setMinute(cursor.getInt(cursor.getColumnIndex(COLUMN_MINUTE)));
            alarm.setEnabled(cursor.getInt(cursor.getColumnIndex(COLUMN_ENABLED)) == 1);
        }

        cursor.close();
        return alarm;
    }

    // Thêm phương thức để lấy danh sách các báo thức đã bật
    public List<Alarm> getEnabledAlarms() {
        List<Alarm> enabledAlarms = new ArrayList<>();
        List<Alarm> allAlarms = getAllAlarms();
        for (Alarm alarm : allAlarms) {
            if (alarm.isEnabled()) {
                enabledAlarms.add(alarm);
            }
        }
        return enabledAlarms;
    }
}
