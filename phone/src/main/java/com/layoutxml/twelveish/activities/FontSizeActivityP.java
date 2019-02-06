package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.layoutxml.twelveish.R;

public class FontSizeActivityP extends Activity {

    private int main;
    private int secondary;
    private SharedPreferences sharedPreferences;
    private EditText mainText;
    private EditText secondaryText;
    private Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen);
//
//        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//        main = sharedPreferences.getInt(getString(R.string.main_text_size_offset),0);
//        secondary = sharedPreferences.getInt(getString(R.string.secondary_text_size_offset),0);
//
//        mainText = findViewById(R.id.batteryMin);
//        secondaryText = findViewById(R.id.batteryMax);
//        submit = findViewById(R.id.battery_submit);
//
//        mainText.setText(main +"");
//        secondaryText.setText(secondary +"");
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int newMin = Integer.parseInt(mainText.getText().toString());
//                int newMax = Integer.parseInt(secondaryText.getText().toString());
//                sharedPreferences.edit().putInt(getString(R.string.main_text_size_offset),newMin).apply();
//                sharedPreferences.edit().putInt(getString(R.string.secondary_text_size_offset),newMax).apply();
//                Toast.makeText(getApplicationContext(),newMin+" and "+newMax+" set",Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });

    }
}
