package im.nucker.utils.render;

import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.systems.RenderSystem;

import im.nucker.functions.impl.render.HUD;
import im.nucker.utils.math.MathUtil;

import java.awt.*;

@UtilityClass
public class ColorUtils {

    public final int green = new Color(64, 255, 64).getRGB();
    public final int yellow = new Color(255, 255, 64).getRGB();
    public final int orange = new Color(255, 128, 32).getRGB();
    public final int red = new Color(255, 64, 64).getRGB();

    public static int rgb(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static void setAlphaColor(final int color, final float alpha) {
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public static int getColor(int index) {
        return HUD.getColor(index);
    }

    public static void setColor(int color) {
        setAlphaColor(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static int toColor(String hexColor) {
        int argb = Integer.parseInt(hexColor.substring(1), 16);
        return setAlpha(argb, 255);
    }

    public static int setAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static float[] rgba(final int color) {
        return new float[]{
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f
        };
    }

    public static int gradient(int start, int end, int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int color = interpolate(start, end, MathHelper.clamp(angle / 180f - 1, 0, 1));
        float[] hs = rgba(color);
        float[] hsb = Color.RGBtoHSB((int) (hs[0] * 255), (int) (hs[1] * 255), (int) (hs[2] * 255), null);

        hsb[1] *= 1.5F;
        hsb[1] = Math.min(hsb[1], 1.0f);

        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int interpolate(int start, int end, float value) {
        float[] startColor = rgba(start);
        float[] endColor = rgba(end);

        return rgba((int) MathUtil.interpolate(startColor[0] * 255, endColor[0] * 255, value),
                (int) MathUtil.interpolate(startColor[1] * 255, endColor[1] * 255, value),
                (int) MathUtil.interpolate(startColor[2] * 255, endColor[2] * 255, value),
                (int) MathUtil.interpolate(startColor[3] * 255, endColor[3] * 255, value));
    }


    public static int reAlphaInt(final int color,
                                 final int alpha) {
        return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 16777215);
    }

    public static int astolfo(int speed, int offset, float saturation, float brightness, float alpha) {
        float hue = (float) calculateHueDegrees(speed, offset);
        hue = (float) ((double) hue % 360.0);
        float hueNormalized;
        return reAlphaInt(
                Color.HSBtoRGB((double) ((hueNormalized = hue % 360.0F) / 360.0F) < 0.5 ? -(hueNormalized / 360.0F) : hueNormalized / 360.0F, saturation, brightness),
                Math.max(0, Math.min(255, (int) (alpha * 255.0F)))
        );
    }

    private static int calculateHueDegrees(int divisor, int offset) {
        long currentTime = System.currentTimeMillis();
        long calculatedValue = (currentTime / divisor + offset) % 360L;
        return (int) calculatedValue;
    }

    public static void setColor1(int color) {
        setAlphaColor(color, (float)(color >> 24 & 255) / 255.0F);
    }


    public static class IntColor {

        public static float[] rgb(final int color) {
            return new float[]{
                    (color >> 16 & 0xFF) / 255f,
                    (color >> 8 & 0xFF) / 255f,
                    (color & 0xFF) / 255f,
                    (color >> 24 & 0xFF) / 255f
            };
        }

        public static int getRed(final int hex) {
            return hex >> 16 & 255;
        }

        public static int getGreen(final int hex) {
            return hex >> 8 & 255;
        }

        public static int getBlue(final int hex) {
            return hex & 255;
        }

        public static int getAlpha(final int hex) {
            return hex >> 24 & 255;
        }

        public static int rgba(final int r,
                               final int g,
                               final int b,
                               final int a) {
            return a << 24 | r << 16 | g << 8 | b;
        }
    }
}
