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

public class GodotOnFire extends GodotPlugin {
    private static Godot _godot;

    public GodotOnFire(Godot godot) {
        super(godot);
        _godot = godot;
    }

    public void init(){
        AnonymousSigninActivity.init(_godot);
        GoogleSigninActivity.init(_godot);
        EmailSigninActivity.init(_godot);
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

    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
            "init", "signinAnonymously", "signinGoogle", "linkAccountWithGoogle",
            "signUpWithEmail", "signInWithEmail", "linkAccountWithEmail", "sendEmailVerification"
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