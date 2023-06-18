package id.maingames.godotonfire;

import android.content.Intent;
import android.util.Log;

import androidx.collection.ArraySet;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Set;

import id.maingames.godotonfire.analytics.Analytics;
import id.maingames.godotonfire.authentications.AnonymousSignin;
import id.maingames.godotonfire.authentications.EmailSignin;
import id.maingames.godotonfire.authentications.GodotFirebaseUser;
import id.maingames.godotonfire.authentications.GoogleSignin;
import id.maingames.godotonfire.databases.RealtimeDatabase;
import id.maingames.godotonfire.firestores.Firestore;
import id.maingames.godotonfire.remoteConfigs.RemoteConfig;
import id.maingames.godotonfire.utilities.SignalParams;

public class GodotOnFire extends GodotPlugin {

    public GodotOnFire(Godot godot) {
        super(godot);
    }

    @UsedByGodot
    public void init(){
        String signalName = getActivity().getString(R.string.GOF_godotonfire_initiated);
        Dictionary signalParams = new Dictionary();
        try{
            AnonymousSignin.init(this, getActivity());
            GoogleSignin.init(this, getActivity());
            EmailSignin.init(this, getActivity());
            listenAuthState();
            RealtimeDatabase.init(this, getActivity());
            Firestore.init(this, getActivity());
            Analytics.init(this, getActivity());
            RemoteConfig.init(this, getActivity());

            signalParams.put("status", 0);
            signalParams.put("message", "GodotOnFire initiated successfully");
            emitGodotSignal(signalName, signalParams);
        }
        catch (Exception e){
            signalParams.put("status", 1);
            signalParams.put("message", "GodotOnFire initiation has failed");
            Log.e(getPluginName(), "GodotOnFire initiation has failed: " + e.getLocalizedMessage(), e);
            emitGodotSignal(signalName, signalParams);
        }
    }

    public void emitGodotSignal(String signalName, Dictionary signalArgs){
        emitSignal(signalName, signalArgs);
    }

    @UsedByGodot
    public void getFirebaseUser(){
        String className = getClass().getSimpleName() + " ";
        String method = "getFirebaseUser ";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GodotFirebaseUser godotUser = new GodotFirebaseUser(user);
        SignalParams signalParams = new SignalParams();
        if (user != null){
            signalParams.Status = 0;
            signalParams.Message = "Firebase user obtained";
        }
        else{
            Log.e(getPluginName(),  className + method + "has failed. Firebase user is null, user might be signed out.");
            signalParams.Status = 1;
            signalParams.Message = "Firebase user is null. It might be because user is signed out";
        }
        signalParams.Data = godotUser.toJson();
        emitGodotSignal(getActivity().getString(R.string.GOF_get_firebase_user_completed), signalParams.toDictionary());
    }

    @UsedByGodot
    public void signOut(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

    private void listenAuthState(){
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Dictionary signalParams = new Dictionary();
                signalParams.put("status", 0);
                if (user != null){
                    signalParams.put("message", "User signed in successfully");
                    GodotFirebaseUser godotUser = new GodotFirebaseUser(user);
                    signalParams.put("data", godotUser.toJson());
                    emitGodotSignal(getActivity().getString(R.string.GOF_firebase_user_signed_in), signalParams);
                }
                else{
                    signalParams.put("message", "User signed out successfully");
                    emitGodotSignal(getActivity().getString(R.string.GOF_firebase_user_signed_out), signalParams);
                }
            }
        });
    }

    @UsedByGodot
    public void signInAnonymously(){
        AnonymousSignin.getInstance().signIn();
    }

    @UsedByGodot
    public void signInGoogle(){
        GoogleSignin.getInstance().signIn();
    }

    @UsedByGodot
    public void linkAccountWithGoogle(){
        GoogleSignin.getInstance().linkAccount();
    }

    @UsedByGodot
    public void signUpWithEmail(String email, String password){
        EmailSignin.getInstance().signUp(email, password);
    }

    @UsedByGodot
    public void signInWithEmail(String email, String password){
        EmailSignin.getInstance().signIn(email, password);
    }

    @UsedByGodot
    public void linkAccountWithEmail(String email, String password){
        EmailSignin.getInstance().linkAccount(email, password);
    }

    @UsedByGodot
    public void sendEmailVerification(){
        EmailSignin.getInstance().sendEmailVerification();
    }

    // Realtime Database region
    @UsedByGodot
    public void databaseSetUserData(String collName, String jsonString){
        RealtimeDatabase.getInstance().set(collName, jsonString, true);
    }

    @UsedByGodot
    public void databaseSetData(String collName, String jsonString){
        RealtimeDatabase.getInstance().set(collName, jsonString, false);
    }

    @UsedByGodot
    public void databasePushUserData(String collName, String jsonString){
        RealtimeDatabase.getInstance().push(collName, jsonString, true);
    }

    @UsedByGodot
    public void databasePushData(String collName, String jsonString){
        RealtimeDatabase.getInstance().push(collName, jsonString, false);
    }

    @UsedByGodot
    public void databaseUpdateUserData(String collName, String jsonString){
        RealtimeDatabase.getInstance().update(collName, jsonString, true);
    }

    @UsedByGodot
    public void databaseUpdateData(String collName, String jsonString){
        RealtimeDatabase.getInstance().update(collName, jsonString, false);
    }

    @UsedByGodot
    public void databaseGetUserData(String collName){
        RealtimeDatabase.getInstance().get(collName, true);
    }

    @UsedByGodot
    public void databaseGetData(String collName){
        RealtimeDatabase.getInstance().get(collName, false);
    }

    @UsedByGodot
    public void databaseRemoveUserData(String collName){
        RealtimeDatabase.getInstance().remove(collName, true);
    }

    @UsedByGodot
    public void databaseRemoveData(String collName){
        RealtimeDatabase.getInstance().remove(collName, false);
    }

    // Realtime Database end region

    @UsedByGodot
    public void firestoreSetUserData(String collName, String jsonString){
        Firestore.getInstance().set(collName, jsonString, null);
    }

    @UsedByGodot
    public void firestoreSetData(String collName, String jsonString, String docName){
        Firestore.getInstance().set(collName, jsonString, docName);
    }

    @UsedByGodot
    public void firestoreAddData(String collName, String jsonString){
        Firestore.getInstance().add(collName, jsonString);
    }

    @UsedByGodot
    public void firestoreUpdateUserData(String collName, String jsonString){
        Firestore.getInstance().update(collName, jsonString, null);
    }

    @UsedByGodot
    public void firestoreUpdateData(String collName, String jsonString, String docName){
        Firestore.getInstance().update(collName, jsonString, docName);
    }

    @UsedByGodot
    public void firestoreReadUserData(String collName){
        Firestore.getInstance().read(collName, null);
    }

    @UsedByGodot
    public void firestoreReadData(String collName, String docName){
        Firestore.getInstance().read(collName, docName);
    }

    @UsedByGodot
    public void firestoreDeleteUserData(String collName){
        Firestore.getInstance().delete(collName, null);
    }

    @UsedByGodot
    public void firestoreDeleteData(String collName, String docName){
        Firestore.getInstance().delete(collName, docName);
    }

    @UsedByGodot
    public void logEvent(String eventName, Dictionary params){
        Analytics.getInstance().logEvent(eventName, params);
    }

    @UsedByGodot
    public void setUserProperty(String propertyName, String value){
        Analytics.getInstance().setUserProperty(propertyName, value);
    }

    @UsedByGodot
    public void testCrash(){
        throw new RuntimeException("this is forced crash to test Firebase Crashlytics. Do not call this method in production");
    }

    @UsedByGodot
    public void remoteConfigFetch(){
        RemoteConfig.getInstance().fetch();
    }
    @UsedByGodot
    public void remoteConfigActivate(){
        RemoteConfig.getInstance().activate();
    }
    @UsedByGodot
    public void remoteConfigFetchAndActivate(){
        RemoteConfig.getInstance().fetchAndActivate();
    }
    @UsedByGodot
    public void remoteConfigGetString(String key){
        RemoteConfig.getInstance().getString(key);
    }
    @UsedByGodot
    public void remoteConfigGetLong(String key){
        RemoteConfig.getInstance().getLong(key);
    }
    @UsedByGodot
    public void remoteConfigGetBoolean(String key){
        RemoteConfig.getInstance().getBoolean(key);
    }
    @UsedByGodot
    public void remoteConfigGetDouble(String key){
        RemoteConfig.getInstance().getDouble(key);
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        super.onMainActivityResult(requestCode, resultCode, data);
        GoogleSignin.getInstance().onMainActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotOnFire";
    }

     @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_godotonfire_initiated), Dictionary.class));

        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_get_firebase_user_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firebase_user_signed_in), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firebase_user_signed_out), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_sign_in_anonymously_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_google_sign_in_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_link_account_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_email_sign_up_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_email_sign_in_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_send_email_verification_completed), Dictionary.class));

        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_database_set_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_database_push_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_database_update_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_database_get_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_database_remove_completed), Dictionary.class));

        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firestore_set_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firestore_add_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firestore_update_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firestore_read_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_firestore_delete_completed), Dictionary.class));

        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_remote_config_fetch_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_remote_config_activate_completed), Dictionary.class));
        signals.add(new SignalInfo(getActivity().getString(R.string.GOF_remote_config_get_value_completed), Dictionary.class));
        return signals;
    }
}