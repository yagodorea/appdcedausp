package com.example.appdcedausp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

/**
 * Created by yago_ on 14/01/2018.
 */

public class CampusSelectPreference extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RadioGroup radio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_config);

        pref = getApplicationContext().getSharedPreferences("myConfig", 0); // 0 - for private mode
        editor = pref.edit();

        radio = (RadioGroup)findViewById(R.id.campusList);

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                editor.putInt("Campus",i);
                editor.apply();
                //Toast.makeText(CampusSelectPreference.this, ""+i, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
