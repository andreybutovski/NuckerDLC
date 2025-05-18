package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;

import im.nucker.events.EventPacket;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;
import lombok.Getter;
import net.minecraft.network.play.server.SUpdateTimePacket;

@Getter
@FunctionRegister(name = "WorldTime", type = Category.Render)
public class WorldTime extends Function {

    public ModeSetting time = new ModeSetting("Время", "Ночь", "День", "Ночь", "Вечер", "Утро");

    public WorldTime() {
        addSettings(time);
    }



    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SUpdateTimePacket p) {
            if (time.get().equalsIgnoreCase("День"))
                p.worldTime = 1000L;
            if (time.get().equalsIgnoreCase("Ночь"))
                p.worldTime = 16000L;
            if (time.get().equalsIgnoreCase("Вечер"))
                p.worldTime = 13000L;
            if (time.get().equalsIgnoreCase("Утро"))
                p.worldTime = 23000L;
        }
    }
}
