package com.dceusp.appdcedausp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dceusp.appdcedausp.R;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FbFeedFragment extends Fragment {

    TextView fbStory;
    TextView fbDate;
    TextView fbPermalink;
    TextView fbMessage;
    ImageView postSeparator;
    ImageView postPic;
    String imgurl;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        v = view;

        innerContainer = view.findViewById(R.id.innerContainer);
    }

    public void setPost(String date, String story, String picture, String message, String perma) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (date != null) {
            fbDate = new TextView(c);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSS",new Locale("pt","BR"));
            try {
                fbDate.setText(DateFormat.getDateInstance(DateFormat.FULL,new Locale("pt","BR")).format(df.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fbDate.setTextSize(14f);
            fbDate.setTypeface(null, Typeface.ITALIC);
            fbDate.setPadding(10, 10, 0, 0);
            fbDate.setTextColor(getResources().getColor(R.color.creme));
            fbDate.setLayoutParams(params);
            innerContainer.addView(fbDate);
        }

        if (story != null) {
            fbStory = new TextView(c);
            fbStory.setText(story);
            fbStory.setTextSize(20f);
            fbStory.setTypeface(null, Typeface.BOLD);
            fbStory.setPadding(10, 0, 0, 10);
            fbStory.setTextColor(getResources().getColor(R.color.creme));
            fbStory.setLayoutParams(params);
            innerContainer.addView(fbStory);
        }

        if (picture != null) {
            imgurl = picture;
            postPic = new ImageView(c);
            postPic.setPadding(10, 10, 10, 10);
            postPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postPic.setLayoutParams(params);
            innerContainer.addView(postPic);

            new DownloadImageTask(postPic).execute(imgurl);
        }

        if (message != null) {
            // Encurtar mensagem
//            if (message.length() > 500) {
//                message = StringBuilder();
//            }

            fbMessage = new TextView(c);
            fbMessage.setText(message);
            fbMessage.setTextSize(16f);
            fbMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            fbMessage.setPadding(10, 0, 0, 10);
            fbMessage.setTextColor(getResources().getColor(R.color.creme));
            fbMessage.setLayoutParams(params);
            innerContainer.addView(fbMessage);
        }

        if (perma != null) {
            fbPermalink = new TextView(c);
            fbPermalink.setText(perma);
            fbPermalink.setTextSize(14f);
            fbPermalink.setTypeface(null, Typeface.ITALIC);
            fbPermalink.setLinkTextColor(getResources().getColor(R.color.colorAccent));
            fbPermalink.setPadding(10, 0, 0, 10);
            fbPermalink.setTextColor(getResources().getColor(R.color.creme));
            fbPermalink.setLinksClickable(true);
            fbPermalink.setMovementMethod(LinkMovementMethod.getInstance());
            fbPermalink.setLayoutParams(params);
            innerContainer.addView(fbPermalink);

            final String link = perma;
            fbPermalink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c.getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }
            });
        }

        postSeparator = new ImageView(c);
        postSeparator.setImageResource(R.drawable.ic_separator);
        postSeparator.setPadding(10,0,10,50);
        postSeparator.setScaleType(ImageView.ScaleType.FIT_XY);
        postSeparator.setLayoutParams(params);
        innerContainer.addView(postSeparator);
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
        private ImageView image;
        private Bitmap bitmap;

        DownloadImageTask(ImageView imageView) {
            this.image = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try {
                InputStream in = new URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                bitmap = null;
                e.printStackTrace();
            }
            return bitmap;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                image.setImageBitmap(bitmap);
            }
        }
    }
}
