package com.example.appdcedausp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yago_ on 05/02/2018.
 */

public class PostActivity extends AppCompatActivity {

    private static final String TAG = PostActivity.class.getName();

    ImageView imagePost;
    TextView postTitle;
    TextView postDescription;
    TextView postDateAndAuthor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        Log.d(TAG, "Shazam! ->onCreate: entrou");

        imagePost = findViewById(R.id.mainPostImage);
        postTitle = findViewById(R.id.mainPostTitle);
        postDescription = findViewById(R.id.mainPostDescription);
        postDateAndAuthor = findViewById(R.id.postDateAndAuthor);


        if (getIntent().hasExtra("post")) {
            Log.d(TAG, "Shazam! ->onCreate: hasExtra = true");
            Post post = (Post) getIntent().getExtras().getSerializable("post");

            Log.d(TAG, "Shazam! ->onCreate: title = " + post.getTitulo());

            // Set post
            if (post.getImagem() != null) {
                Picasso.with(this)
                        .load(post.getImagem())
                        .fit()
                        .centerCrop()
                        .into(imagePost);
            }
            postTitle.setText(post.getTitulo());
            postDescription.setText(post.getDescricao());
            Date date = new Date(post.getCriadoem());
            postDateAndAuthor.setText(post.getAutor() + ", " + new SimpleDateFormat("EEE, d MMM, h:mm a", new Locale("pt", "BR")).format(date));
        }
    }
}
