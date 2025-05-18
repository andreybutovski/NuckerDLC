package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import java.util.Iterator;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.entity.Entity;

@FunctionRegister(
        name = "GlowESP",
        type = Category.Render
)
public class GlowEsp extends Function {
    public GlowEsp() {
    }

    @Subscribe
    public void onUpdate(EventUpdate var1) {
        Iterator var2 = mc.world.getPlayers().iterator();

        while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            if (var3 != null) {
                var3.setGlowing(true);
            }
        }

    }

    public boolean onEnable() {
        super.onEnable();
        Iterator var1 = mc.world.getPlayers().iterator();

        while(var1.hasNext()) {
            Entity var2 = (Entity)var1.next();
            if (var2 != null) {
                var2.setGlowing(true);
            }
        }
        return false;
    }

    public void onDisable() {
        super.onDisable();
        Iterator var1 = mc.world.getPlayers().iterator();

        while(var1.hasNext()) {
            Entity var2 = (Entity)var1.next();
            if (var2 != null) {
                var2.setGlowing(false);
            }
        }

    }
}