package im.nucker.utils.rotation;

import im.nucker.utils.client.IMinecraft;
import im.nucker.utils.math.VectorUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

@UtilityClass
public class RotationUtils implements IMinecraft {
    public static float[] getMatrixRots(LivingEntity target) {
        return null;
    }

    public Vector3d getClosestVec(Entity entity) {
        Vector3d eyePosVec = mc.player.getEyePosition(1.0F);

        return VectorUtils.getClosestVec(eyePosVec, entity).subtract(eyePosVec);
    }

    public double getStrictDistance(Entity entity) {
        return getClosestVec(entity).length();
    }

}
