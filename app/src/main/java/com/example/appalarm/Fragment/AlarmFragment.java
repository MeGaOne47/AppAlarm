package com.example.appalarm.Fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalarm.Sqlite.Alarm;
import com.example.appalarm.Sqlite.AlarmDatabaseHelper;
import com.example.appalarm.Notification.AlarmReceiver;
import com.example.appalarm.R;

import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment {

    private static final int ALARM_REQUEST_CODE = 1;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private TimePicker timePicker;
    private AlarmDatabaseHelper databaseHelper;
    private List<Alarm> alarmList;
    private ListView alarmListView;
    private ArrayAdapter<Alarm> alarmListAdapter;
    private int selectedAlarmPosition = -1; // Vị trí báo thức được chọn

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        databaseHelper = new AlarmDatabaseHelper(getContext());
        alarmList = databaseHelper.getAllAlarms();

        timePicker = rootView.findViewById(R.id.timePicker);
        Button setAlarmButton = rootView.findViewById(R.id.setAlarmButton);
        Button cancelAlarmButton = rootView.findViewById(R.id.cancelAlarmButton);
        Button toggleAlarmButton = rootView.findViewById(R.id.toggleAlarmButton); // Thêm nút bật/tắt báo thức

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        toggleAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAlarm();
            }
        });

        alarmListView = rootView.findViewById(R.id.alarmListView);
        alarmListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, alarmList);
        alarmListView.setAdapter(alarmListAdapter);

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lưu vị trí báo thức được chọn
                selectedAlarmPosition = position;

                // Hiển thị trạng thái bật/tắt của báo thức
                Alarm selectedAlarm = alarmList.get(selectedAlarmPosition);
                toggleAlarmButton.setText(selectedAlarm.isEnabled() ? "Turn Off" : "Turn On");
            }
        });

        alarmListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Hiển thị cửa sổ xác nhận xóa báo thức khi người dùng giữ một mục trong danh sách.
                showDeleteConfirmation(position);
                return true;
            }
        });

        return rootView;
    }

    private void toggleAlarm() {
        if (selectedAlarmPosition == -1) {
            return; // No alarm selected
        }

        Alarm alarm = alarmList.get(selectedAlarmPosition);
        alarm.setEnabled(!alarm.isEnabled());
        databaseHelper.updateAlarm(alarm);
        alarmListAdapter.notifyDataSetChanged();

        // Start or stop the alarm sound accordingly.
        if (alarm.isEnabled()) {
            setAlarm(alarm);
        } else {
            cancelAlarm(alarm.getId());
        }

        // Update the button text to reflect the new state
        Button toggleAlarmButton = getActivity().findViewById(R.id.toggleAlarmButton);
        toggleAlarmButton.setText(alarm.isEnabled() ? "Turn Off" : "Turn On");
    }

    private void showDeleteConfirmation(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Alarm");
        builder.setMessage("Are you sure you want to delete this alarm?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAlarm(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteAlarm(int position) {
        Alarm alarm = alarmList.get(position);
        databaseHelper.deleteAlarm(alarm);
        alarmList.remove(alarm);
        alarmListAdapter.notifyDataSetChanged();
        selectedAlarmPosition = -1; // Reset lại vị trí báo thức được chọn
        Toast.makeText(getContext(), "Alarm deleted!", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Alarm alarm = new Alarm();
        alarm.setHour(hour);
        alarm.setMinute(minute);
        alarm.setEnabled(true);

        // Save the new alarm to the database
        long id = databaseHelper.addAlarm(alarm);
        alarm.setId((int) id);
        alarmList.add(alarm);

        // Set the alarm using the new alarm object
        setAlarm(alarm);

        // Update Adapter and ListView
        alarmListAdapter.notifyDataSetChanged();
    }

    private void setAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        long alarmTime = calendar.getTimeInMillis();
        if (alarmTime <= System.currentTimeMillis()) {
            // If the selected time is in the past, set the alarm for the next day
            alarmTime += AlarmManager.INTERVAL_DAY;
        }

        // Calculate the delay from the current time to the alarm time
        long delay = alarmTime - System.currentTimeMillis();

        // Set the alarm with the delay
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, alarmIntent);
    }

    private void cancelAlarm() {
        if (selectedAlarmPosition == -1) {
            return; // Không có báo thức nào được chọn
        }

        Alarm alarm = alarmList.get(selectedAlarmPosition);
        cancelAlarm(alarm.getId());
        selectedAlarmPosition = -1; // Reset lại vị trí báo thức được chọn
    }

    private void cancelAlarm(int alarmId) {
        // Hủy báo thức dựa vào ID của báo thức
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);

        // Stop playing the sound (if it's playing).
        AlarmReceiver.stopAlarm();
    }
}
