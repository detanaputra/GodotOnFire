package id.maingames.godotonfire;

import androidx.collection.ArraySet;

import androidx.annotation.NonNull;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import id.maingames.godotonfire.authentications.AnonymousSigninActivity;
import id.maingames.godotonfire.authentications.EmailSigninActivity;
import id.maingames.godotonfire.authentications.GoogleSigninActivity;
import id.maingames.godotonfire.databases.RealtimeDatabaseActivity;
import id.maingames.godotonfire.firestores.FirestoreActivity;

public class GodotOnFire extends GodotPlugin {

    public GodotOnFire(Godot godot) {
        super(godot);
        //init(godot);
    }

    public void init(){
        Godot godot = getGodot();
        AnonymousSigninActivity.init(godot);
        GoogleSigninActivity.init(godot);
        EmailSigninActivity.init(godot);
        RealtimeDatabaseActivity.init(godot);
        FirestoreActivity.init(godot);
    }

    public void signinAnonymously(){
        AnonymousSigninActivity.getInstance().signIn();
    }

    public void signinGoogle(){
        GoogleSigninActivity.getInstance().signin();
    }

    public void linkAccountWithGoogle(){
        GoogleSigninActivity.getInstance().linkAccount();
    }

    public void signUpWithEmail(String email, String password){
        EmailSigninActivity.getInstance().signUp(email, password);
    }

    public void signInWithEmail(String email, String password){
        EmailSigninActivity.getInstance().signin(email, password);
    }

    public void linkAccountWithEmail(String email, String password){
        EmailSigninActivity.getInstance().linkAccount(email, password);
    }

    public void sendEmailVerification(){
        EmailSigninActivity.getInstance().sendEmailVerification();
    }

    public void databaseWriteUserData(String collName, Dictionary data){
        RealtimeDatabaseActivity.getInstance().WriteUserData(collName, data);
    }

    public void databaseUpdateUserData(String collName, Dictionary data){
        RealtimeDatabaseActivity.getInstance().UpdateUserData(collName, data);
    }

    public void databaseReadUserData(String collName){
        RealtimeDatabaseActivity.getInstance().ReadUserData(collName);
    }

    public void databaseDeleteUserData(String collName){
        RealtimeDatabaseActivity.getInstance().DeleteUserData(collName);
    }

    public void firestoreWriteUserData(String collName, Dictionary data){
        FirestoreActivity.getInstance().WriteUserData(collName, data);
    }

    public void firestoreUpdateUserData(String collName, Dictionary data){
        FirestoreActivity.getInstance().UpdateUserData(collName, data);
    }

    public void firestoreReadUserData(String collName){
        FirestoreActivity.getInstance().ReadUserData(collName);
    }

    public void firestoreDeleteUserData(String collName){
        FirestoreActivity.getInstance().DeleteUserData(collName);
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
            "init", "signinAnonymously", "signinGoogle", "linkAccountWithGoogle"
            , "signUpWithEmail", "signInWithEmail", "linkAccountWithEmail", "sendEmailVerification"
            , "databaseWriteUserData", "databaseUpdateUserData", "databaseReadUserData", "databaseDeleteUserData"
            , "firestoreWriteUserData", "firestoreUpdateUserData", "firestoreReadUserData", "firestoreDeleteUserData"
        );
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("_on_signin_anonymously_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_google_signin_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_link_account_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signup_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_signin_email_completed", Dictionary.class));
        signals.add(new SignalInfo("_on_send_email_verification_completed", Integer.class));

        signals.add(new SignalInfo("_on_database_write_completed", Boolean.class));
        signals.add(new SignalInfo("_on_database_update_completed", Boolean.class));
        signals.add(new SignalInfo("_on_database_read_completed", Object.class));
        signals.add(new SignalInfo("_on_database_delete_completed", Boolean.class));

        signals.add(new SignalInfo("_on_firestore_write_completed", Boolean.class));
        signals.add(new SignalInfo("_on_firestore_update_completed", Boolean.class));
        signals.add(new SignalInfo("_on_firestore_read_completed", Object.class));
        signals.add(new SignalInfo("_on_firestore_delete_completed", Boolean.class));

        //signals.add(new SignalInfo("disconnected"));
        //signals.add(new SignalInfo("billing_resume"));
        //signals.add(new SignalInfo("connect_error", Integer.class, String.class));
        //signals.add(new SignalInfo("purchases_updated", Object[].class));
        //signals.add(new SignalInfo("query_purchases_response", Object.class));
        //signals.add(new SignalInfo("purchase_error", Integer.class, String.class));
        //signals.add(new SignalInfo("sku_details_query_completed", Object[].class));
        //signals.add(new SignalInfo("sku_details_query_error", Integer.class, String.class, String[].class));
        //signals.add(new SignalInfo("price_change_acknowledged", Integer.class));
        //signals.add(new SignalInfo("purchase_acknowledged", String.class));
        //signals.add(new SignalInfo("purchase_acknowledgement_error", Integer.class, String.class, String.class));
        //signals.add(new SignalInfo("purchase_consumed", String.class));
        //signals.add(new SignalInfo("purchase_consumption_error", Integer.class, String.class, String.class));

        return signals;
    }
}