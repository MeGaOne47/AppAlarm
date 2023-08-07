package com.example.appalarm.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimerFragment extends Fragment {

    private long startTimeInMillis;
    private long timeLeftInMillis;
    private boolean isRunning;
    private Handler handler;
    private TextView timerText;
    private Button startButton;
    private Button stopButton;
    private Button lapButton;
    private ListView lapListView;
    private ArrayAdapter<String> lapListAdapter;
    private List<String> lapTimeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        timerText = rootView.findViewById(R.id.timerText);
        startButton = rootView.findViewById(R.id.startButton);
        stopButton = rootView.findViewById(R.id.stopButton);
        lapButton = rootView.findViewById(R.id.lapButton);
        lapListView = rootView.findViewById(R.id.lapListView);

        handler = new Handler();
        lapTimeList = new ArrayList<>();
        lapListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lapTimeList);
        lapListView.setAdapter(lapListAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    saveLapTime();
                }
            }
        });

        return rootView;
    }

    private void startTimer() {
        if (!isRunning) {
            lapTimeList.clear();
            lapListAdapter.notifyDataSetChanged();

            startTimeInMillis = System.currentTimeMillis();
            handler.postDelayed(runnable, 10);
            isRunning = true;
            updateButtons();
        }
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        isRunning = false;
        updateButtons();
    }

    private void saveLapTime() {
        long lapTime = timeLeftInMillis;
        String lapTimeFormatted = formatTime(lapTime);
        lapTimeList.add(0, lapTimeFormatted);
        lapListAdapter.notifyDataSetChanged();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        int milliseconds = (int) (timeLeftInMillis % 1000) / 10;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, milliseconds);
        timerText.setText(timeFormatted);
    }

    private void updateButtons() {
        startButton.setEnabled(!isRunning);
        stopButton.setEnabled(isRunning);
        lapButton.setEnabled(isRunning);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeLeftInMillis = System.currentTimeMillis() - startTimeInMillis;
            updateTimerText();
            handler.postDelayed(this, 10);
        }
    };

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 1000) / 60;
        int seconds = (int) (timeInMillis / 1000) % 60;
        int milliseconds = (int) (timeInMillis % 1000) / 10;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, milliseconds);
    }
}
