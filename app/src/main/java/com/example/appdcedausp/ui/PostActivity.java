package com.example.appdcedausp.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
    String imUrl;
    TextView postTitle;
    TextView postDescription;
    TextView postDateAndAuthor;
    FloatingActionButton fabClose;
    FloatingActionButton fabDelete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        Log.d(TAG, "Shazam! ->onCreate: entrou");

        imagePost = findViewById(R.id.mainPostImage);
        postTitle = findViewById(R.id.mainPostTitle);
        postDescription = findViewById(R.id.mainPostDescription);
        postDateAndAuthor = findViewById(R.id.postDateAndAuthor);
        fabClose = findViewById(R.id.fabClosePost);
        fabDelete = findViewById(R.id.fabDeletePost);


        if (getIntent().hasExtra("post")) {
            Log.d(TAG, "Shazam! ->onCreate: hasExtra = true");
            Post post = (Post) getIntent().getExtras().getSerializable("post");

            Log.d(TAG, "Shazam! ->onCreate: title = " + post.getTitulo());

            // Set post
            if (post.getImagem() != null) {
                imUrl = post.getImagem();
                Picasso.with(this)
                        .load(imUrl)
                        .fit()
                        .centerCrop()
                        .into(imagePost);
            }
            postTitle.setText(post.getTitulo());
            postDescription.setText(post.getDescricao());
            Date date = new Date(post.getCriadoem());
            postDateAndAuthor.setText(post.getAutor() + ", " + new SimpleDateFormat("EEE, d MMM, h:mm a", new Locale("pt", "BR")).format(date));
        }

        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Center the image in a dialog
                showImageDialog();
            }
        });

        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: verificar se é dono ou adm, e fazer função para deletar o post
            }
        });
    }

    private void showImageDialog() {
        ImageDialog dialog = new ImageDialog();
        dialog.setImage(imUrl);
        dialog.show(getSupportFragmentManager(),"ImageDialog");
    }

    public static class ImageDialog extends DialogFragment {

        ImageView im;
        String image;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View v = inflater.inflate(R.layout.dialog_image,null);
            builder.setView(v);
            im = v.findViewById(R.id.dialogImage);

            if (im != null && image != null) {
                Picasso.with(getActivity())
                        .load(image)
                        .into(im);
            }

            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageDialog.this.dismiss();
                }
            });

            return builder.create();
        }

        public void setImage(String url) {
            image = url;
        }
    }
}
