package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.command.friends.FriendStorage;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.StringSetting;
import im.nucker.utils.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

@FunctionRegister(name = "NameProtect", type = Category.Misc)
public class NameProtect extends Function {

    private final StringSetting name = new StringSetting(
            "Заменяемое Имя",
            "NuckerDLC",
            "Укажите текст для замены вашего игрового ника"
    );
    private final BooleanSetting friends = new BooleanSetting("Друзья", true);

    public NameProtect() {
        addSettings(name, friends);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
    }

    public static String getReplaced(String input) {
        NuckerDLC NuckerDLCInstance = NuckerDLC.getInstance();
        if (NuckerDLCInstance != null && NuckerDLCInstance.getFunctionRegistry().getNameProtect().isState()) {
            return input.replace(Minecraft.getInstance().session.getUsername(), NuckerDLCInstance.getFunctionRegistry().getNameProtect().name.get());
        }
        return input;
    }

    public ITextComponent patchFriendTextComponent(ITextComponent text, String name) {
        if (this.friends.get() && NuckerDLC.getInstance().getFunctionRegistry().getNameProtect().isState()) {
            if (FriendStorage.isFriend(name)) { // Проверяем, есть ли имя в хранилище друзей
                return ClientUtil.replace(text, name, this.name.getDescription());
            }
        }
        return text;
    }
}