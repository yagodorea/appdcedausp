package com.example.appdcedausp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static com.example.appdcedausp.utils.Constants.*;

/**
 * Created by yago_ on 04/02/2018.
 *
 * TODO: Por enquanto, os posts inseridos no servidor do aplicativo precisarão de um aval de administrador, para evitar bagunça, pornografia, etc
 * TODO: Fazer verificação sobre preexistencia de um id igual ao gerado
 */

public class MakePostActivity extends AppCompatActivity {

    ImageView addImageButton;
    TextView title;
    TextView description;
    LinearLayout sendPost;
    int postId = 10;
    long forumId;
    int nPosts;

    Uri imgUri;

    ProgressDialog progressDialog;

    SharedPreferences pref;

    boolean gotImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.appdcedausp.R.layout.makepost_activity);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        forumId = getIntent().getLongExtra("forumId",-1);
        nPosts = getIntent().getIntExtra("nPosts",-1);

        gotImage = false;

        progressDialog = new ProgressDialog(this);
        addImageButton = findViewById(R.id.postImageContainer);

        title = findViewById(R.id.postTitle);
        description = findViewById(R.id.postDescription);

        sendPost = findViewById(R.id.postSendButton);
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

        // Adicionar OnClickListener para invocar intent da galeria e pegar uma imagem
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
    }

    private void startPosting() {

        Random random = new Random();

        progressDialog.setMessage("Postando...");
        progressDialog.show();

        postId = random.nextInt(MAX_N_POSTS);
        final long now = System.currentTimeMillis();

        final String tit = title.getText().toString().trim();
        final String desc = description.getText().toString().trim();
        if (!TextUtils.isEmpty(tit) && !TextUtils.isEmpty(desc)) {
            if (gotImage) {
                // TODO tratar upload de imagem
                StorageReference filepath = FirebaseUtils.getMStorage()
                        .child("forum_images")
                        .child("image:" + pref.getInt("Campus",0) + "-" + now);

                filepath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        // Inserir dados no id do post correto, no campus correto
                        DatabaseReference newPost = FirebaseUtils.getMDatabase()
                                .child("Forum")
                                .child(String.valueOf(pref.getInt("Campus",0)))
                                .child(String.valueOf(forumId))
                                .child("posts")
                                .child(String.valueOf(now));

                        newPost.child("titulo").setValue(tit);
                        newPost.child("descricao").setValue(desc);
                        newPost.child("autor").setValue(FirebaseUtils.getUser().getDisplayName());
                        newPost.child("criadoem").setValue(now);
                        if (downloadUri != null) {
                            newPost.child("imagem").setValue(downloadUri.toString());
                        }

                        FirebaseUtils.getMDatabase()
                                .child("Forum")
                                .child(String.valueOf(pref.getInt("Campus",0)))
                                .child(String.valueOf(forumId))
                                .child("forum_posts").setValue(nPosts + 1);

                        progressDialog.dismiss();
                        finish();
                    }
                });
            } else {
                // Inserir dados no id do post correto, no campus correto
                DatabaseReference newPost = FirebaseUtils.getMDatabase()
                        .child("Forum")
                        .child(String.valueOf(pref.getInt("Campus",0)))
                        .child(String.valueOf(forumId))
                        .child("posts")
                        .child(String.valueOf(now));

                newPost.child("titulo").setValue(tit);
                newPost.child("autor").setValue(FirebaseUtils.getUser().getDisplayName());
                newPost.child("criadoem").setValue(now);
                newPost.child("descricao").setValue(desc);

                FirebaseUtils.getMDatabase()
                        .child("Forum")
                        .child(String.valueOf(pref.getInt("Campus",0)))
                        .child(String.valueOf(forumId))
                        .child("forum_posts").setValue(nPosts + 1);

                progressDialog.dismiss();
                finish();
            }
        } else {
            progressDialog.dismiss();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            // Get Bitmap and upload to Firebase Storage

            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
                // Tratar uri, upar no servidor
                addImageButton.setImageBitmap(bm);
                gotImage = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
