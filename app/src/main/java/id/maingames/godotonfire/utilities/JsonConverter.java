package id.maingames.godotonfire.utilities;

import android.util.Log;

import com.google.gson.Gson;

import org.godotengine.godot.Dictionary;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class JsonConverter {
    private static String TAG = "GodotOnFire";
    public static HashMap<String, Object> jsonToMap(String t) throws JSONException {
        try{
            HashMap<String, Object> map = new Gson().fromJson(t, HashMap.class);
            return map;
        }catch (Exception e){
            Log.w(TAG, "Json To Map:failed. " + e);
            return null;
        }
    }

    public static Dictionary jsonToDictionary(String t){
        try{
            Dictionary dict = new Gson().fromJson(t, Dictionary.class);
            return dict;
        }catch (Exception e){
            Log.w(TAG, "Json To Dictionary:failed. " + e);
            return null;
        }
    }

    public static String mapToJson(Map map){
        try{
            return new Gson().toJson(map);
        }catch (Exception e){
            Log.w(TAG, "Map to Json:failed. " + e);
            return null;
        }
    }

    public static String dictToJson(Dictionary dict){
        try{
            return new Gson().toJson(dict);
        }catch (Exception e){
            Log.w(TAG, "Dictionary to Json:failed. " + e);
            return null;
        }
    }

    public static String toJson(Object obj){
        String jsonStr = new Gson().toJson(obj);
        return jsonStr;
    }
}