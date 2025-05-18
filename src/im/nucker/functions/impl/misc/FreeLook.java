package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.impl.combat.KillAura;
import im.nucker.functions.settings.impl.BooleanSetting;
import net.minecraft.client.settings.PointOfView;

@FunctionRegister(name = "FreeLook", type = Category.Misc)
public class FreeLook extends Function {

    public BooleanSetting free = new BooleanSetting("Свободная камера", true);
    public FreeLook(){
    }

    private float startYaw, startPitch;
    @Override
    public boolean onEnable(){
        if(isFree()) {
            startYaw = mc.player.rotationYaw;
            startPitch = mc.player.rotationPitch;
        }
        super.onEnable();
        return false;
    }

    @Override
    public void onDisable(){
        if(isFree()) {
            mc.player.rotationYawOffset = Integer.MIN_VALUE;
            mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
            mc.player.rotationYaw = startYaw;
            mc.player.rotationPitch = startPitch;
        }
        super.onDisable();
    }


    @Subscribe
    public void onUpdate(EventUpdate e) {
        KillAura aura = NuckerDLC.getInstance().getFunctionRegistry().getKillAura();
        if (free.get()) {
            if (! aura.isState() && aura.getTarget() == null) {
                mc.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
                mc.player.rotationYawOffset = startYaw;
            } else {
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e){
        if(free.get()) {
            e.setYaw(startYaw);
            e.setPitch(startPitch);
            e.setOnGround(mc.player.isOnGround());
            mc.player.rotationYawHead = mc.player.rotationYawOffset;
            mc.player.renderYawOffset = mc.player.rotationYawOffset;
            mc.player.rotationPitchHead = startPitch;
        }
    }

    public boolean isFree(){
        return free.get();
    }
}