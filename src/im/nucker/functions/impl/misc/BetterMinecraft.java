package im.nucker.functions.impl.misc;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;

@FunctionRegister(name = "BetterMinecraft", type = Category.Misc)
public class BetterMinecraft extends Function {

    public final BooleanSetting smoothCamera = new BooleanSetting("Плавная камера", true);
    //public final BooleanSetting smoothTab = new BooleanSetting("Плавный таб", true); // пот
    public final BooleanSetting betterTab = new BooleanSetting("Улучшенный таб", true);

    public BetterMinecraft() {
        addSettings(smoothCamera, betterTab);
    }
}
