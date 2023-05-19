package id.maingames.godotonfire.extensions;

import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.godotengine.godot.Dictionary;


public class FirebaseUserExtension {

    public static Dictionary ToDictionary(@This FirebaseUser user){
        int status = 1;
        String uid = "";
        String displayName = "";
        String email = "";
        String providerId = "";

        if (user != null){
            status = 0;
            uid = user.getUid();
            displayName = user.getDisplayName();
            email = user.getEmail();
            providerId = user.getProviderId();
        }

        Dictionary dictionary = new Dictionary();
        dictionary.put("status", status);
        dictionary.put("uid", uid);
        dictionary.put("displayName", displayName);
        dictionary.put("email", email);
        dictionary.put("providerId", providerId);

        return dictionary;
    }
}