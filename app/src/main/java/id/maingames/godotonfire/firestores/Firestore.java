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
import id.maingames.godotonfire.R;
import id.maingames.godotonfire.utilities.JsonConverter;
import id.maingames.godotonfire.utilities.SignalParams;

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
        String signalName = godotActivity.getString(R.string.GOF_firestore_set_completed);
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalParams signalParams = new SignalParams();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.w(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
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
                    signalParams.Status = 0;
                    signalParams.Message = "Firestore set is successful";
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "Firestore set has failed";
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }

    public void addData(String collName, String jsonString){
        String signalName = godotActivity.getString(R.string.GOF_firestore_add_completed);
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalParams signalParams = new SignalParams();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.w(TAG, className + method + "has failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        database.collection(collName).add(_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        signalParams.Status = 0;
                        signalParams.Message = "Firestore set is successful";
                        Log.d(TAG, className + method + "is successful.");
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signalParams.Status = 1;
                        signalParams.Message = "Firestore set has failed";
                        Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                        Log.e(TAG, className + method + "has failed. " + e);
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }
                });
    }

    public void updateData(String collName, String jsonString, String docName){
        String signalName = godotActivity.getString(R.string.GOF_firestore_update_completed);
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalParams signalParams = new SignalParams();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            return;
        }
        Dictionary _data = JsonConverter.jsonToDictionary(jsonString);
        if (_data == null){
            signalParams.Status = 1;
            signalParams.Message = "Failed to marshall json string to Dictionary";
            Log.w(TAG, "firestoreWriteUserdata:failed. Failed to marshall json string to Dictionary.");
            godotOnFire.emitGodotSignal("_on_firestore_update_completed", signalParams.toDictionary());
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
                    signalParams.Status = 0;
                    signalParams.Message = "Firestore update is successful";
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "Firestore write has failed";
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }

    public void readData(String collName, String docName){
        String signalName = godotActivity.getString(R.string.GOF_firestore_read_completed);
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalParams signalParams = new SignalParams();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
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
                        signalParams.Status = 0;
                        signalParams.Message = "Firestore read has success";
                        signalParams.Data = data;
                        Log.d(TAG, className + method + "is successful.");
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    } catch (Exception e){
                        Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                        Log.e(TAG, className + method + "has failed. " + e);
                        signalParams.Status = 1;
                        signalParams.Message = "Firestore read has failed";
                        godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                    }

                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    signalParams.Status = 1;
                    signalParams.Message = "Firestore read has failed";
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }

    public void deleteData(String collName, String docName){
        String signalName = godotActivity.getString(R.string.GOF_firestore_delete_completed);
        String className = getClass().getSimpleName() + " ";
        String method = getClass().getEnclosingMethod().getName() + " ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalParams signalParams = new SignalParams();
        if (user == null){
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null";
            Log.e(TAG, className + method + "has failed. Firebase user is null, user might be signed out.");
            godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
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
                    signalParams.Status = 0;
                    signalParams.Message = "Firestore delete has success";
                    Log.d(TAG, className + method + "is successful.");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "Database delete has failed";
                    Log.w(TAG, className + method + "has failed. " + e.getLocalizedMessage());
                    Log.e(TAG, className + method + "has failed. " + e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }


}
