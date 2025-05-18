package im.nucker.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;

@FunctionRegister(name = "параша аура", type = Category.Combat)
public class AttackAura extends Function {
    private LivingEntity target = null;
    private long lastAttackTime = 0;
    private final float attackRange = 4.0f;
    private final long attackDelay = 500;


    public boolean onEnable() {
        super.onEnable();
        target = null;
        return false;
    }

    public void onDisable() {
        super.onDisable();
        target = null;
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        target = findClosestTarget();

        if (target != null) {
            if (canAttack() && isTargetInView(target)) {
                performSnapAttack(target);
            }
        }
    }

    @Subscribe
    public void onWalking(EventMotion event) {
        if (target == null) return;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;

        event.setYaw(yaw);
        event.setPitch(pitch);
        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;
    }

    private LivingEntity findClosestTarget() {
        LivingEntity closest = null;
        double closestDistance = attackRange;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isValidTarget(living)) {
                double distance = mc.player.getDistance(entity);
                if (distance < closestDistance) {
                    closest = living;
                    closestDistance = distance;
                }
            }
        }
        return closest;
    }

    private boolean isValidTarget(LivingEntity entity) {
        return entity.isAlive() &&
                entity != mc.player &&
                mc.player.getDistance(entity) <= attackRange &&
                !entity.isInvisible();
    }

    private boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= attackDelay &&
                mc.player.getCooledAttackStrength(0.5f) >= 1.0f;
    }

    private boolean isTargetInView(LivingEntity target) {
        Vector3d playerEyePos = mc.player.getEyePosition(1.0f);
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() / 2.0, 0);

        BlockRayTraceResult blockResult = mc.world.rayTraceBlocks(new RayTraceContext(
                playerEyePos, targetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player));

        return blockResult.getType() == RayTraceResult.Type.MISS;
    }

    private void calculateRotationToTarget(LivingEntity target) {
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() / 2.0, 0);
        Vector3d playerEyePos = mc.player.getEyePosition(1.0f);

        Vector3d difference = targetPos.subtract(playerEyePos);
        double distance = Math.sqrt(difference.x * difference.x + difference.z * difference.z);

        float yaw = (float) (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.y, distance)));

        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;
    }

    private void performSnapAttack(LivingEntity target) {
        float originalYaw = mc.player.rotationYaw;
        float originalPitch = mc.player.rotationPitch;
        calculateRotationToTarget(target);
        mc.playerController.attackEntity(mc.player, target);
        mc.player.swingArm(Hand.MAIN_HAND);
        mc.player.rotationYaw = originalYaw;
        mc.player.rotationPitch = originalPitch;
        lastAttackTime = System.currentTimeMillis();
    }
}