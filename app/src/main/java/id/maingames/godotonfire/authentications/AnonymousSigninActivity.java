package id.maingames.godotonfire.authentications;

import org.godotengine.godot.plugin.GodotPlugin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.godotengine.godot.Godot;

import id.maingames.godotonfire.R;

public class AnonymousSigninActivity extends GodotPlugin {
    private static final String TAG = "AnonymousAuth";
    private static Godot _godot;
    private static AnonymousSigninActivity instance;
    private FirebaseAuth mAuth;

    public AnonymousSigninActivity(Godot godot) {
        super(godot);
    }

    public static AnonymousSigninActivity getInstance(){
        if(instance == null){
            Log.w(TAG, "Anonymous instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("Anonymous Activity is null"));
        }
        return instance;
    }

    public static void init(Godot godot){
        _godot = godot;
        instance = new AnonymousSigninActivity(godot);
        instance.mAuth = FirebaseAuth.getInstance();
    }


    public void signIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
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
                        emitSignal("_on_signin_anonymously_completed", user.ToDictionary());
                    }
                });
    }




    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }
}
