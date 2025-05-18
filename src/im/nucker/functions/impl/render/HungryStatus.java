
package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

@FunctionRegister(
        name = "HungryStatus",
            type = Category.Render
)
public class HungryStatus extends Function {
    private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft mc = Minecraft.getInstance();

    public HungryStatus() {
    }

    @Subscribe
    public void onDisplay(EventDisplay var1) {

        if (Minecraft.player != null) {

            int var2 = Minecraft.player.getFoodStats().getFoodLevel();

            float var3 = Minecraft.player.getFoodStats().getSaturationLevel();
            float var4 = 20.0F;
            float var5 = ((float)var2 + var3) / var4 * 100.0F;

            int var6 = Minecraft.player.isInWater() ? 10 : 0;
            this.renderHungerPercentage(var1.getMatrixStack(), var5, var6);
        }

    }

    private void renderHungerPercentage(MatrixStack var1, float var2, int var3) {
        FontRenderer var4 = this.mc.fontRenderer;
        String var5 = String.format("%.1f%%", var2);
        int var6 = this.mc.getMainWindow().getScaledWidth() / 2 + 70;
        int var7 = this.mc.getMainWindow().getScaledHeight() - 39 - var3;
        int var8 = this.getColorForPercentage(var2);
        int var9 = var4.getStringWidth(var5) / 2;
        var4.drawStringWithShadow(var1, var5, (float)(var6 - var9), (float)(var7 - 10), var8);
    }

    private int getColorForPercentage(float var1) {
        int var2;
        int var3;
        byte var4;
        if (var1 >= 100.0F) {
            var2 = 0;
            var3 = 255;
            var4 = 0;
        } else if (var1 >= 70.0F) {
            var2 = (int)(255.0F * (var1 - 70.0F) / 25.0F);
            var3 = 255;
            var4 = 0;
        } else if (var1 >= 30.0F) {
            var2 = 255;
            var3 = (int)(255.0F * (var1 - 30.0F) / 40.0F);
            var4 = 0;
        } else {
            var2 = 255;
            var3 = 0;
            var4 = 0;
        }

        return var2 << 16 | var3 << 8 | var4;
    }
}
