package im.nucker.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventPacket;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.SliderSetting;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;

@FunctionRegister(name = "SuperBow", type = Category.Combat)
public class SuperBow extends Function {

    private final SliderSetting power = new SliderSetting("Сила", 30, 1, 100, 1);

    public SuperBow() {
        addSettings(power);
    }

    @Subscribe
    public void onEvent(EventPacket event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof CPlayerDiggingPacket p) {
            if (p.getAction() == CPlayerDiggingPacket.Action.RELEASE_USE_ITEM) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                for (int i = 0; i < power.get().intValue(); i++) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 0.000000001, mc.player.getPosZ(), true));
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.000000001, mc.player.getPosZ(), false));
                }
            }
        }
    }
}
