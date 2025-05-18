package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import im.nucker.functions.api.FunctionRegistry;
import net.minecraft.item.Items;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;

@FunctionRegister(name = "Fly", type = Category.Player)
public class Fly extends Function {
    private final ModeSetting mod = new ModeSetting("Mode", "HolyTime", "HolyTime");
    private final Minecraft mc = Minecraft.getInstance();

    public Fly() {
        addSettings(mod);

    }
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.mod.is("HolyTime")) {
            float SPEED = 0.055F;
            Minecraft.getInstance();
            PlayerEntity player = mc.player;
            if (player != null && player.isAlive()) {
                player.setMotion(player.getMotion().x, player.getMotion().y + 0.05499999761581421D, player.getMotion().z);
            }
        }

    }
}