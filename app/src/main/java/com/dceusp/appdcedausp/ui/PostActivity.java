package com.dceusp.appdcedausp.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dceusp.appdcedausp.R;
import com.dceusp.appdcedausp.utils.FirebaseUtils;
import com.dceusp.appdcedausp.utils.Post;
import com.dceusp.appdcedausp.utils.Resposta;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = PostActivity.class.getName();

    RecyclerView mRespostaList;

    ImageView imagePost;
    String imUrl;
    TextView postTitle;
    TextView postDescription;
    TextView postDateAndAuthor;
    FloatingActionButton fabClose;
    FloatingActionButton fabDelete;
    FloatingActionButton fabAnswer;
    long dateLong;
    SharedPreferences pref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        pref = getSharedPreferences("myConfig",0);

        Log.d(TAG, "Shazam! ->onCreate: entrou");

        imagePost = findViewById(R.id.mainPostImage);
        postTitle = findViewById(R.id.mainPostTitle);
        postDescription = findViewById(R.id.mainPostDescription);
        postDateAndAuthor = findViewById(R.id.postDateAndAuthor);
        fabClose = findViewById(R.id.fabClosePost);
        fabDelete = findViewById(R.id.fabDeletePost);
        fabAnswer = findViewById(R.id.fabAnswerPost);

        mRespostaList = findViewById(R.id.answer_list);
        mRespostaList.setHasFixedSize(true);
        mRespostaList.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent().hasExtra("post") && getIntent().getExtras() != null) {
            Log.d(TAG, "Shazam! ->onCreate: hasExtra = true");
            Post post = (Post) getIntent().getExtras().getSerializable("post");

            // Set post
            if (post != null) {
                Log.d(TAG, "Shazam! ->onCreate: title = " + post.getTitulo());

                if (post.getImagem() != null) {
                    imUrl = post.getImagem();
                    Picasso.with(this)
                            .load(imUrl)
                            .fit()
                            .centerCrop()
                            .into(imagePost);
                } else {
                    imagePost.setVisibility(View.GONE);
                }
                postTitle.setText(post.getTitulo());
                postDescription.setText(post.getDescricao());
                dateLong = post.getCriadoem();
                Date date = new Date(dateLong);
                String txt = post.getAutor() + ", " + new SimpleDateFormat("EEE, d MMM, h:mm a", new Locale("pt", "BR")).format(date);
                postDateAndAuthor.setText(txt);
            }
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
                AlertDialog eraseDialog = new AlertDialog.Builder(PostActivity.this).create();
                eraseDialog.setTitle("Apagar");
                eraseDialog.setMessage("Deseja mesmo apagar esse post?");
                eraseDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                verifyAdm();
                            }
                        });
                eraseDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                eraseDialog.show();
            }
        });

        fabAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRespostaDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Resposta,PostActivity.RespostaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Resposta, RespostaViewHolder>(
                Resposta.class,
                R.layout.answer_fragment,
                PostActivity.RespostaViewHolder.class,
                FirebaseUtils.getMDatabase()
                        .child("Forum")
                        .child(String.valueOf(pref.getInt("Campus",0)))
                        .child(String.valueOf(getIntent().getLongExtra("forumId",-1)))
                        .child("posts")
                        .child(String.valueOf(dateLong))
                        .child("respostas")
        ) {
            @Override
            protected void populateViewHolder(RespostaViewHolder viewHolder, Resposta model, int position) {

                viewHolder.setAutor(model.getAutor());
                viewHolder.setQuando(model.getQuando());
                viewHolder.setTexto(model.getTexto());

                // TODO Implementar botão "apagar resposta"
            }
        };

        mRespostaList.setAdapter(firebaseRecyclerAdapter);
    }

    public void verifyAdm() {
        DatabaseReference ref = FirebaseUtils.getMDatabase();
        ref = ref.child("Forum").child("adms");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                String userEmail = FirebaseUtils.getUser().getEmail();
                Log.d(TAG, "Shazam! ->onDataChange: userEmail = " + userEmail);
                while (dataSnapshot.child(String.valueOf(i)).exists()) {
                    String adm = dataSnapshot.child(String.valueOf(i)).getValue(String.class);
                    Log.d(TAG, "Shazam! ->onDataChange: adms: " + adm);
                    if (adm.equals(userEmail)) {
                        // Delete
                        Log.d(TAG, "Shazam! ->onDataChange: will delete!");
//                        Map<String,Object> map = new HashMap<>();
//                        map.put(String.valueOf(dateLong),null);
                        Log.d(TAG, "Shazam! ->onDataChange: deleting: Forum"
                                + "->" + String.valueOf(pref.getInt("Campus",0))
                                + "->" + String.valueOf(getIntent().getLongExtra("forumId",-1))
                                + "->posts->"
                                + dateLong);
                        FirebaseUtils.getMDatabase()
                                .child("Forum")
                                .child(String.valueOf(pref.getInt("Campus",0)))
                                .child(String.valueOf(getIntent().getLongExtra("forumId",-1)))
                                .child("posts")
                                .child(String.valueOf(dateLong))
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Log.d(TAG, "Shazam! ->onComplete: remove result: " + databaseError);
                                        PostActivity.this.finish();
                                    }
                                });
                        // Também decrementar o numero de posts do forum
                        FirebaseUtils.getMDatabase()
                                .child("Forum")
                                .child(String.valueOf(pref.getInt("Campus",0)))
                                .child(String.valueOf(getIntent().getLongExtra("forumId",-1)))
                                .child("forum_posts")
                                .setValue(Integer.valueOf(getIntent().getIntExtra("nPosts",0) - 1));
                        break;
                    } else {
                        i++;
                    }
                }
                if (!dataSnapshot.child(String.valueOf(i)).exists()) {
                    verifyOwner();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void verifyOwner() {
        final DatabaseReference ref = FirebaseUtils.getMDatabase()
                .child("Forum")
                .child(String.valueOf(pref.getInt("Campus",0)))
                .child(String.valueOf(getIntent().getLongExtra("forumId",-1)))
                .child("posts")
                .child(String.valueOf(dateLong));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String owner = FirebaseUtils.getUser().getDisplayName();
                if (owner.equals(dataSnapshot.child("autor").getValue())) {
                    Log.d(TAG, "Shazam! ->onDataChange: user is owner!");
                    ref.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            PostActivity.this.finish();
                        }
                    });
                } else {
                    Toast.makeText(PostActivity.this, "Você não pode apagar esse post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showImageDialog() {
        ImageDialog dialog = new ImageDialog();
        dialog.setImage(imUrl);
        dialog.show(getSupportFragmentManager(),"ImageDialog");
    }

    private void showRespostaDialog() {
        RespostaDialog dialog = new RespostaDialog();
        dialog.setInfos(pref.getInt("Campus",0),getIntent().getLongExtra("forumId",-1),dateLong);
        dialog.show(getSupportFragmentManager(),"RespostaDialog");
    }

    public static class ImageDialog extends DialogFragment {

        ImageView im;
        String image;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @SuppressWarnings("ConstantConditions") AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_image,null);
            builder.setView(v);
            im = v.findViewById(R.id.dialogImage);

            if (im != null) {
                if (image != null) {
                    Picasso.with(getActivity())
                            .load(image)
                            .into(im);
                } else {
                    im.setVisibility(View.GONE);
                }
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

    public static class RespostaDialog extends DialogFragment {

        View mView;
        TextView ok;
        TextView cancel;
        EditText caixa;

        int campus;
        long forum;
        long postDate;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @SuppressWarnings("ConstantConditions") AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            mView = inflater.inflate(R.layout.dialog_resposta,null);
            builder.setView(mView);

            ok = mView.findViewById(R.id.answerSend);
            cancel = mView.findViewById(R.id.answerCancel);
            caixa = mView.findViewById(R.id.answerBox);

            if (ok != null && cancel != null) {
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RespostaDialog.this.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Fecha se não tiver  nada escrito
                        if (caixa.getText().toString().length() < 1) RespostaDialog.this.dismiss();

                        long now = System.currentTimeMillis();
                        DatabaseReference ref = FirebaseUtils.getMDatabase()
                                .child("Forum")
                                .child(String.valueOf(campus))
                                .child(String.valueOf(forum))
                                .child("posts")
                                .child(String.valueOf(postDate))
                                .child("respostas")
                                .child(String.valueOf(now));

                        ref.child("autor").setValue(FirebaseUtils.getUser().getDisplayName());
                        ref.child("quando").setValue(now);
                        ref.child("texto").setValue(caixa.getText().toString());
                        RespostaDialog.this.dismiss();
                    }
                });
            } else {
                Log.d(TAG, "Shazam! ->onCreateDialog: shit is null!");
            }

            return builder.create();
        }
        public void setInfos(int campusN, long forumN, long postN) {
            campus = campusN;
            forum = forumN;
            postDate = postN;
        }

    }

    public static class RespostaViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RespostaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setAutor(String autor) {
            TextView mAutor = mView.findViewById(R.id.answer_author);
            mAutor.setText(autor);
        }

        void setTexto(String texto) {
            TextView mTexto = mView.findViewById(R.id.answer_text);
            mTexto.setText(texto);
        }

        void setQuando(long quando) {
            TextView mQuando = mView.findViewById(R.id.answer_date);
            String quandoText = new SimpleDateFormat("EEE, d MMM, h:mm a", new Locale("pt", "BR")).format(new Date(quando));
            mQuando.setText(quandoText);
        }
    }
}
