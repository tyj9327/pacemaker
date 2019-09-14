package com.example.pacemaker;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentC extends Fragment {

    public FragmentC() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_c, container, false);

        final EditText editText = (EditText) view.findViewById(R.id.alcohol_capacity);
        ImageView submitButton = (ImageView) view.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreference(v, editText.getText().toString());
                Intent intent = new Intent(getContext(), StartActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private void setPreference(View v, String capacity) {
        SharedPreferences sf = v.getContext().getSharedPreferences("taeyoung", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("USER_ALCOHOL", capacity);
        editor.apply();
    }
}
