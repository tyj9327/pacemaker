package com.example.pacemaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentB extends Fragment {

    private String gender = "NONE";

    public FragmentB() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_b, container, false);

        ImageView nextButton = (ImageView) view.findViewById(R.id.next_icon_b);
        Button maleButton = (Button) view.findViewById(R.id.button_male);
        Button femaleButton = (Button) view.findViewById(R.id.button_female);

        maleButton.setFocusable(true);
        maleButton.setFocusableInTouchMode(true);
        femaleButton.setFocusable(true);
        femaleButton.setFocusableInTouchMode(true);

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.background_end));
                gender = "MALE";

            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "FEMALE";
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPreference(v, gender);

                FragmentC fragmentC = new FragmentC();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, fragmentC);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;

    }

    private void setPreference(View v, String gender) {
        SharedPreferences sf = v.getContext().getSharedPreferences("taeyoung", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("USER_GENDER", gender);
        editor.apply();
    }
}
