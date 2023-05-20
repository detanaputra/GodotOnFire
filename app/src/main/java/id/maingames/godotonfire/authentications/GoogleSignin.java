package id.maingames.godotonfire.authentications;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.R;

public class GoogleSignin {
    private static final String TAG = "GoogleActivity";
    public static final int RC_SIGN_IN = 9001;
    public static final int RC_LINK = 9002;
    private static GoogleSignin instance;

    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    public GoogleSignin() {
    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new GoogleSignin();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mAuth = FirebaseAuth.getInstance();

        instance.oneTapClient = Identity.getSignInClient(_godotActivity);
        instance.signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(_godotActivity.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
    }


    public static GoogleSignin getInstance(){
        if(instance == null){
            Log.w(TAG, "Google Sign in instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("GoogleSigninActivity is null"));
        }
        return instance;
    }

    public void signin(){
        Log.d(TAG, "Launching google One Tap Signin");
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(godotActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            Log.d(TAG, "One Tap signin success, sending intent..");
                            godotActivity.startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't sending intent: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(godotActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.e(TAG, "firebaseAuthWithGoogle:failure.  " + e.getLocalizedMessage());
                        godotOnFire.emitGodotSignal("_on_google_signin_completed", new GodotFirebaseUser(null).ToDictionary());
                    }
                });
    }

    public void linkAccount(){
        Log.d(TAG, "Launching google One Tap Signin");
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(godotActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            Log.d(TAG, "One Tap signin success, sending intent..");
                            godotActivity.startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), RC_LINK, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't sending intent: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(godotActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.e(TAG, "firebaseAuthWithGoogle:failure.  " + e.getLocalizedMessage());
                    }
                });
    }

    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN){
            try{
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null){
                    Log.d(TAG, "Got ID token.");
                    firebaseAuthWithGoogle(idToken);
                }
            }
            catch (ApiException e){
                Log.w(TAG, "firebaseAuthWithGoogle:failure. " + e.getLocalizedMessage(), e);
            }
        }
        else if (requestCode == RC_LINK){
            try{
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null){
                    Log.d(TAG, "Got ID token.");
                    linkWithGoogleCredential(idToken);
                }
            }
            catch (ApiException e){
                Log.w(TAG, "link account with Google failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        //FirebaseUser user = null;
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser currentUser = task.getResult().getUser();
                            user = new GodotFirebaseUser(currentUser);
                        } else {
                            // sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        godotOnFire.emitGodotSignal("_on_google_signin_completed", user.ToDictionary());
                    }
                });
    }

    // TODO: Bug, setiap user ex anonymouse yang sudah di link, ketika login, cuma bisa diambil uid nya saja
    // workaround jadi ketika sudah login, next time dia start aplikasi baru bisa di tarik data email dll.
    private void linkWithGoogleCredential(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithGoogleCredential:success");
                            //user = new GodotFirebaseUser(mAuth.getCurrentUser());
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "linkWithGoogleCredential:failure", task.getException());
                        }
                        godotOnFire.emitGodotSignal("_on_link_account_completed", user.ToDictionary());
                    }
        });
    }
}
