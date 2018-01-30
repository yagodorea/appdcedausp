package com.example.appdcedausp.utils;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appdcedausp.R;

/**
 * Created by yago_ on 13/01/2018.
 */

public class DescriptionFragment extends Fragment {

    TextView tTitle;
    TextView tDesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.description_fragment,container,false);
        tTitle = v.findViewById(R.id.descTitle);
        tDesc = v.findViewById(R.id.descContent);
        return v;
    }

    public void setText(String title, String desc) {
        tTitle.setText(title);
        tDesc.setText(desc);
    }
}
