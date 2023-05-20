package id.maingames.godotonfire.authentications;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import id.maingames.godotonfire.GodotOnFire;

public class AnonymousSignin {
    private static final String TAG = "AnonymousAuth";
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
                        if(task.isSuccessful()){
                            // sign in success
                            Log.d(TAG, "signInAnonymously:success");
                            user = new GodotFirebaseUser(task.getResult().getUser());
                        }
                        else{
                            // sign in failed
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                        godotOnFire.emitGodotSignal("_on_signin_anonymously_completed", user.ToDictionary());
                    }
                });
    }
}
