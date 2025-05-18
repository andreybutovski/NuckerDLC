package im.nucker.functions.impl.movement;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;

@FunctionRegister(name = "AutoSprint", type = Category.Movement)
public class AutoSprint extends Function {
    public BooleanSetting saveSprint = new BooleanSetting("Сохранять спринт", false);

    public AutoSprint() {
        addSettings(saveSprint);
    }


}
