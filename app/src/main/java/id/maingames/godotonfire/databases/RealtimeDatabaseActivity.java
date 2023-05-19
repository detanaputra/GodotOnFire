package id.maingames.godotonfire.databases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

import id.maingames.godotonfire.R;

public class RealtimeDatabaseActivity extends GodotPlugin {
    private static final String TAG = "RealtimeDatabase";

    private static RealtimeDatabaseActivity instance;

    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    public RealtimeDatabaseActivity (Godot godot){
        super(godot);
    }

    public static void init(Godot godot){
        instance = new RealtimeDatabaseActivity(godot);
        instance.database = FirebaseDatabase.getInstance();
        instance.database.setPersistenceEnabled(true);
        instance.dbRef = instance.database.getReference();
    }

    public static RealtimeDatabaseActivity getInstance(){
        if(instance == null){
            Log.w(TAG, "Realtime database instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("RealtimeDatabase instance is null"));
        }
        return instance;
    }

    public void WriteUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            emitSignal("_on_database_write_completed", false);
            return;
        }
        dbRef.child(collName).child(user.getUid()).setValue(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "realtimeDatabaseWriteUserdata:success");
                    emitSignal("_on_database_write_completed", true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Write failed
                    Log.w(TAG, "realtimeDatabaseWriteUserdata:failed");
                    emitSignal("_on_database_write_completed", false);
                }
            });
    }

    public void UpdateUserData(String collName, Dictionary data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            emitSignal("_on_database_update_completed", false);
            return;
        }
        dbRef.child(collName).child(user.getUid()).updateChildren(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "realtimeDatabaseUpdateUserdata:success");
                    emitSignal("_on_database_update_completed", true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "realtimeDatabaseUpdateUserdata:failed");
                    emitSignal("_on_database_update_completed", false);
                }
            });
    }

    public void ReadUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.e(TAG, "realtimeDatabaseReadUserdata:failure");
            emitSignal("_on_database_read_completed", null);
            return;
        }
        dbRef.child(collName).child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String data = String.valueOf(task.getResult().getValue());
                    Log.d(TAG, "realtimeDatabaseReadUserdata:success");
                    emitSignal("_on_database_read_completed", data);
                }
                else {
                    Log.e(TAG, "realtimeDatabaseReadUserdata:failure", task.getException());
                    emitSignal("_on_database_read_completed", null);
                }
            }
        });
    }

    public void DeleteUserData(String collName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.e(TAG, "realtimeDatabaseDeleteUserdata:failure");
            emitSignal("_on_database_delete_completed", false);
            return;
        }
        dbRef.child(collName).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "realtimeDatabaseDeleteUserdata:success");
                emitSignal("_on_database_delete_completed", true);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "realtimeDatabaseDeleteUserdata:failure");
                emitSignal("_on_database_delete_completed", false);
            }
        });
    }

    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }
}
