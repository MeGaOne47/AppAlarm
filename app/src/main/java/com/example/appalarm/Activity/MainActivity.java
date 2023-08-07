package com.example.appalarm.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appalarm.Fragment.AlarmFragment;
import com.example.appalarm.Login;
import com.example.appalarm.R;
import com.example.appalarm.Fragment.TimerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private AlarmFragment alarmFragment;
    private TimerFragment timerFragment;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmFragment = new AlarmFragment();
        timerFragment = new TimerFragment();
        fragmentManager = getSupportFragmentManager();
        currentFragment = alarmFragment;

        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, currentFragment).commit();

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onAlarmButtonClick(View view) {
        switchFragment(alarmFragment);
    }

    public void onTimerButtonClick(View view) {
        switchFragment(timerFragment);
    }

    private void switchFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            currentFragment = fragment;
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, currentFragment).commit();
        }
    }
}
