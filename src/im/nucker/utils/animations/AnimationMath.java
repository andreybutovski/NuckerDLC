package im.nucker.utils.animations;

import com.mojang.blaze3d.platform.GlStateManager;
import im.nucker.utils.client.IMinecraft;
import net.minecraft.util.math.MathHelper;

public class AnimationMath implements IMinecraft {
    public AnimationMath() {
    }

    public static double deltaTime() {
        return mc.debugFPS > 0 ? 1.0 / (double)mc.debugFPS : 1.0;
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0F - MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F)) * end + MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F) * start;
    }

    public static float lerp(float end, float start, float multiple) {
        return (float)((double)end + (double)(start - end) * MathHelper.clamp(deltaTime() * (double)multiple, 0.0, 1.0));
    }

    public static double lerp(double end, double start, double multiple) {
        return end + (start - end) * MathHelper.clamp(deltaTime() * multiple, 0.0, 1.0);
    }

    public static void sizeAnimation(double width, double height, double scale) {
        GlStateManager.translated(width, height, 0.0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-width, -height, 0.0);
    }
}

