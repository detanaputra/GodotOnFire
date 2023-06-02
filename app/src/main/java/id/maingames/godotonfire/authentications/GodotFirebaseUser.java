package id.maingames.godotonfire.authentications;

import com.google.firebase.auth.FirebaseUser;

import org.godotengine.godot.Dictionary;

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

    public Dictionary ToDictionary(){
        Dictionary dictionary = new Dictionary();
        dictionary.put("status", status);
        dictionary.put("uid", uid);
        dictionary.put("displayName", displayName);
        dictionary.put("email", email);
        dictionary.put("providerId", providerId);

        return dictionary;
    }
}
