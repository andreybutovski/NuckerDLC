package im.nucker.utils.math;

import im.nucker.utils.client.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import static im.nucker.utils.render.DisplayUtils.reAlphaInt;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@UtilityClass
public class MathUtil implements IMinecraft {

    public double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    // is hovered
    public boolean isHovered(float mouseX, float mouseY, float x,float y,float width,float height) {

        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }


    public static double randomWithUpdate(double min, double max, long ms, StopWatch stopWatch) {
        double randomValue = 0;

        if (stopWatch.isReached(ms)) {
            randomValue = random((float) min, (float) max);
            stopWatch.reset();
        }

        return randomValue;
    }


    public Vector2f rotationToVec(Vector3d vec) {
        Vector3d eyesPos = mc.player.getEyePosition(1.0f);
        double diffX = vec != null ? vec.x - eyesPos.x : 0;
        double diffY = vec != null ? vec.y - (mc.player.getPosY() + (double) mc.player.getEyeHeight() + 0.5) : 0;
        double diffZ = vec != null ? vec.z - eyesPos.z : 0;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        yaw = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
        pitch = mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch);
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);

        return new Vector2f(yaw, pitch);
    }

    public static Vector2f rotationToEntity(Entity target) {
        Vector3d vector3d = target.getPositionVec().subtract(Minecraft.getInstance().player.getPositionVec());
        double magnitude = Math.hypot(vector3d.x, vector3d.z);
        return new Vector2f(
                (float) Math.toDegrees(Math.atan2(vector3d.z, vector3d.x)) - 90.0F,
                (float) (-Math.toDegrees(Math.atan2(vector3d.y, magnitude))));
    }

    /**
     * @return Scale который будет возвращать всегда статичный размер который не будет увеличиваться больше указанного в size создавая ощущения будто это в 3д.
     *
     *  Нужно для 2д рендера что бы из за проекции оно на весь экран не становилось при отдалении
     *
     *  Можно применить для партиклов, таргет есп, неймтегов и много чего ещё
     */
    public double getScale(Vector3d position, double size){
        Vector3d cam = mc.getRenderManager().info.getProjectedView();
        double distance = cam.distanceTo(position);
        double fov = mc.gameRenderer.getFOVModifier(mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        return (float) Math.max(10f, 1000 / distance) * (size / 30f) / (float) (fov == 70 ? 1 : fov / 70.0f);
    }


    public static int calculateHuyDegrees(int divisor, int offset) {
        long currentTime = System.currentTimeMillis();
        long calculatedValue = (currentTime / divisor + offset) % 360L;
        return (int) calculatedValue;
    }

    public Vector2f rotationToVec(Vector2f rotationVector, Vector3d target) {
        double x = target.x - mc.player.getPosX();
        double y = target.y - mc.player.getEyePosition(1).y;
        double z = target.z - mc.player.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yaw - rotationVector.x);
        float pitchDelta = (pitch - rotationVector.y);

        if (abs(yawDelta) > 180)
            yawDelta -= signum(yawDelta) * 360;

        return new Vector2f(yawDelta, pitchDelta);
    }

    public static int astolfo(int speed, int index, float saturation, float brightness, float alpha) {
        float hueStep = 360.0f / 4.0f;
        float basaHuy = (float) calculateHuyDegrees(speed, index);
        float huy = (basaHuy + index * hueStep) % 360.0f;

        huy = huy / 360.0f;

        saturation = MathHelper.clamp(saturation, 0.0f, 1.0f);
        brightness = MathHelper.clamp(brightness, 0.0f, 1.0f);

        int rgb = Color.HSBtoRGB(huy, saturation, brightness);
        int Ialpha = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));

        return reAlphaInt(rgb, Ialpha);
    }

    // round
    public double round(double num, double increment) {
        double v = (double) Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // distance
    public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distance(double x1, double y1, double x2, double y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt(x * x + y * y);
    }

    public double deltaTime() {
        return mc.debugFPS > 0 ? (1.0000 / mc.debugFPS) : 1;
    }

    public float fast(float end, float start, float multiple) {
        return (1 - MathHelper.clamp((float) (deltaTime() * multiple), 0, 1)) * end
                + MathHelper.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public Vector3d interpolate(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(
                interpolate(end.getX(), start.getX(), multiple),
                interpolate(end.getY(), start.getY(), multiple),
                interpolate(end.getZ(), start.getZ(), multiple));
    }

    public Vector3d fast(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(
                fast((float) end.getX(), (float) start.getX(), multiple),
                fast((float) end.getY(), (float) start.getY(), multiple),
                fast((float) end.getZ(), (float) start.getZ(), multiple));
    }

    public float lerp(float end, float start, float multiple) {
        return (float) (end + (start - end) * MathHelper.clamp(deltaTime() * multiple, 0, 1));
    }

    public double lerp(double end, double start, double multiple) {
        return (end + (start - end) * MathHelper.clamp(deltaTime() * multiple, 0, 1));
    }

    private static final Random random = new Random();

    public static double easeOutBounce(double value) {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (value < 1.0 / d1) {
            return n1 * value * value;
        }
        if (value < 2.0 / d1) {
            return n1 * (value -= 1.5 / d1) * value + 0.75;
        }
        if (value < 2.5 / d1) {
            return n1 * (value -= 2.25 / d1) * value + 0.9375;
        }
        return n1 * (value -= 2.625 / d1) * value + 0.984375;
    }
    public static double getRandomInRange(double max, double min) {
        return min + (max - min) * random.nextDouble();
    }
    public static int randomize(int max, int min) {
        return -min + (int)(Math.random() * (double)(max - -min + 1));
    }
    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public boolean isInRegion(float mouseX, float mouseY, float x, float y, float width, float height) {

        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
