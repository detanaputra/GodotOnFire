package id.maingames.godotonfire.firestores;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.godotengine.godot.Dictionary;

import java.util.Map;

import id.maingames.godotonfire.GodotOnFire;

public class Firestore {
    private static final String TAG = "Firestore";
    private static Firestore instance;
    private FirebaseFirestore database;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    
    public Firestore(){

    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new Firestore();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.database = FirebaseFirestore.getInstance();
    }

    public static Firestore getInstance(){
        if(instance == null){
            Log.w(TAG, "Firestore instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("FirestoreActivity instance is null"));
        }
        return instance;
    }

    public void WriteUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "firestoreWriteUserdata:failure. Firebase user is null, user might be signed out");
            godotOnFire.emitGodotSignal("_on_firestore_write_completed", signalParams);
            return;
        }
        database.collection(collName).document(user.getUid()).set(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore write has success");
                    Log.d(TAG, "firestoreWriteUserdata:success");
                    godotOnFire.emitGodotSignal("_on_firestore_write_completed", signalParams);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database write has failed");
                    Log.w(TAG, "firesoreWriteUserdata:failed " + e.getLocalizedMessage());
                    Log.e(TAG, "firesoreWriteUserdata:failed " + e);
                    godotOnFire.emitGodotSignal("_on_firestore_write_completed", signalParams);
                }
            });
    }

    public void UpdateUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "firestoreUpdateUserdata:failure. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_firestore_update_completed", signalParams);
            return;
        }
        database.collection(collName).document(user.getUid()).update(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore update has success");
                    Log.d(TAG, "firesoreUpdateUserdata:success");
                    godotOnFire.emitGodotSignal("_on_firestore_update_completed", signalParams);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Firestore write has failed");
                    Log.w(TAG, "firestoreUpdateUserdata:failed. code: " + e.getLocalizedMessage());
                    Log.e(TAG, "firestoreUpdateUserdata:failed. code: " + e);
                    godotOnFire.emitGodotSignal("_on_firestore_update_completed", signalParams);
                }
            });
    }

    public void ReadUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.w(TAG, "firestoreReadUserdata: failure. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_firestore_read_completed", signalParams);
            return;
        }
        database.collection(collName).document(user.getUid()).get()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try{
                        Map<String, Object> obj = documentSnapshot.getData();
                        Dictionary data = new Dictionary();
                        data.putAll(obj);
                        signalParams.put("status", 0);
                        signalParams.put("message", "Firestore read has success");
                        signalParams.put("data", data);
                        Log.d(TAG, "firestoreReadUserdata:success" );
                        godotOnFire.emitGodotSignal("_on_firestore_read_completed", signalParams);
                    } catch (Exception e){
                        Log.w(TAG, "firestoreReadUserdata:failed " + e.getLocalizedMessage() );
                        Log.e(TAG, "firestoreReadUserdata:failed " + e );
                        signalParams.put("status", 1);
                        signalParams.put("message", "Firestore read has failed");
                        godotOnFire.emitGodotSignal("_on_firestore_read_completed", signalParams);
                    }

                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database read has failed");
                    Log.w(TAG, "firestoreReadUserdata:failure. " + e.getLocalizedMessage());
                    Log.e(TAG, "firestoreReadUserdata:failure. " + e);
                    godotOnFire.emitGodotSignal("_on_firestore_read_completed", signalParams);
                }
            });
    }

    public void DeleteUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.w(TAG, "firestoreDeleteUserdata: failure. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_firestore_delete_completed", signalParams);
            return;
        }
        database.collection(collName).document(user.getUid()).delete()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore delete has success");
                    Log.d(TAG, "firestoreDeleteUserdata:success");
                    godotOnFire.emitGodotSignal("_on_firestore_delete_completed", signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database delete has failed");
                    Log.w(TAG, "firestoreDeleteUserdata:failure " + e.getLocalizedMessage());
                    Log.e(TAG, "firestoreDeleteUserdata:failure " + e);
                    godotOnFire.emitGodotSignal("_on_firestore_delete_completed", signalParams);
                }
            });
    }


}
