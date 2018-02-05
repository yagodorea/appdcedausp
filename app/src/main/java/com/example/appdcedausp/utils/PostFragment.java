package com.example.appdcedausp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appdcedausp.R;

/**
 * Created by yago_ on 30/01/2018.
 */

public class PostFragment extends Fragment {

    TextView title;
    TextView content;
    TextView author;
    ImageView image;
    View separator;

    Context c;
    View v;

    LinearLayout mainContainer;
    RelativeLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_fragment,container,false);
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
        mainContainer = view.findViewById(R.id.post_list);
    }

    public void addPost(String tit, String cont, Bitmap img, String aut) {
        // Set Title
        title = new TextView(c);
        title.setText(tit);
        title.setTextSize(24f);
        title.setTypeface(null,Typeface.BOLD);

        // Set Content
        content = new TextView(c);
        content.setText(cont);

        // Set Image
        if (img != null) {
            image = new ImageView(c);
            image.setImageBitmap(img);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    300); // 300px de altura maxima
            image.setLayoutParams(params);
            mainContainer.addView(image);
        }

        // Set author
        author = new TextView(c);
        author.setText(aut);
        author.setTextSize(12f);
        author.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        author.setTypeface(null, Typeface.ITALIC);

        mainContainer.addView(title);
        mainContainer.addView(content);
        mainContainer.addView(author);

        separator = new View(c);
        separator.setBackgroundColor(getResources().getColor(R.color.grena));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2);
        separator.setLayoutParams(params);
        mainContainer.addView(separator);
        mainContainer.setPadding(0,0,0,20);
    }
}
