package id.maingames.godotonfire.authentications;

import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Dictionary;

import id.maingames.godotonfire.utilities.JsonConverter;

public class GodotFirebaseUser {
    private int status = 1;
    private String uid = "";
    private String displayName = "";
    private String email = "";
    private String providerId = "";
    
    public GodotFirebaseUser(FirebaseUser user){
        if (user != null){
            status = 0;
            uid = user.getUid();
            displayName = user.getDisplayName();
            email = user.getEmail();
            providerId = user.getProviderId();
        }
    }

    public Dictionary toDictionary(){
        Dictionary dictionary = new Dictionary();
        dictionary.put("status", status);
        dictionary.put("uid", uid);
        dictionary.put("displayName", displayName);
        dictionary.put("email", email);
        dictionary.put("providerId", providerId);

        return dictionary;
    }

    public String toJson(){
        return JsonConverter.toJson(this);
    }
}
