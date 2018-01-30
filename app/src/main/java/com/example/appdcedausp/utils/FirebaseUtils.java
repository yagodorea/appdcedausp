package com.example.appdcedausp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdcedausp.R;
import com.example.appdcedausp.ui.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

/**
 * Created by yago_ on 29/01/2018.
 *
 * Classe criada para armazenar operações do Firebase e deixar o código do aplicativo mais limpo
 */

public class FirebaseUtils {

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static AuthCredential mCredential;

    private static Activity mContext;

    public static final String TAG = FirebaseUtils.class.getName();

    public static void setContext(Context context) {
        mContext = (Activity)context;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static FirebaseAuth getAuthInstance() {
        return mAuth;
    }

    public static FirebaseUser getUser() {
        return mUser;
    }

    public static void firebaseAuthWithGoogle(GoogleSignInAccount acct,Context context) {
        Log.d(TAG, "Shazam! ->firebaseAuthWithGoogle: " + acct.getId());
        final GoogleSignInAccount account = acct;
        mContext = (Activity)context;
        GoogleUtils.initCredential();
        mCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        Log.d(TAG, "Shazam! ->firebaseAuthWithGoogle: mCredential: " + mCredential.toString());
        if (mAuth != null) {
            mAuth.signInWithCredential(mCredential)
                    .addOnCompleteListener(mContext, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "Shazam! ->onComplete: entrou");
                            ImageView signInButton = mContext.findViewById(R.id.googleSignInButton);
                            TextView signInText = mContext.findViewById(R.id.googleSignInText);
                            if (task.isSuccessful()) {
                                mUser = task.getResult().getUser();
                                GoogleUtils.setgCredentialAcc(account.getAccount());
                                signInText.setText("Sair:");
                                signInButton.setImageResource(R.drawable.googlesign_grena);
                                Toast.makeText(mContext, "Logado como " + mUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Login Firebase falhou!", Toast.LENGTH_SHORT).show();
                                signInButton.setImageResource(R.drawable.googlesign_creme);
                            }
                        }
                    });
        }
        else {
            Log.d(TAG, "Shazam! ->firebaseAuthWithGoogle: mAuth null");
        }
    }

    public static boolean checkSignInStatus() {
        return (mUser != null);
    }

    public static void login() {
        Log.d(TAG, "Shazam! ->login: entrou");
        GoogleUtils.signIn();
    }

    public static void signOut() {
        Log.d(TAG, "Shazam! ->signOut: entrou");
        GoogleUtils.googleSignOut();
        mAuth.signOut();
        mUser = mAuth.getCurrentUser();
    }
}
