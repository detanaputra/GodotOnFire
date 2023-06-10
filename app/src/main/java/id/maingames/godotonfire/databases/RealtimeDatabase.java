package id.maingames.godotonfire.databases;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.godotengine.godot.Dictionary;

import java.util.Map;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.utilities.JsonConverter;
import id.maingames.godotonfire.utilities.SignalParams;

/**
 * This is a class reference to interact with Firebase Realtime Database.
 *
 * Signals:
 * _database_set_completed
 * _database_push_completed
 * _database_update_completed
 * _database_get_completed
 * _database_remove_completed
 *
*/
public class RealtimeDatabase {
    private static String TAG = "";
    private static RealtimeDatabase instance;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;

    public RealtimeDatabase(){

    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new RealtimeDatabase();
        TAG = _godotOnFire.getPluginName();
        instance.database = FirebaseDatabase.getInstance();
        instance.database.setPersistenceEnabled(true);
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.dbRef = instance.database.getReference();
    }

    public static RealtimeDatabase getInstance(){
        if(instance == null){
            Log.w(TAG, "Realtime database instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("RealtimeDatabase instance is null"));
        }
        return instance;
    }

    public void set(String collName, String jsonString, Boolean isUserData){
        String signalName = "_database_set_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        SignalParams signalParams = new SignalParams();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG,  className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.e(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        if(isUserData){
            dbRef.child(collName).child(user.getUid());
        }
        else{
            dbRef.child(collName);
        }
        dbRef.setValue(_data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    signalParams.Status = 0;
                    signalParams.Message = "Database set value has succeed";
                    Log.d(TAG, className + method + "has succeed");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "Database set value has failed";
                    Log.e(TAG, className + method + "has failed. code: " + e.getLocalizedMessage(), e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });

    }

    public void push(String collName, String jsonString, Boolean isUserData){
        String signalName = "_database_push_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        SignalParams signalParams = new SignalParams();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG,  className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.e(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        if(isUserData){
            dbRef.child(collName).child(user.getUid());
        }
        else{
            dbRef.child(collName);
        }
        dbRef.push().setValue(_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signalParams.Status = 0;
                        signalParams.Message = "Database set value has succeed";
                        Log.d(TAG, className + method + "has succeed");
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signalParams.Status = 1;
                        signalParams.Message = "Database set value has failed";
                        Log.e(TAG, className + method + "has failed. code: " + e.getLocalizedMessage(), e);
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                });

    }

    public void update(String collName, String jsonString, Boolean isUserData){
        String signalName = "_database_update_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        SignalParams signalParams = new SignalParams();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG,  className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.e(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        if(isUserData){
            dbRef.child(collName).child(user.getUid());
        }
        else{
            dbRef.child(collName);
        }
        dbRef.updateChildren(_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signalParams.Status = 0;
                        signalParams.Message = "Database update value has succeed";
                        Log.d(TAG, className + method + "has succeed");
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signalParams.Status = 1;
                        signalParams.Message = "Database update value has failed";
                        Log.e(TAG, className + method + "has failed. code: " + e.getLocalizedMessage(), e);
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                });
    }

    public void get(String collName, Boolean isUserData){
        String signalName = "_database_get_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        SignalParams signalParams = new SignalParams();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG,  className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        if(isUserData){
            dbRef.child(collName).child(user.getUid());
        }
        else{
            dbRef.child(collName);
        }
        dbRef.get()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    try{
                        Map<String, Object> obj = (Map<String, Object>)dataSnapshot.getValue();
                        String data = JsonConverter.mapToJson(obj);
                        signalParams.Status = 0;
                        signalParams.Message = "Database read has succeed";
                        signalParams.Data = data;
                        Log.d(TAG, "Database read has succeed");
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    } catch (Exception e){
                        Log.e(TAG,  className + method + "has failed. " + e.getLocalizedMessage());
                        Log.e(TAG, className + method + "has failed " + e);
                        signalParams.Status = 1;
                        signalParams.Message = "Database read has failed";
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }

                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,  className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed " + e);
                    signalParams.Status = 1;
                    signalParams.Message = "Database read has failed";
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }

    public void remove(String collName, Boolean isUserData){
        String signalName = "_database_remove_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        SignalParams signalParams = new SignalParams();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG,  className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        if(isUserData){
            dbRef.child(collName).child(user.getUid());
        }
        else{
            dbRef.child(collName);
        }
        dbRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                signalParams.Status = 0;
                signalParams.Message = "Database delete has succeed";
                Log.d(TAG, className + method + "has succeed");
                godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signalParams.Status = 1;
                signalParams.Message = "Database delete has failed";
                Log.e(TAG, className + method + "has failed. " + e.getLocalizedMessage(), e);
                godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            }
        });
    }
}
