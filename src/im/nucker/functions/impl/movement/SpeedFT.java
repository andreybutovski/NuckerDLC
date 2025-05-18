package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventMotion;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

@FunctionRegister(name="SpeedFT", type= Category.Movement)
public class SpeedFT extends Function {

    @Subscribe
    private void handleFunTimeMode(EventMotion event) {
        if (mc.player == null || mc.world == null) return;

        // Проверка, чтобы скорость не накапливалась слишком сильно
        if (mc.player.isOnGround()) {
            mc.player.setMotion(mc.player.getMotion().x * 1.12, mc.player.getMotion().y, mc.player.getMotion().z * 1.12);
        }
    }
}
