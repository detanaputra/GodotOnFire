package id.maingames.godotonfire.authentications;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

import id.maingames.godotonfire.R;

public class GoogleSigninActivity extends GodotPlugin {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LINK = 9002;
    private static GoogleSigninActivity instance;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public GoogleSigninActivity(Godot godot) {
        super(godot);
    }

    public static void init(Godot godot){
        instance = new GoogleSigninActivity(godot);
        instance.mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(godot.getString(R.string.default_web_client_id))
                .requestEmail().requestId()
                .build();

        instance.mGoogleSignInClient = GoogleSignIn.getClient(instance.getActivity(), gso);
    }

    public static GoogleSigninActivity getInstance(){
        if(instance == null){
            Log.w(TAG, "Google Sign in instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("GoogleSigninActivity is null"));
        }
        return instance;
    }

    public void signin(){
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "Launching google signin");
        getActivity().startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    public void linkAccount(){
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "Launching google link account");
        getActivity().startActivityForResult(signinIntent, RC_LINK);
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        super.onMainActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:success " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e){
                Log.w(TAG, "firebaseAuthWithGoogle:failure", e);
            }
        }
        else if (requestCode == RC_LINK){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                linkAccount(account.getIdToken());
            }
            catch (ApiException e){
                Log.w(TAG, "link account with Google failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        //FirebaseUser user = null;
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                            user = new GodotFirebaseUser(mAuth.getCurrentUser());
                            //user = mAuth.getCurrentUser();
                        } else {
                            // sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                        emitSignal("_on_google_signin_completed", user.ToDictionary());
                    }
                });
    }

    private void linkAccount(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithGoogleCredential:success");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "linkWithGoogleCredential:failure", task.getException());
                        }
                        emitSignal("_on_link_account_completed", user.ToDictionary());
                    }
        });
    }

    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }
}
