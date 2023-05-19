package id.maingames.godotonfire.firestores;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

public class FirestoreActivity extends GodotPlugin {
    private static final String TAG = "Firestore";

    private static FirestoreActivity instance;

    private FirebaseFirestore database;
    
    public FirestoreActivity (Godot godot){
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return null;
    }

    public static void init(Godot godot){
        instance = new FirestoreActivity(godot);
        instance.database = FirebaseFirestore.getInstance();
    }

    public static FirestoreActivity getInstance(){
        if(instance == null){
            Log.w(TAG, "Firestore instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("FirestoreActivity instance is null"));
        }
        return instance;
    }

    public void WriteUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.w(TAG, "firestoreWriteUserdata:failure. User is not signed in");
            emitSignal("_on_firestore_write_completed", false);
            return;
        }
        database.collection(collName).document(user.getUid()).set(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "firestoreWriteUserdata:success");
                    emitSignal("_on_firestore_write_completed", true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Write failed
                    Log.w(TAG, "firesoreWriteUserdata:failed");
                    emitSignal("_on_firestore_write_completed", false);
                }
            });
    }

    public void UpdateUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.w(TAG, "firestoreUpdateUserdata:failure. User is not signed in");
            emitSignal("_on_firestore_update_completed", false);
            return;
        }
        database.collection(collName).document(user.getUid()).update(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "firesoreUpdateUserdata:success");
                    emitSignal("_on_firestore_update_completed", true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "firesoreUpdateUserdata:failed");
                    emitSignal("_on_firestore_update_completed", false);
                }
            });
    }

    public void ReadUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.w(TAG, "firestoreReadUserdata: failure. User is not signed in.");
            emitSignal("_on_firestore_read_completed", null);
            return;
        }
        database.collection(collName).document(user.getUid()).get().addOnCompleteListener(this.getActivity(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.d(TAG, "firestoreReadUserdata:success" );
                        emitSignal("_on_firestore_read_completed", document.getData());
                    }
                    else{
                        Log.w(TAG, "firestoreReadUserdata:failure. Snapshot is not exist");
                        emitSignal("_on_firestore_read_completed", null);
                    }
                }
                else{
                    Log.w(TAG, "firestoreReadUserdata: failure");
                    emitSignal("_on_firestore_read_completed", null);
                }
            }
        });
    }

    public void DeleteUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.w(TAG, "firestoreDeleteUserdata: failure. User is not signed in.");
            emitSignal("_on_firestore_delete_completed", false);
            return;
        }
        database.collection(collName).document(user.getUid()).delete().addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "firestoreDeleteUserdata:success");
                    emitSignal("_on_firestore_delete_completed", true);
                }
                else{
                    Log.d(TAG, "firestoreDeleteUserdata:failure");
                    emitSignal("_on_firestore_delete_completed", false);
                }
            }
        });
    }


}
