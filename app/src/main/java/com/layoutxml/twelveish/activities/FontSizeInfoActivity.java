package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Intent;
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

        main.setText("Be careful!\n\n" +
                "This option allows increasing or decreasing text sizes.\n\n" +
                "By increasing them, items may overlap or go off screen.\n" +
                "By decreasing them, text will be smaller.\n\n" +
                "By default, main text size is automatically calculated to fit the screen and secondary text size is static.\n");

        okayButton.setOnClickListener(new View.OnClickListener() {
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
