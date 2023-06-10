package id.maingames.godotonfire.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.godotengine.godot.Dictionary;

import java.util.HashMap;
import java.util.Map;

import id.maingames.godotonfire.GodotOnFire;

public class Analytics{
    private static String TAG = "";
    private static Analytics instance;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    private FirebaseAnalytics mFirebaseAnalytics;

    public Analytics(){}

    public static Analytics getInstance(){
        if(instance == null){
            Log.w(TAG, "Firebase analytics instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("Firebase analytics is null"));
        }
        return instance;
    }

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new Analytics();
        TAG = _godotOnFire.getPluginName();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mFirebaseAnalytics = FirebaseAnalytics.getInstance(_godotActivity);
    }

    public void logEvent(String eventName, Dictionary params){
        Bundle bundle = new Bundle();
        for (HashMap.Entry<String, Object> entry : params.entrySet()
             ) {
            try {
                if(entry.getValue() instanceof Double){
                    bundle.putDouble(entry.getKey(), (Double)entry.getValue());
                }
                else if(entry.getValue() instanceof Boolean){
                    bundle.putBoolean(entry.getKey(), (Boolean)entry.getValue());
                }
                else{
                    bundle.putString(entry.getKey(), (String)entry.getValue());
                }
                mFirebaseAnalytics.logEvent(eventName, bundle);
                Log.d(TAG, eventName + " logged");
            } catch (Exception e){
                Log.w(TAG, "Error on marshaling Godot Dictionary to Bundle. For now, we only " +
                        "support Double and String value dictionary. Code: " + e.getLocalizedMessage(), e);
                Log.e(TAG, eventName + " logging failed");
            }
        }
    }

    public void setUserProperty(String propertyName, String value){
        mFirebaseAnalytics.setUserProperty(propertyName, value);
        Log.d(TAG, propertyName + " set");
    }
}
