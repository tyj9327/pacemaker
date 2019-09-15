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
        setProgressBar(3);

        final FragmentManager fm = getSupportFragmentManager();
        fragmentA = new FragmentA();
        fragmentB = new FragmentB();
        fragmentC = new FragmentC();
        fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_layout, fragmentA, "FRAGMENT_A").commitAllowingStateLoss();
        fragmentTransaction.addToBackStack("FRAGMENT_A");


        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentA fragment1 = (FragmentA) fm.findFragmentByTag("FRAGMENT_A");
                FragmentB fragment2 = (FragmentB) fm.findFragmentByTag("FRAGMENT_B");
                FragmentC fragment3 = (FragmentC) fm.findFragmentByTag("FRAGMENT_C");

                if(fragment1 != null && fragment1.isVisible()) {
                    setProgressBar(3);
                } else if(fragment2 != null && fragment2.isVisible()) {
                    setProgressBar(7);
                } else if(fragment3 != null && fragment3.isVisible()) {
                    setProgressBar(10);
                }
            }
        });


    }

    private void findViews() {
        progressBar = findViewById(R.id.progress_bar);
    }

    public void setProgressBar(int progress) {

        progressBar.setBackgroundColor(getResources().getColor(R.color.background_end));
        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_start)));
        progressBar.setProgress(progress);
    }
}
