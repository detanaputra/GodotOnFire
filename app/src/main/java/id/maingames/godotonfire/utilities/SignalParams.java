package id.maingames.godotonfire.utilities;

import org.godotengine.godot.Dictionary;

public class SignalParams {
    public int Status = 1;
    public String Message = "";
    public String Data = "";

    public SignalParams(int status, String message, String data) {
        Status = status;
        Message = message;
        Data = data;
    }

    public SignalParams() {
    }

    public Dictionary toDictionary(){
        Dictionary dict = new Dictionary();
        dict.put("status", Status);
        dict.put("message", Message);
        dict.put("data", Data);
        return dict;
    }
}
