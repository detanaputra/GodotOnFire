package id.maingames.godotonfire.authentications;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.godotengine.godot.Dictionary;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.R;
import id.maingames.godotonfire.utilities.SignalParams;

/**
 *
 * Signals:
 * _sign_in_anonymously_completed
 * */
public class AnonymousSignin {
    private static String TAG = "";
    private static AnonymousSignin instance;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    private FirebaseAuth mAuth;

    public AnonymousSignin() {

    }

    public static AnonymousSignin getInstance(){
        if(instance == null){
            Log.w(TAG, "Anonymous instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("Anonymous Activity is null"));
        }
        return instance;
    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new AnonymousSignin();
        TAG = _godotOnFire.getPluginName();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mAuth = FirebaseAuth.getInstance();
    }


    public void signIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(godotActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GodotFirebaseUser user = new GodotFirebaseUser(null);
                        SignalParams signalParams = new SignalParams();
                        if(task.isSuccessful()){
                            // sign in success
                            Log.d(TAG, "Sign In Anonymously is successful");
                            signalParams.Status = 0;
                            signalParams.Message = "Sign In anonymously is successful";
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            // sign in failed
                            signalParams.Status = 1;
                            signalParams.Message = "Sign in anonymously has failed";
                            Log.w(TAG, "signInAnonymously has failed. ", task.getException());
                        }
                        signalParams.Data = user.toJson();
                        godotOnFire.emitGodotSignal(godotActivity.getString(R.string.GOF_sign_in_anonymously_completed), signalParams.toDictionary());
                    }
                });
    }
}
