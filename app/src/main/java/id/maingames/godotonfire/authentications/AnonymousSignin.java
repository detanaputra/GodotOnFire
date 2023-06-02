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
                        Dictionary signalParams = new Dictionary();
                        if(task.isSuccessful()){
                            // sign in success
                            Log.d(TAG, "signInAnonymously:success");
                            signalParams.put(godotActivity.getString(R.string.status), 0);
                            signalParams.put(godotActivity.getString(R.string.message), "Sign in anonymously has succeed");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            // sign in failed
                            signalParams.put(godotActivity.getString(R.string.status), 1);
                            signalParams.put(godotActivity.getString(R.string.message), "Sign in anonymously has failed");
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                        signalParams.put(godotActivity.getString(R.string.data), user.toJson());
                        godotOnFire.emitGodotSignal("_on_signin_anonymously_completed", signalParams);
                    }
                });
    }
}
