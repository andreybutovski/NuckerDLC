package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventDisplay.Type;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import java.awt.Color;
import net.minecraft.client.settings.PointOfView;

@FunctionRegister(
        name = "Crosshair",
        type = Category.Render
)
public class Crosshair extends Function {
    private final ModeSetting mode = new ModeSetting("Вид", "Круг", new String[]{"Круг", "Класический"});
    private final BooleanSetting staticCrosshair = new BooleanSetting("Статический", false);
    private float lastYaw;
    private float lastPitch;
    private float animatedYaw;
    private float animatedPitch;
    private float animation;
    private float animationSize;
    private final int outlineColor;
    private final int entityColor;

    public Crosshair() {
        this.outlineColor = Color.BLACK.getRGB();
        this.entityColor = Color.RED.getRGB();
        this.addSettings(new Setting[]{this.mode, this.staticCrosshair});
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (mc.player != null && mc.world != null && e.getType() == Type.POST) {
            float x = (float)mc.getMainWindow().getScaledWidth() / 2.0F;
            float y = (float)mc.getMainWindow().getScaledHeight() / 2.0F;
            float padding = 5.0F;
            float cooldown;
            float length;
            switch(this.mode.getIndex()) {
                case 0:
                    cooldown = 5.0F;
                    int color = ColorUtils.interpolate(HUD.getColor(1), HUD.getColor(1), 1.0F - this.animation);
                    if (!(Boolean)this.staticCrosshair.get()) {
                        x += this.animatedYaw;
                        y += this.animatedPitch;
                    }

                    this.animationSize = MathUtil.fast(this.animationSize, (1.0F - mc.player.getCooledAttackStrength(1.0F)) * 260.0F, 10.0F);
                    length = 3.0F + ((Boolean)this.staticCrosshair.get() ? 0.0F : this.animationSize);
                    if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                        DisplayUtils.drawCircle1(x, y, 0.0F, 360.0F, 3.5F, 3.0F, false, ColorUtils.getColor(90));
                        DisplayUtils.drawCircle1(x, y, 0.0F, this.animationSize, 3.5F, 3.0F, false, ColorUtils.rgb(23, 21, 21));
                    }
                    break;
                case 1:
                    if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                        return;
                    }

                    cooldown = 1.0F - mc.player.getCooledAttackStrength(1.0F);
                    float thickness = 1.0F;
                    length = 3.0F;
                    float gap = 2.0F + 8.0F * cooldown;
                    color = mc.pointedEntity != null ? this.entityColor : -1;
                    this.drawOutlined(x - thickness / 2.0F, y - gap - length, thickness, length, ColorUtils.getColor(90));
                    this.drawOutlined(x - thickness / 2.0F, y + gap, thickness, length, ColorUtils.getColor(90));
                    this.drawOutlined(x - gap - length, y - thickness / 2.0F, length, thickness, color);
                    this.drawOutlined(x + gap, y - thickness / 2.0F, length, thickness, color);
            }

        }
    }

    private void drawOutlined(float x, float y, float w, float h, int hex) {
        DisplayUtils.drawRectW((double)x - 0.5D, (double)y - 0.5D, (double)(w + 1.0F), (double)(h + 1.0F), this.outlineColor);
        DisplayUtils.drawRectW((double)x, (double)y, (double)w, (double)h, hex);
    }
}