package im.nucker.functions.impl.misc;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeListSetting;
import lombok.Getter;

@Getter
@FunctionRegister(name = "NoPush", type = Category.Player)
public class NoPush extends Function {

    private final ModeListSetting modes = new ModeListSetting("Тип",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Вода", false),
            new BooleanSetting("Блоки", true));

    public NoPush() {
        addSettings(modes);
    }

}
