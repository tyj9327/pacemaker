package com.example.pacemaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentA extends Fragment {

    public FragmentA(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_a, container, false);

        ImageView nextButton = (ImageView) view.findViewById(R.id.next_icon_a);
        final EditText nameText = (EditText) view.findViewById(R.id.edit_text_name);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameText.getText().toString();

                if(name.equals("")) {
                    Toast.makeText(getContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    SharedPreferences sf = v.getContext().getSharedPreferences("taeyoung", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString("USER_NAME", name);
                    editor.apply();

                    FragmentB fragmentB = new FragmentB();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_layout, fragmentB, "FRAGMENT_B");
                    fragmentTransaction.addToBackStack("FRAGMENT_B");
                    fragmentTransaction.commit();
                }
            }
        });

        return view;

    }


}
