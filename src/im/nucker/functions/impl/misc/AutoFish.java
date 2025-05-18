package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.events.EventPacket;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.math.StopWatch;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;

@FunctionRegister(name = "AutoFish", type = Category.Player)
public class AutoFish extends Function {
    private final StopWatch delay = new StopWatch();
    private boolean isHooked = false;
    private boolean needToHook = false;
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (delay.isReached(600) && isHooked) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            isHooked = false;
            needToHook = true;
            delay.reset();
        }
        if (delay.isReached(300) && needToHook) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            needToHook = false;
            delay.reset();
        }
    }
    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SPlaySoundEffectPacket p) {
            if (p.getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
                double x = p.getX();
                double y = p.getY();
                double z = p.getZ();
                FishingBobberEntity bobber = mc.player.fishingBobber;
                if (bobber != null) {
                    double distance = Math.sqrt(Math.pow(x - bobber.getPosX(), 2) + Math.pow(y - bobber.getPosY(), 2) + Math.pow(z - bobber.getPosZ(), 2));
                    if (distance <= 0.5) {
                        isHooked = true;
                        delay.reset();
                    }
                }
            }
        }
    }
}