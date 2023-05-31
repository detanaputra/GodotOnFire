package id.maingames.godotonfire.authentications;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Dictionary;

import id.maingames.godotonfire.GodotOnFire;

public class EmailSignin {
    private static String TAG = "";
    private static EmailSignin instance;
    private FirebaseAuth mAuth;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;

    public EmailSignin() {

    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new EmailSignin();
        TAG = _godotOnFire.getPluginName();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mAuth = FirebaseAuth.getInstance();
    }

    public static EmailSignin getInstance(){
        if(instance == null){
            Log.w(TAG, "Email sign in instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("EmailSigninActivity is null"));
        }
        return instance;
    }

    public void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        Dictionary signalParams = new Dictionary();
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            signalParams.put("status", 0);
                            signalParams.put("message", "Create user with email has succeed");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            signalParams.put("status", 1);
                            signalParams.put("message", "Create user with email has failed");
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        signalParams.put("data", user.ToDictionary());
                        godotOnFire.emitGodotSignal("_on_signup_email_completed", signalParams);
                    }
                });
    }

    public void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        Dictionary signalParams = new Dictionary();
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:success");
                            signalParams.put("status", 0);
                            signalParams.put("message", "Sign in with email has succeed");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            signalParams.put("status", 1);
                            signalParams.put("message", "Sign in with email has failed");
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                        signalParams.put("data", user.ToDictionary());
                        godotOnFire.emitGodotSignal("_on_signin_email_completed", signalParams);
                    }
                });
    }

    public void linkAccount(String email, String password){
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        Dictionary signalParams = new Dictionary();
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithEmailCredential:success");
                            signalParams.put("status", 0);
                            signalParams.put("message", "Link with email credential has succeed");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "linkWithEmailCredential:failure", task.getException());
                            signalParams.put("status", 1);
                            signalParams.put("message", "Link with email credential has failed");
                        }
                        signalParams.put("data", user.ToDictionary());
                        godotOnFire.emitGodotSignal("_on_link_account_completed", signalParams);
                    }
                });
    }

    public void sendEmailVerification(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(godotActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Dictionary signalParams = new Dictionary();
                        if(task.isSuccessful()){
                            signalParams.put("status", 0);
                            signalParams.put("message", "Send email verification has succeed");
                            signalParams.put("data", true);
                            Log.d(TAG, "sendEmailVerification:success");
                        }
                        else{
                            signalParams.put("status", 1);
                            signalParams.put("message", "Send email verification has failed");
                            signalParams.put("data", false);
                            Log.w(TAG, "sendEmailVerification:failure ", task.getException());
                        }
                        godotOnFire.emitGodotSignal("_on_send_email_verification_completed", signalParams);
                    }
                });
    }
}
