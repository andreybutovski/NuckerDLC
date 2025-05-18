//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import im.nucker.events.EventLivingUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;

@FunctionRegister(name = "CreeperFarm", type = Category.Misc)
public class CreeperFarm extends Function {
    private Entity targetCreeper = null;
    private long lastAttackTime = 0L;
    private boolean retreating = false;

    @Subscribe
    public void onUpdate(EventLivingUpdate event) {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            if (this.retreating) {
                if (!(this.getDistanceToEntity(this.targetCreeper) >= (double)6.0F)) {
                    this.moveAwayFromEntity(this.targetCreeper);
                    return;
                }

                this.retreating = false;
            }

            this.targetCreeper = this.findNearestCreeperOnSameY(player);
            if (this.targetCreeper != null) {
                this.lookAtEntity(this.targetCreeper);
                this.moveToEntity(this.targetCreeper);
                if ((double)player.getDistance(this.targetCreeper) <= (double)3.0F) {
                    this.performCriticalHit(this.targetCreeper);
                    this.lastAttackTime = System.currentTimeMillis();
                    this.retreating = true;
                }
            }

        }
    }

    private Entity findNearestCreeperOnSameY(ClientPlayerEntity player) {
        Entity nearest = null;
        double minDistance = Double.MAX_VALUE;
        double playerY = Math.floor(player.getPosY());

        for(Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof CreeperEntity) {
                double entityY = Math.floor(entity.getPosY());
                if (entityY == playerY) {
                    double distance = (double)player.getDistance(entity);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = entity;
                    }
                }
            }
        }

        return nearest;
    }

    private void lookAtEntity(Entity entity) {
        Vector3d entityPos = entity.getPositionVec();
        Vector3d playerPos = mc.player.getPositionVec();
        double diffX = entityPos.x - playerPos.x;
        double diffY = entityPos.y + (double)entity.getEyeHeight() - (playerPos.y + (double)mc.player.getEyeHeight());
        double diffZ = entityPos.z - playerPos.z;
        double distance = (double)MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(MathHelper.atan2(diffZ, diffX) * (180D / Math.PI)) - 90.0F;
        float pitch = (float)(-(MathHelper.atan2(diffY, distance) * (180D / Math.PI)));
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    private void moveToEntity(Entity entity) {
        if (entity != null) {
            double diffX = entity.getPosX() - mc.player.getPosX();
            double diffZ = entity.getPosZ() - mc.player.getPosZ();
            double distance = (double)MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
            if (distance > (double)0.5F) {
                double speed = Math.min(0.4, distance * 0.1);
                double motionX = diffX / distance * speed;
                double motionZ = diffZ / distance * speed;
                mc.player.moveRelative(0.1F, new Vector3d(motionX, (double)0.0F, motionZ));
                mc.player.setSprinting(distance > (double)3.0F);
            } else {
                mc.player.setSprinting(false);
            }

        }
    }

    private void moveAwayFromEntity(Entity entity) {
        if (entity != null) {
            double diffX = mc.player.getPosX() - entity.getPosX();
            double diffZ = mc.player.getPosZ() - entity.getPosZ();
            double distance = (double)MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
            if (distance < (double)6.0F) {
                double speed = (double)0.5F;
                double motionX = diffX / distance * speed;
                double motionZ = diffZ / distance * speed;
                mc.player.moveRelative(0.1F, new Vector3d(motionX, (double)0.0F, motionZ));
                mc.player.setSprinting(true);
            }

        }
    }

    private void performCriticalHit(Entity entity) {
        if (mc.player.isOnGround()) {
        }

        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(mc.player.getActiveHand());
    }

    private double getDistanceToEntity(Entity entity) {
        return entity == null ? Double.MAX_VALUE : (double)mc.player.getDistance(entity);
    }
}
