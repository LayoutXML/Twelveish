package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.layoutxml.twelveish.R;

public class FontSizeInfoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_size_info_activity);

        TextView main = findViewById(R.id.infoText);
        Button okayButton = findViewById(R.id.okay);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int mainSize = sharedPreferences.getInt(getString(R.string.main_text_size_offset),0);
        int secondarySize = sharedPreferences.getInt(getString(R.string.secondary_text_size_offset),0);
        int actualMainTextSize = sharedPreferences.getInt(getString(R.string.main_text_size_real),24);

        main.setText("This option allows increasing or decreasing text sizes.\n\n" +
                "By increasing them, items may overlap or go off screen.\n" +
                "By decreasing them, text will be smaller.\n\n" +
                "Main text size by default is automatically calculated to fit the screen and secondary text size is static.\n\n" +
                "For reference, these are current text sizes:\n" +
                "Main text: "+actualMainTextSize+" + "+mainSize+" (offset),\n" +
                "Secondary: 24 + "+secondarySize+" (offset).");

        okayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(FontSizeInfoActivity.this, FontSizeActivity.class);
                FontSizeInfoActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}
