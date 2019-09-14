package com.example.pacemaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ProgressBar;

public class AccountActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FragmentA fragmentA;
    private FragmentB fragmentB;
    private FragmentC fragmentC;
    private FragmentTransaction fragmentTransaction;
    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViews();
        setProgressBar();

        FragmentManager fm = getSupportFragmentManager();
        fragmentA = new FragmentA();
        fragmentB = new FragmentB();
        fragmentC = new FragmentC();
        fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_layout, fragmentA).commitAllowingStateLoss();



    }

    private void findViews() {
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setProgressBar() {

        progressBar.setBackgroundColor(getResources().getColor(R.color.background_end));
        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_start)));
        progressBar.setProgress(7);
    }
}
