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
import id.maingames.godotonfire.R;
import id.maingames.godotonfire.utilities.JsonConverter;
import id.maingames.godotonfire.utilities.SignalParams;


/**
 * Signals:
 * _remote_config_fetch_completed
 * _remote_config_activate_completed
 * _remote_config_get_value_completed
 *
 */
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
        String signalName = godotActivity.getString(R.string.GOF_remote_config_fetch_completed);
        String className = getClass().getSimpleName() + " ";
        String method = "fetch ";
        SignalParams signalParams = new SignalParams();
        mfirebaseRemoteConfig.fetch().addOnSuccessListener(godotActivity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                signalParams.Status = 0;
                signalParams.Message = "remote config fetched successfully";
                Log.d(TAG, className + method + "has succeed");
                godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            }
        })
        .addOnFailureListener(godotActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signalParams.Status = 1;
                signalParams.Message = "remote config has failed";
                Log.e(TAG, className + method + "has failed. " + e.getLocalizedMessage(), e);
                godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
            }
        });

    }

    public void activate(){
        String signalName = godotActivity.getString(R.string.GOF_remote_config_activate_completed);
        String className = getClass().getSimpleName() + " ";
        String method = "activate ";
        SignalParams signalParams = new SignalParams();
        mfirebaseRemoteConfig.activate()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    signalParams.Status = 0;
                    signalParams.Message = "remote config activated successfully";
                    Log.d(TAG, className + method + "has succeed");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "remote config activate has failed";
                    Log.e(TAG, className + method + "has failed. " + e.getLocalizedMessage(), e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });
    }

    public void fetchAndActivate(){
        String signalName = godotActivity.getString(R.string.GOF_remote_config_activate_completed);
        String className = getClass().getSimpleName() + " ";
        String method = "fetchAndActivate ";
        SignalParams signalParams = new SignalParams();
        mfirebaseRemoteConfig.fetchAndActivate()
            .addOnSuccessListener(godotActivity, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    signalParams.Status = 0;
                    signalParams.Message = "remote config fetch and activate has succeed";
                    Log.d(TAG, className + method + "has succeed");
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            })
            .addOnFailureListener(godotActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signalParams.Status = 1;
                    signalParams.Message = "remote config fetch and activate has failed";
                    Log.e(TAG, className + method + "has failed. " + e.getLocalizedMessage(), e);
                    godotOnFire.emitGodotSignal(signalName, signalParams.toDictionary());
                }
            });

    }

    public void getLong(String key){
        try{
            Long value = mfirebaseRemoteConfig.getLong(key);
            wrapValueAndEmitSignal(key, value, 0, null);
        }
        catch (Exception e){
            wrapValueAndEmitSignal(key, null, 1, e);
        }
    }

    public void getBoolean(String key){
        try{
            boolean value = mfirebaseRemoteConfig.getBoolean(key);
            wrapValueAndEmitSignal(key, value, 0, null);
        }
        catch (Exception e){
            wrapValueAndEmitSignal(key, null, 1, e);
        }
    }

    public void getDouble(String key){
        try{
            double value = mfirebaseRemoteConfig.getDouble(key);
            wrapValueAndEmitSignal(key, value, 0, null);
        }
        catch (Exception e){
            wrapValueAndEmitSignal(key, null, 1, e);
        }
    }

    public void getString(String key){
        try{
            String value = mfirebaseRemoteConfig.getString(key);
            wrapValueAndEmitSignal(key, value, 0, null);
        }
        catch (Exception e){
            wrapValueAndEmitSignal(key, null, 1, e);
        }
    }

    private void wrapValueAndEmitSignal(String key, Object value, int status, Throwable tr){
        SignalParams signalParams = new SignalParams();
        signalParams.Status = status;
        if (status == 0){
            Log.d(TAG, signalParams.Message);
            signalParams.Message = "got value from remote config";
            Dictionary dict = new Dictionary();
            dict.put(key, value);
            String data = JsonConverter.dictToJson(dict);
            signalParams.Data = data;
        }
        else{
            signalParams.Message = String.format("Getting %s value from remote config has failed", key);
            Log.e(TAG, "Getting value from remote config has failed. " + tr.getLocalizedMessage(), tr);
        }
        godotOnFire.emitGodotSignal(godotActivity.getString(R.string.GOF_remote_config_get_value_completed), signalParams.toDictionary());
    }


}
