package im.nucker.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.rotation.RotationUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

@FunctionRegister(name = "SexAura", type = Category.Combat)
public class SexAura extends Function {

    boolean isActionEnabled;
    @Subscribe
    public void onUpdate(EventUpdate event) {
        handleUpdate();
    }
    @Subscribe
    private void onMotion(EventMotion e) {
        handleEventMotion(e);
    }

    private void handleEventMotion(EventMotion event) {
        LivingEntity target = getClosestEntity(25.0F);
        if (target != null) {
            float[] rots = RotationUtils.getMatrixRots(target);
            event.setYaw(rots[0]);
            event.setPitch(rots[1]);

            mc.player.rotationYaw = (rots[0]);
        }
    }

    public void handleUpdate() {
        LivingEntity target = getClosestEntity(25.0F);
        if (target != null) {
            mc.gameSettings.keyBindJump.setPressed((target.getPosY() > mc.player.getPosY()) || (mc.player.collidedHorizontally && target.getPosY() == mc.player.getPosY()));
            boolean isCloseEnough = this.moveTowardsTargetIfNecessary(this.getTargetPositionOffset(target), MathHelper.clamp(target.getWidth() / 2.0F, 0.1F, 2.0F), target);
            this.performActionIfRequired(isCloseEnough);
        }
    }

    private Vector3d getTargetPositionOffset(LivingEntity target) {
        return target.getPositionVec().add(Math.sin(Math.toRadians(target.renderYawOffset)) * (double) (float) 0.3, 0.0, -Math.cos(Math.toRadians(target.renderYawOffset)) * (double) (float) 0.3);
    }

    private boolean moveTowardsTargetIfNecessary(Vector3d vec, float checkR, LivingEntity target) {
        boolean isCloseEnough = mc.player.getDistanceSq(vec) <= (double) checkR;

        if (!isCloseEnough) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getDefault(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getDefault(), false);
        }

        return isCloseEnough;
    }

    private void performActionIfRequired(boolean DO) {
        if (DO) {
            mc.gameSettings.keyBindSneak.pressed = mc.player.ticksExisted % 2 == 0;
            if (!this.isActionEnabled) {
                this.isActionEnabled = true;
            }
        } else if (this.isActionEnabled) {
            mc.gameSettings.keyBindSneak.pressed = false;
            this.isActionEnabled = false;
        }
    }

    public LivingEntity getClosestEntity(double range) {
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof ArmorStandEntity) && entity != mc.player && mc.player.getDistance(entity) <= range) {
                targets.add((LivingEntity) entity);
            }
        }

        LivingEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity target : targets) {
            double distance = mc.player.getDistance(target);
            if (distance < closestDistance) {
                closestEntity = target;
                closestDistance = distance;
            }
        }

        return closestEntity;
    }

    @Override
    public boolean onEnable() {
        this.performActionIfRequired(false);
        super.onEnable();
        return false;
    }

}
