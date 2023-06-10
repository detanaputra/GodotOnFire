package id.maingames.godotonfire.firestores;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.godotengine.godot.Dictionary;

import java.util.Map;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.utilities.JsonConverter;

/**
 * This is a class to interact with Firebase Firestore.
 *
 * Signals:
 * _firestore_set_completed
 * _firestore_add_completed
 * _firestore_update_completed
 * _firestore_read_completed
 * _firestore_delete_completed
 * **/
public class Firestore {
    private static String TAG = "";
    private static Firestore instance;
    private FirebaseFirestore database;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    
    public Firestore(){

    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new Firestore();
        TAG = _godotOnFire.getPluginName();
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

    public void setData(String collName, String jsonString, String docName){
        String signalName = "_firestore_set_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Failed to marshall json string to Dictionary");
            Log.w(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        DocumentReference documentReference;
        if (docName == null || docName.isEmpty() || docName.trim().isEmpty()){
            documentReference = database.collection(collName).document(user.getUid());
        }
        else{
            documentReference = database.collection(collName).document(docName);
        }
        documentReference.set(_data)
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore set is successful");
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Firestore set has failed");
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            });
    }

    public void addData(String collName, String jsonString){
        String signalName = "_firestore_add_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Failed to marshall json string to Dictionary");
            Log.w(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        database.collection(collName).add(_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        signalParams.put("status", 0);
                        signalParams.put("message", "Firestore set is successful");
                        Log.d(TAG, className + method + "is successful.");
                        godotOnFire.emitGodotSignal(signalName, signalParams);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signalParams.put("status", 1);
                        signalParams.put("message", "Firestore set has failed");
                        Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                        Log.e(TAG, className + method + "has failed. " + e);
                        godotOnFire.emitGodotSignal(signalName, signalParams);
                    }
                });
    }

    public void updateData(String collName, String jsonString, String docName){
        String signalName = "_firestore_update_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Failed to marshall json string to Dictionary");
            Log.w(TAG, "firestoreWriteUserdata:failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal("_on_firestore_update_completed", signalParams);
            return;
        }
        DocumentReference documentReference;
        if (docName == null || docName.isEmpty() || docName.trim().isEmpty()){
            documentReference = database.collection(collName).document(user.getUid());
        }
        else{
            documentReference = database.collection(collName).document(docName);
        }
        documentReference.update(_data)
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore update is successful");
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Firestore write has failed");
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            });
    }

    public void readData(String collName, String docName){
        String signalName = "_firestore_read_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        DocumentReference documentReference;
        if (docName == null || docName.isEmpty() || docName.trim().isEmpty()){
            documentReference = database.collection(collName).document(user.getUid());
        }
        else{
            documentReference = database.collection(collName).document(docName);
        }
        documentReference.get()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try{
                        Map<String, Object> obj = documentSnapshot.getData();
                        String data = JsonConverter.mapToJson(obj);
                        signalParams.put("status", 0);
                        signalParams.put("message", "Firestore read has success");
                        signalParams.put("data", data);
                        Log.d(TAG, className + method + "is successful.");
                        godotOnFire.emitGodotSignal(signalName, signalParams);
                    } catch (Exception e){
                        Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                        Log.e(TAG, className + method + "has failed. " + e);
                        signalParams.put("status", 1);
                        signalParams.put("message", "Firestore read has failed");
                        godotOnFire.emitGodotSignal(signalName, signalParams);
                    }

                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    signalParams.put("status", 1);
                    signalParams.put("message", "Firestore read has failed");
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            });
    }

    public void deleteData(String collName, String docName){
        String signalName = "_firestore_delete_completed";
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Dictionary signalParams = new Dictionary();
        if (user == null){
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null");
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams);
            return;
        }
        DocumentReference documentReference;
        if (docName == null || docName.isEmpty() || docName.trim().isEmpty()){
            documentReference = database.collection(collName).document(user.getUid());
        }
        else{
            documentReference = database.collection(collName).document(docName);
        }
        documentReference.delete()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "Firestore delete has success");
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "Database delete has failed");
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams);
                }
            });
    }


}
