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

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.R;
import id.maingames.godotonfire.utilities.SignalParams;

public class EmailSignin {
    private static String TAG = "";
    private static EmailSignin instance;
    private static String signInSignalName;
    private static String signUpSignalName;
    private static String linkSignalName;
    private static String sendVerificationSignalName;
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
        signInSignalName = _godotActivity.getString(R.string.GOF_email_sign_in_completed);
        signUpSignalName = _godotActivity.getString(R.string.GOF_email_sign_up_completed);
        linkSignalName = _godotActivity.getString(R.string.GOF_link_account_completed);
        sendVerificationSignalName = _godotActivity.getString(R.string.GOF_send_email_verification_completed);
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
                        SignalParams signalParams = new SignalParams();
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            signalParams.Status = 0;
                            signalParams.Message = "Create user with email has succeed";
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            signalParams.Status = 1;
                            signalParams.Message = "Create user with email has failed";
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        signalParams.Data = user.toJson();
                        godotOnFire.emitGodotSignal(signUpSignalName, signalParams.toDictionary());
                    }
                });
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        SignalParams signalParams = new SignalParams();
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:success");
                            signalParams.Status = 0;
                            signalParams.Message = "Sign in with email has succeed";
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            signalParams.Status = 1;
                            signalParams.Message = "Sign in with email has failed";
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                        signalParams.Data = user.toJson();
                        godotOnFire.emitGodotSignal(signInSignalName, signalParams.toDictionary());
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
                        SignalParams signalParams = new SignalParams();
                        if(task.isSuccessful()){
                            Log.d(TAG, "linkWithEmailCredential:success");
                            signalParams.Status = 0;
                            signalParams.Message = "Link with email credential has succeed";
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            Log.w(TAG, "linkWithEmailCredential:failure", task.getException());
                            signalParams.Status = 1;
                            signalParams.Message = "Link with email credential has failed";
                        }
                        signalParams.Data = user.toJson();
                        godotOnFire.emitGodotSignal(linkSignalName, signalParams.toDictionary());
                    }
                });
    }

    public void sendEmailVerification(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(godotActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SignalParams signalParams = new SignalParams();
                        if(task.isSuccessful()){
                            signalParams.Status = 0;
                            signalParams.Message = "Send email verification has succeed";
                            Log.d(TAG, "sendEmailVerification:success");
                        }
                        else{
                            signalParams.Status = 1;
                            signalParams.Message = "Send email verification has failed";
                            Log.w(TAG, "sendEmailVerification:failure ", task.getException());
                        }
                        godotOnFire.emitGodotSignal(sendVerificationSignalName, signalParams.toDictionary());
                    }
                });
    }
}
