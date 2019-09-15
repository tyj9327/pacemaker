package com.example.pacemaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

public class ColorPickerPopupActivity extends AppCompatActivity {

    ColorPickerView colorPickerView;
    Button applyBtn;
    Button cancelBtn;
    Intent colorIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        findViews();
        setContentView(R.layout.activity_color_picker_popup);

        colorPickerView.setActionMode(ActionMode.LAST);

        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {

                colorIntent = new Intent();
                colorIntent.putExtra("pickedColor", color);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorIntent == null) {
//                    colorIntent = new Intent();
//                    colorIntent.putExtra("colorPopupResult", -1);
                    setResult(RESULT_CANCELED);
                } else{
                    setResult(RESULT_OK, colorIntent);
                }
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void findViews() {
        colorPickerView = findViewById(R.id.color_picker_popup_view);
        applyBtn = findViewById(R.id.color_picker_apply);
        cancelBtn = findViewById(R.id.color_picker_cancel);
    }


}
