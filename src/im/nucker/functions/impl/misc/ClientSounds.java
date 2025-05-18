package im.nucker.functions.impl.misc;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "ClientSounds", type = Category.Misc)
public class ClientSounds extends Function {

    public ModeSetting mode = new ModeSetting("Тип", "Тип 1", "Тип 1", "Тип 2", "Тип 3", "Тип 4","Тип 5");
    public SliderSetting volume = new SliderSetting("Громкость", 85.0f, 0.0f, 120.0f, 1.0f);
    public BooleanSetting other = new BooleanSetting("Гуи", true);

    public ClientSounds() {
        addSettings(mode, volume);
    }


    public String getFileName(boolean state) {
        switch (mode.get()) {
            case "Тип 1" -> {
                return state ? "enable" : "disable".toString();
            }
            case "Тип 2" -> {
                return state ? "enable1" : "disable1";
            }
            case "Тип 3" -> {
                return state ? "enable2" : "disable2";
            }
            case "Тип 4" -> {
                return state ? "enable3" : "disable3";
            }
            case "Тип 5" -> {
                return state ? "enable4" : "disable4";
            }
        }
        return "";
    }
}