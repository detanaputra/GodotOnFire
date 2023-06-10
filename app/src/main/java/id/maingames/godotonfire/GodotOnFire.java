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

public class GodotOnFire extends GodotPlugin {

    public GodotOnFire(Godot godot) {
        super(godot);
    }

    @UsedByGodot
    public void init(){
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
            emitGodotSignal("_on_godotonfire_initiated", signalParams);
        }
        catch (Exception e){
            signalParams.put("status", 1);
            signalParams.put("message", "GodotOnFire initiating has failed");
            Log.e(getPluginName(), "GodotOnFire initiating has failed: " + e.getLocalizedMessage(), e);
            emitGodotSignal("_on_godotonfire_initiated", signalParams);
        }
    }

    public void emitGodotSignal(String signalName, Dictionary signalArgs){
        emitSignal(signalName, signalArgs);
    }

    // TODO
    @UsedByGodot
    public void getFirebaseUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GodotFirebaseUser godotUser = new GodotFirebaseUser(user);
        Dictionary signalParams = new Dictionary();
        if (user != null){
            signalParams.put("status", 0);
            signalParams.put("message", "Firebase user obtained");
        }
        else{
            signalParams.put("status", 1);
            signalParams.put("message", "Firebase user is null. It might be because user is signed out");
        }
        signalParams.put("data", godotUser.toJson());
        emitGodotSignal("_on_got_firebase_user", signalParams);
    }

    @UsedByGodot
    public void signOut(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

    // TODO
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
                    emitGodotSignal("_on_firebase_user_signedIn", signalParams);
                }
                else{
                    signalParams.put("message", "User signed out successfully");
                    emitGodotSignal("_on_firebase_user_signedOut", signalParams);
                }
            }
        });
    }

    @UsedByGodot
    public void signinAnonymously(){
        AnonymousSignin.getInstance().signIn();
    }

    @UsedByGodot
    public void signinGoogle(){
        GoogleSignin.getInstance().signin();
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
        EmailSignin.getInstance().signin(email, password);
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
    public void firestoreWriteUserData(String collName, String jsonString){
        Firestore.getInstance().WriteUserData(collName, jsonString);
    }

    @UsedByGodot
    public void firestoreUpdateUserData(String collName, String jsonString){
        Firestore.getInstance().UpdateUserData(collName, jsonString);
    }

    @UsedByGodot
    public void firestoreReadUserData(String collName){
        Firestore.getInstance().ReadUserData(collName);
        getActivity();
    }

    @UsedByGodot
    public void firestoreDeleteUserData(String collName){
        Firestore.getInstance().DeleteUserData(collName);
    }

    @UsedByGodot
    public void logEvent(String eventName, Dictionary params){
        Analytics.getInstance().logEvent(eventName, params);
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
        return getActivity().getString(R.string.app_name);
    }

     @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("_on_godotonfire_initiated", Dictionary.class));

        signals.add(new SignalInfo("_on_got_firebase_user", Dictionary.class));
        signals.add(new SignalInfo("_on_firebase_user_signedIn", Dictionary.class));
        signals.add(new SignalInfo("_on_firebase_user_signedOut", Dictionary.class));
        signals.add(new SignalInfo("_on_signin_anonymously_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_google_signin_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_link_account_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signup_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signin_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_send_email_verification_completed", Dictionary.class));

        signals.add(new SignalInfo("_database_set_completed", Dictionary.class));
        signals.add(new SignalInfo("_database_push_completed", Dictionary.class));
        signals.add(new SignalInfo("_database_update_completed", Dictionary.class));
        signals.add(new SignalInfo("_database_get_completed", Dictionary.class));
        signals.add(new SignalInfo("_database_remove_completed", Dictionary.class));

        signals.add(new SignalInfo("_on_firestore_write_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_update_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_read_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_delete_completed", Dictionary.class));

        signals.add(new SignalInfo("_remote_config_fetch_completed", Dictionary.class));
        signals.add(new SignalInfo("_remote_config_activate_completed", Dictionary.class));
        signals.add(new SignalInfo("_remote_config_get_value_completed", Dictionary.class));
        return signals;
    }
}