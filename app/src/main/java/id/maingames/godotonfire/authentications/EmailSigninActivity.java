package id.maingames.godotonfire.authentications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

import id.maingames.godotonfire.R;

public class EmailSigninActivity extends GodotPlugin {
    private static final String TAG = "EmailActivity";
    private static EmailSigninActivity instance;
    private FirebaseAuth mAuth;

    public EmailSigninActivity(Godot godot) {
        super(godot);
    }

    public static void init(Godot godot){
        instance = new EmailSigninActivity(godot);
        instance.mAuth = FirebaseAuth.getInstance();
    }

    public static EmailSigninActivity getInstance(){
        if(instance == null){
            Log.w(TAG, "Email sign in instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("EmailSigninActivity is null"));
        }
        return instance;
    }

    public void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        emitSignal("_on_signup_email_completed", user.ToDictionary());
                    }
                });
    }

    public void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:success");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                        emitSignal("_on_signin_email_completed", user.ToDictionary());
                    }
                });
    }

    public void linkAccount(String email, String password){
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithEmailCredential:success");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "linkWithEmailCredential:failure", task.getException());
                        }
                        emitSignal("_on_link_account_completed", user.ToDictionary());
                    }
                });
    }

    public void sendEmailVerification(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        int emailSent = 1;
                        if(task.isSuccessful()){
                            emailSent = 0;
                            Log.d(TAG, "sendEmailVerification:success");
                        }
                        else{
                            Log.w(TAG, "sendEmailVerification:failure");
                        }
                        emitSignal("_on_send_email_verification_completed", emailSent);
                    }
                });
    }

    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }
}
