package id.maingames.godotonfire.remoteConfigs;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import org.godotengine.godot.Dictionary;

import java.util.Map;

import id.maingames.godotonfire.GodotOnFire;
import id.maingames.godotonfire.utilities.JsonConverter;

public class RemoteConfig {
    private static String TAG = "";
    private static RemoteConfig instance;
    private GodotOnFire godotOnFire;
    private Activity godotActivity;
    private FirebaseRemoteConfig mfirebaseRemoteConfig;
    private FirebaseRemoteConfigSettings settings;

    public RemoteConfig(){}

    public static void init(GodotOnFire _godotOnFire, Activity _godotActivity){
        instance = new RemoteConfig();
        TAG = _godotOnFire.getPluginName();
        instance.godotOnFire = _godotOnFire;
        instance.godotActivity = _godotActivity;
        instance.mfirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        instance.settings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build();
        instance.mfirebaseRemoteConfig.setConfigSettingsAsync(instance.settings);
    }

    public static RemoteConfig getInstance(){
        if(instance == null){
            Log.w(TAG, "Remote Config instance is null, call Init(godot) first before calling getInstance()"
                    , new NullPointerException("Remote Config Activity is null"));
        }
        return instance;
    }

    public void fetch(){
        Dictionary signalParams = new Dictionary();
        mfirebaseRemoteConfig.fetch().addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                signalParams.put("status", 0);
                signalParams.put("message", "remote config fetched successfully");
                Log.d(TAG, "remote config fetched successfully");
                godotOnFire.emitGodotSignal("_on_remote_config_fetched", signalParams);
            }
        })
        .addOnFailureListener(godotActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signalParams.put("status", 1);
                signalParams.put("message", "remote config fetch failed");
                Log.e(TAG, "remote config fetch failed " + e);
                godotOnFire.emitGodotSignal("_on_remote_config_fetched", signalParams);
            }
        });

    }

    public void activate(){
        Dictionary signalParams = new Dictionary();
        mfirebaseRemoteConfig.activate()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "remote config activated successfully");
                    Log.d(TAG, "remote config activated successfully");
                    godotOnFire.emitGodotSignal("_on_remote_config_activated", signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "remote config activate failed");
                    Log.e(TAG, "remote config activate has failed " + e);
                    godotOnFire.emitGodotSignal("_on_remote_config_activated", signalParams);
                }
            });
    }

    public void fetchAndActivate(){
        Dictionary signalParams = new Dictionary();
        mfirebaseRemoteConfig.fetchAndActivate()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    signalParams.put("status", 0);
                    signalParams.put("message", "remote config activated successfully");
                    Log.d(TAG, "remote config fetched and activated successfully");
                    godotOnFire.emitGodotSignal("_on_remote_config_activated", signalParams);
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.put("status", 1);
                    signalParams.put("message", "remote config activate failed");
                    Log.e(TAG, "remote config fetch and activate has failed " + e);
                    godotOnFire.emitGodotSignal("_on_remote_config_activated", signalParams);
                }
            });

    }

    public void getLong(String key){
        Long value = mfirebaseRemoteConfig.getLong(key);
        wrapValueAndEmitSignal(key, value);
    }

    public void getBoolean(String key){
        boolean value = mfirebaseRemoteConfig.getBoolean(key);
        wrapValueAndEmitSignal(key, value);
    }

    public void getDouble(String key){
        double value = mfirebaseRemoteConfig.getDouble(key);
        wrapValueAndEmitSignal(key, value);
    }

    public void getString(String key){
        String value = mfirebaseRemoteConfig.getString(key);
        wrapValueAndEmitSignal(key, value);
    }

    private void wrapValueAndEmitSignal(String key, Object value){
        Dictionary signalParams = new Dictionary();
        signalParams.put("status", 0);
        signalParams.put("message", "got value from remote config");
        Dictionary dict = new Dictionary();
        dict.put(key, value);
        String data = JsonConverter.dictToJson(dict);
        signalParams.put("data", data);
        godotOnFire.emitGodotSignal("_on_remote_config_got_value", signalParams);
    }


}
