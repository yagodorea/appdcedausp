package com.example.appdcedausp.utils;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appdcedausp.R;

/**
 * Created by yago_ on 29/01/2018.
 */

public class EventFragment extends Fragment {

    Context c;

    View v;

    RelativeLayout container;
    LinearLayout inner;
    LinearLayout mainContainer;
    ImageView icon;

    TextView tTitle;
    TextView tDateStart;
    TextView tDateEnd;
    TextView tLocal;
    TextView tDesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
//        container = view.findViewById(R.id.eventContainer);
        mainContainer = view.findViewById(R.id.eventInnerContainer);
    }

    public void setEvent(String title, String start, String end, String local, String desc) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0,0,0,20);

        inner = new LinearLayout(c);
        inner.setLayoutParams(params);
        inner.setOrientation(LinearLayout.VERTICAL);

        tTitle = new TextView(c);
        tTitle.setBackgroundColor(getResources().getColor(R.color.preto));
        tTitle.setTextColor(getResources().getColor(R.color.creme));
        tTitle.setTextSize(24f);
        tTitle.setTypeface(null,Typeface.BOLD_ITALIC);
        tTitle.setText(title);
        tTitle.setLayoutParams(params);
        inner.addView(tTitle);

        tDateStart = new TextView(c);
        tDateStart.setTextSize(12f);
        tDateStart.setTypeface(null,Typeface.ITALIC);
        tDateStart.setText("Início: " + start);
        tDateStart.setLayoutParams(params);
        inner.addView(tDateStart);

        tDateEnd = new TextView(c);
        tDateEnd.setTextSize(12f);
        tDateEnd.setTypeface(null,Typeface.ITALIC);
        tDateEnd.setText("Fim: " + end);
        tDateEnd.setLayoutParams(params);
        inner.addView(tDateEnd);

        tLocal = new TextView(c);
        tLocal.setTextSize(16f);
        tLocal.setTypeface(null,Typeface.BOLD);
        tLocal.setText("Local: " + local);
        tLocal.setLayoutParams(params);
        inner.addView(tLocal);

        tDesc = new TextView(c);
        tDesc.setTextSize(14f);
        tDesc.setText(desc);
        tDesc.setLayoutParams(params);
        tDesc.setPadding(0,0,0,20);
        inner.addView(tDesc);


        container = new RelativeLayout(c);
        icon = new ImageView(c);
        icon.setImageResource(R.drawable.ic_add);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(params);
        params2.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE);
        params2.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        icon.setLayoutParams(params2);
        icon.getLayoutParams().height = 200;
        icon.getLayoutParams().width = 200;
        container.setPadding(10,10,10,10);
        container.addView(inner);
        container.addView(icon);

        mainContainer.addView(container);
    }
}
