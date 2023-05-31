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

import org.godotengine.godot.Dictionary;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.R;

public class GoogleSignin {
    private static String TAG = "";
    public static final int RC_SIGN_IN = 9001;
    public static final int RC_LINK = 9002;
    private static GoogleSignin instance;

    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private BeginSignInRequest signUpRequest;

    public GoogleSignin() {
    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new GoogleSignin();
        TAG = _godotOnFire.getPluginName();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mAuth = FirebaseAuth.getInstance();

        instance.oneTapClient = Identity.getSignInClient(_godotActivity);
        instance.signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(_godotActivity.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
        instance.signUpRequest = BeginSignInRequest.builder()
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

    public void signup(){
        Log.d(TAG, "Launching google One Tap Signup");
        Dictionary signalParams = new Dictionary();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(godotActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            Log.d(TAG, "One Tap signin success, sending sign up intent..");
                            godotActivity.startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't sending intent: " + e.getLocalizedMessage(), e);
                            signalParams.put("status", 1);
                            signalParams.put("message", "Begin sign in request has failed and couldn't sending intent");
                            signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                            godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
                        }
                    }
                })
                .addOnFailureListener(godotActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sign up request has failed: " + e.getLocalizedMessage(), e);
                        signalParams.put("status", 1);
                        signalParams.put("message", "Begin sign up request has failed and couldn't sending intent");
                        signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                        godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
                    }
                });
    }

    public void signin(){
        Log.d(TAG, "Launching google One Tap Signin");
        Dictionary signalParams = new Dictionary();
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(godotActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            Log.d(TAG, "One Tap signin success, sending sign in intent..");
                            godotActivity.startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't sending intent: " + e.getLocalizedMessage(), e);
                            signalParams.put("status", 1);
                            signalParams.put("message", "Begin sign in request has failed and couldn't sending intent");
                            signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                            godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
                        }
                    }
                })
                .addOnFailureListener(godotActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.w(TAG, "sign in request has failed. It might be because user hasn't sign up to your game. This behaviour is expected. " + e.getLocalizedMessage(), e);
                        Log.d(TAG, "Sending sign up request ...  ");
                        signup();
                    }
                });
    }

    public void linkAccount(){
        Log.d(TAG, "Launching google One Tap Signin");
        Dictionary signalParams = new Dictionary();
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(godotActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            Log.d(TAG, "One Tap signin success, sending intent..");
                            godotActivity.startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), RC_LINK, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't sending intent: " + e.getLocalizedMessage(), e);
                            signalParams.put("status", 1);
                            signalParams.put("message", "Begin sign in request has failed and couldn't sending intent");
                            signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                            godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
                        }
                    }
                })
                .addOnFailureListener(godotActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.e(TAG, "Link account has failed. " + e.getLocalizedMessage(), e);
                        signalParams.put("status", 1);
                        signalParams.put("message", "Begin sign in request has failed and couldn't sending intent");
                        signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                        godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
                    }
                });
    }

    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        Dictionary signalParams = new Dictionary();
        if(requestCode == RC_SIGN_IN){
            try{
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null){
                    Log.d(TAG, "Got ID token.");
                    firebaseAuthWithGoogle(idToken);
                }
                else{
                    Log.w(TAG, "Sign in request has failed because google id token is null");
                    signalParams.put("status", 1);
                    signalParams.put("message", "Sign in request has failed because google id token is null");
                    signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                    godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
                }
            }
            catch (ApiException e){
                // user cancel the one tap intent
                Log.w(TAG, "firebaseAuthWithGoogle:failure. " + e.getLocalizedMessage(), e);
                Log.w(TAG, "Sign in request has failed because user cancel the process");
                signalParams.put("status", 1);
                signalParams.put("message", "Sign in request has failed because user cancel the process");
                signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
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
                else{
                    signalParams.put("status", 1);
                    signalParams.put("message", "Sign in request has failed because google id token is null");
                    signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                    godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
                }
            }
            catch (ApiException e){
                Log.w(TAG, "link account with Google failed :" + e.getLocalizedMessage(), e);
                Log.w(TAG, "Sign in request has failed because user cancel the process");
                signalParams.put("status", 1);
                signalParams.put("message", "Sign in request has failed because user cancel the process");
                signalParams.put("data", new GodotFirebaseUser(null).ToDictionary());
                godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Dictionary signalParams = new Dictionary();
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
                            signalParams.put("status", 0);
                            signalParams.put("message", "Sign in with google credential has succeed");
                            signalParams.put("data", user.ToDictionary());
                        } else {
                            // sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            signalParams.put("status", 1);
                            signalParams.put("message", "Sign in with google credential has failed");
                            signalParams.put("data", user.ToDictionary());
                        }

                        godotOnFire.emitGodotSignal("_on_google_signin_completed", signalParams);
                    }
                });
    }

    // TODO: Bug, setiap user ex anonymouse yang sudah di link, ketika login, cuma bisa diambil uid nya saja
    // workaround jadi ketika sudah login, next time dia start aplikasi baru bisa di tarik data email dll.
    private void linkWithGoogleCredential(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Dictionary signalParams = new Dictionary();
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithGoogleCredential:success");
                            FirebaseUser currentUser = task.getResult().getUser();
                            user = new GodotFirebaseUser(currentUser);
                            signalParams.put("status", 0);
                            signalParams.put("message", "Sign in with google credential has succeed");
                            signalParams.put("data", user.ToDictionary());
                        }
                        else{
                            Log.w(TAG, "linkWithGoogleCredential:failure", task.getException());
                            signalParams.put("status", 1);
                            signalParams.put("message", "Link with google credential has failed");
                            signalParams.put("data", user.ToDictionary());

                        }
                        godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
                    }
        });
    }
}
