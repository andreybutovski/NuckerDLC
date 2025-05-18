package im.nucker.utils.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class PingUtil implements IMinecraft {
    public PingUtil() {
    }

    public static String calculateBPS() {
        Object[] var10001 = new Object[1];
        Minecraft var10004 = mc;
        double var1 = Minecraft.player.getPosX();
        Minecraft var10005 = mc;
        var1 -= Minecraft.player.prevPosX;
        var10005 = mc;
        double var0 = Minecraft.player.getPosZ();
        Minecraft var10006 = mc;
        var10001[0] = Math.hypot(var1, var0 - Minecraft.player.prevPosZ) * (double)mc.timer.timerSpeed * 20.0;
        return String.format("%.2f", var10001);
    }

    public static void drawItemStack(ItemStack stack, float x, float y, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0F);
        if (scale) {
            GL11.glScaled((double)scaleValue, (double)scaleValue, (double)scaleValue);
        }

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (withoutOverlay) {
            mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
        }

        RenderSystem.popMatrix();
    }

    public static int calculatePing() {
        Minecraft var10000 = mc;
        Minecraft var10001 = mc;
        int var0;
        if (Minecraft.player.connection.getPlayerInfo(Minecraft.player.getUniqueID()) != null) {
            var10000 = mc;
            var10001 = mc;
            var0 = Minecraft.player.connection.getPlayerInfo(Minecraft.player.getUniqueID()).getResponseTime();
        } else {
            var0 = 0;
        }

        return var0;
    }

    public static String serverIP() {
        return mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && !mc.isSingleplayer() ? mc.getCurrentServerData().serverIP : "";
    }
}
