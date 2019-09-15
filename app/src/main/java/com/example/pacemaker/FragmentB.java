package com.example.pacemaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
        final View maleChecked = (View) view.findViewById(R.id.male_checked);
        final View femaleChecked = (View) view.findViewById(R.id.female_checked);



        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "MALE";
                maleChecked.setVisibility(View.VISIBLE);
                femaleChecked.setVisibility(View.INVISIBLE);

            }
        });


        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "FEMALE";
                femaleChecked.setVisibility(View.VISIBLE);
                maleChecked.setVisibility(View.INVISIBLE);
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gender.equals("NONE")){
                    Toast.makeText(getContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    setPreference(v, gender);

                    FragmentC fragmentC = new FragmentC();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_layout, fragmentC, "FRAGMENT_C");
                    fragmentTransaction.addToBackStack("FRAGMENT_C");
                    fragmentTransaction.commit();
                }

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
