package com.example.appdcedausp.utils;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import static com.example.appdcedausp.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.example.appdcedausp.utils.Constants.SIGN_IN_REQUEST;

/**
 * Created by yago_ on 29/01/2018.
 *
 * Classe para lidar com o login da google e deixar o código mais limpo
 */

public class GoogleUtils {

    public static final String TAG = GoogleUtils.class.getName();

    // Login Google
    private static GoogleSignInOptions gso;
    private static GoogleSignInClient mGoogleSignInClient;
    private static GoogleSignInAccount account;
    private static GoogleAccountCredential gCredential;

    private static Activity mContext;

    public static void setContext(Context context) {
        mContext = (Activity)context;
    }

    // Checagem do Google Services
    public static boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (available == ConnectionResult.SUCCESS) {
            // Tudo ok, pode requisitar o mapa
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // Se for um erro resolvivel, mostrar
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(mContext,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "Shazam! ->isServicesOK: erro no google services");
        }
        return false;
    }

    public static void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mContext.startActivityForResult(signInIntent,SIGN_IN_REQUEST);
    }

    public static void checkGoogleLoginStatus() {
        // Login básico da conta Google, com perfil básico, ID e email.
        // Pega informações da conta Google que o usuário estiver logado, se não tiver nenhuma,
        // faz a requisição de login na função OnStart()
        Resources res = mContext.getResources();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(res.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext,gso);
        account = GoogleSignIn.getLastSignedInAccount(mContext);
        if (account != null) {
            initCredential();
            gCredential.setSelectedAccount(account.getAccount());
            Log.d(TAG, "Shazam! ->checkGoogleLoginStatus: account: " + account.getDisplayName());
        }
        Log.d(TAG, "Shazam! ->checkGoogleLoginStatus: gCredential: " + gCredential);
    }

    public static void initCredential() {
        Log.d(TAG, "Shazam! ->initCredential: entrou");
        // inicializacao GoogleAccountCredential
        gCredential = GoogleAccountCredential.usingOAuth2(
                mContext, Arrays.asList(CalendarScopes.CALENDAR_READONLY))
                .setBackOff(new ExponentialBackOff());
    }

    public static void setgCredentialAcc(Account acc) {
        gCredential.setSelectedAccount(acc);
    }

    public static GoogleAccountCredential getgCredential() {
        return gCredential;
    }

    public static void googleSignOut() {
        Log.d(TAG, "Shazam! ->googleSignOut: entrou");
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(mContext, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Saiu!", Toast.LENGTH_SHORT).show();
                        googleRevokeAccess();
                    }
                });
    }

    public static void googleRevokeAccess() {
        Log.d(TAG, "Shazam! ->googleRevokeAccess: entrou");
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(mContext, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gCredential = null;
                        ImageView signInButton = mContext.findViewById(R.id.googleSignInButton);
                        signInButton.setImageResource(R.drawable.googlesign_creme);
                        TextView signInText = mContext.findViewById(R.id.googleSignInText);
                        signInText.setText("Entrar:");
                    }
                });
    }
}