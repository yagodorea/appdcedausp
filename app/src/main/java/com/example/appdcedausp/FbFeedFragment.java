package com.example.appdcedausp;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by yago_ on 17/01/2018.
 */

public class FbFeedFragment extends Fragment {

    private static final String TAG = FbFeedFragment.class.getName();

    TextView fbPost;
    TextView fbStory;
    TextView fbDate;
    TextView fbPermalink;
    ImageView postSeparator;
    ImageView postPic;
    Drawable pic;

    LinearLayout innerContainer;

    View v;

    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fbfeed_fragment,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        fbPost = view.findViewById(R.id.fbPostMessage);
//        fbStory = view.findViewById(R.id.fbStory);
//        fbDate = view.findViewById(R.id.fbDate);
//        fbPermalink = view.findViewById(R.id.fbPermalink);
//        postSeparator = view.findViewById(R.id.fbPostSeparator);
//        postPic = view.findViewById(R.id.postPic);

        v = view;

        innerContainer = view.findViewById(R.id.innerContainer);
    }

    public void setPost(String story, String date, String perma, final String imgurl) {

        fbDate = new TextView(c);
        fbDate.setText(date);
        fbDate.setTextSize(14f);
        fbDate.setTypeface(null, Typeface.ITALIC);
        fbDate.setPadding(10,10,0,0);
        fbDate.setTextColor(getResources().getColor(R.color.creme));

        fbStory = new TextView(c);
        fbStory.setText(story);
        fbStory.setTextSize(20f);
        fbStory.setTypeface(null, Typeface.BOLD);
        fbStory.setPadding(10,0,0,10);
        fbStory.setTextColor(getResources().getColor(R.color.creme));

        postPic = new ImageView(c);
        postPic.setPadding(10,10,10,10);
        postPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
        postPic.setScaleX(2f);
        postPic.setScaleY(2f);

        fbPermalink = new TextView(c);
        fbPermalink.setText(perma);
        fbPermalink.setTextSize(14f);
        fbPermalink.setTypeface(null, Typeface.ITALIC);
        fbPermalink.setLinksClickable(true);
        fbPermalink.setLinkTextColor(getResources().getColor(R.color.colorAccent));
        fbPermalink.setPadding(10,0,0,10);
        fbPermalink.setTextColor(getResources().getColor(R.color.creme));

        postSeparator = new ImageView(c);
        postSeparator.setImageResource(R.drawable.ic_separator);
        postSeparator.setPadding(10,0,10,0);
        postSeparator.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        postSeparator.setLayoutParams(params);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Run network operation in background thread not to freeze main thread
                pic = LoadImageFromWebOperations(imgurl);

                // Then hand the result to main thread who can alter the view it created
                Handler handler = new Handler(c.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (pic != null && postPic != null)
                            postPic.setImageDrawable(pic);
                    }
                };
                handler.post(runnable);
            }
        }).start();

        innerContainer.addView(fbDate);
        innerContainer.addView(fbStory);
        innerContainer.addView(postPic);
        innerContainer.addView(fbPermalink);
        innerContainer.addView(postSeparator);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            Log.d(TAG, "Shazam! ->LoadImageFromWebOperations: error loading image from web: " + e.toString());
            return null;
        }
    }
}
