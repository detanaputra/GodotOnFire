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

    public void WriteUserData(String collName, String jsonString){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "realtimeDatabaseWriteUserdata:failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_database_write_completed", signalParams);
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Failed to marshall json string to Dictionary");
            Log.w(TAG, "realtimeDatabaseWriteUserdata:failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal("_on_database_write_completed", signalParams);
            return;
        }
        dbRef.child(collName).child(user.getUid()).setValue(_data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Database write has success");
                    Log.d(TAG, "realtimeDatabaseWriteUserdata:success");
                    godotOnFire.emitGodotSignal("_on_database_write_completed", signalParams);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database write has failed");
                    Log.e(TAG, "realtimeDatabaseWriteUserdata:failed. code: " + e.getLocalizedMessage());
                    godotOnFire.emitGodotSignal("_on_database_write_completed", signalParams);
                }
            });
    }

    public void UpdateUserData(String collName, String jsonString){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "realtimeDatabaseUpdateUserdata:failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_database_update_completed", signalParams);
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Failed to marshall json string to Dictionary");
            Log.w(TAG, "realtimeDatabaseUpdateUserdata:failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal("_on_database_update_completed", signalParams);
            return;
        }
        dbRef.child(collName).child(user.getUid()).updateChildren(_data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Database update has success");
                    Log.d(TAG, "realtimeDatabaseUpdateUserdata:success");
                    godotOnFire.emitGodotSignal("_on_database_update_completed", signalParams);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database write has failed");
                    Log.w(TAG, "realtimeDatabaseUpdateUserdata:failed. code: " + e.getLocalizedMessage());
                    Log.e(TAG, "realtimeDatabaseUpdateUserdata:failed. code: " + e);
                    godotOnFire.emitGodotSignal("_on_database_update_completed", signalParams);
                }
            });
    }

    public void ReadUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "realtimeDatabaseWriteUserdata:failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_database_read_completed", signalParams);
            return;
        }
        dbRef.child(collName).child(user.getUid()).get()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    try{
                        Map<String, Object> obj = (Map<String, Object>)dataSnapshot.getValue();
                        /*Dictionary data = new Dictionary();
                        data.putAll(obj);*/
                        String data = JsonConverter.mapToJson(obj);
                        signalParams.put("status", 0);
                        signalParams.put("message", "Database read has success");
                        signalParams.put("data", data);
                        Log.d(TAG, "realtimeDatabaseReadUserdata:success " + data);
                        godotOnFire.emitGodotSignal("_on_database_read_completed", signalParams);
                    } catch (Exception e){
                        Log.w(TAG, "realtimeDatabaseReadUserdata:failed " + e.getLocalizedMessage());
                        Log.e(TAG, "realtimeDatabaseReadUserdata:failed " + e);
                        signalParams.put("status", 1);
                        signalParams.put("message", "Database read has failed");
                        godotOnFire.emitGodotSignal("_on_database_read_completed", signalParams);
                    }

                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database read has failed");
                    Log.e(TAG, "realtimeDatabaseReadUserdata:failure", e);
                    godotOnFire.emitGodotSignal("_on_database_read_completed", signalParams);
                }
            });
    }

    public void DeleteUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, "realtimeDatabaseDeleteUserdata:failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal("_on_database_delete_completed", signalParams);
            return;
        }
        dbRef.child(collName).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                signalParams.put("status", 0);
                signalParams.put("message", "Database delete has success");
                Log.d(TAG, "realtimeDatabaseDeleteUserdata:success");
                godotOnFire.emitGodotSignal("_on_database_delete_completed", signalParams);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signalParams.put("status", 1);
                signalParams.put("message", "Database delete has failed");
                Log.e(TAG, "realtimeDatabaseDeleteUserdata:failure. code: " + e.getLocalizedMessage());
                godotOnFire.emitGodotSignal("_on_database_delete_completed", signalParams);
            }
        });
    }
}
