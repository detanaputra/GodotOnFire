package id.maingames.godotonfire;

import android.content.Intent;

import androidx.collection.ArraySet;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import id.maingames.godotonfire.analytics.Analytics;
import id.maingames.godotonfire.authentications.AnonymousSignin;
import id.maingames.godotonfire.authentications.EmailSignin;
import id.maingames.godotonfire.authentications.GodotFirebaseUser;
import id.maingames.godotonfire.authentications.GoogleSignin;
import id.maingames.godotonfire.databases.RealtimeDatabase;
import id.maingames.godotonfire.firestores.Firestore;

public class GodotOnFire extends GodotPlugin {

    public GodotOnFire(Godot godot) {
        super(godot);
    }

    @UsedByGodot
    public void init(){
        AnonymousSignin.init(this, getActivity());
        GoogleSignin.init(this, getActivity());
        EmailSignin.init(this, getActivity());
        listenAuthState();
        RealtimeDatabase.init(this, getActivity());
        Firestore.init(this, getActivity());
        Analytics.init(this, getActivity());
    }

    public void emitGodotSignal(String signalName, Object... signalArgs){
        emitSignal(signalName, signalArgs);
    }

    @UsedByGodot
    public void getFirebaseUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GodotFirebaseUser godotUser = new GodotFirebaseUser(user);
        emitGodotSignal("_on_got_firebase_user", godotUser.ToDictionary());
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
                if (user != null){
                    signalParams.put("status", 0);
                    signalParams.put("message", "User signed in successfully");
                    GodotFirebaseUser godotUser = new GodotFirebaseUser(user);
                    signalParams.put("data", godotUser.ToDictionary());
                    emitGodotSignal("_on_firebase_user_signedIn", signalParams);
                }
                else{
                    signalParams.put("status", 0);
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

    @UsedByGodot
    public void databaseWriteUserData(String collName, Dictionary data){
        RealtimeDatabase.getInstance().WriteUserData(collName, data);
    }

    @UsedByGodot
    public void databaseUpdateUserData(String collName, Dictionary data){
        RealtimeDatabase.getInstance().UpdateUserData(collName, data);
    }

    @UsedByGodot
    public void databaseReadUserData(String collName){
        RealtimeDatabase.getInstance().ReadUserData(collName);
    }

    @UsedByGodot
    public void databaseDeleteUserData(String collName){
        RealtimeDatabase.getInstance().DeleteUserData(collName);
    }

    @UsedByGodot
    public void firestoreWriteUserData(String collName, Dictionary data){
        Firestore.getInstance().WriteUserData(collName, data);
    }

    @UsedByGodot
    public void firestoreUpdateUserData(String collName, Dictionary data){
        Firestore.getInstance().UpdateUserData(collName, data);
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
    public List<String> getPluginMethods() {
        return Arrays.asList(
            "init", "signOut", "signinAnonymously", "signinGoogle", "linkAccountWithGoogle", "getFirebaseUser"
            , "signUpWithEmail", "signInWithEmail", "linkAccountWithEmail", "sendEmailVerification"
            , "databaseWriteUserData", "databaseUpdateUserData", "databaseReadUserData", "databaseDeleteUserData"
            , "firestoreWriteUserData", "firestoreUpdateUserData", "firestoreReadUserData", "firestoreDeleteUserData"
            , "logEvent", "testCrash"
        );
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("_on_got_firebase_user", Dictionary.class));
        signals.add(new SignalInfo("_on_firebase_user_signedIn", Dictionary.class));
        signals.add(new SignalInfo("_on_firebase_user_signedOut", Dictionary.class));
        signals.add(new SignalInfo("_on_signin_anonymously_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_google_signin_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_link_account_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signup_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signin_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_send_email_verification_completed", Dictionary.class));

        signals.add(new SignalInfo("_on_database_write_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_database_update_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_database_read_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_database_delete_completed", Dictionary.class));

        signals.add(new SignalInfo("_on_firestore_write_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_update_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_read_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_firestore_delete_completed", Dictionary.class));
        return signals;
    }
}