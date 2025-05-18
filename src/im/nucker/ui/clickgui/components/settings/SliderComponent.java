package im.nucker.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.functions.settings.impl.SliderSetting;

import im.nucker.ui.clickgui.components.builder.Component;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.Cursors;

import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/**
 * SliderComponent
 */
public class SliderComponent extends Component {

    private final SliderSetting setting;

    public SliderComponent(SliderSetting setting) {
        this.setting = setting;
        this.setHeight(18);
    }
    private float newValue, lastValue;
    private float anim;
    private boolean drag;
    private boolean hovered = false;

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        Fonts.sfbold.drawText(stack, setting.getName(), getX() + 5, getY() + 4.5f / 2f + 1, ColorUtils.rgb(153, 153, 153), 5.5f, 0.05f);
        Fonts.sfbold.drawText(stack, String.valueOf(setting.get()), getX() + getWidth() - 6 - Fonts.sfbold.getWidth(String.valueOf(setting.get()), 5.5f), getY() + 4.5f / 2f + 1, ColorUtils.rgb(153, 153, 153), 5.5f, 0.05f);

        DisplayUtils.drawRoundedRect(getX() + 5, getY() + 12, getWidth() - 10, 2, 0.6f, ColorUtils.rgba(55,55,55,100));
        anim = MathUtil.fast(anim, (getWidth() - 10) * (setting.get() - setting.min) / (setting.max - setting.min), 20);
        float sliderWidth = anim;
        DisplayUtils.drawRoundedRect(getX() + 5, getY() + 12, sliderWidth, 2, 1, ColorUtils.rgba(123, 123, 123,200));
        DisplayUtils.drawCircle(getX() + 5 + sliderWidth, getY() + 13, 5, ColorUtils.rgba(153, 153, 153,255));
        //DisplayUtils.drawShadowCircle(getX() + 5 + sliderWidth, getY() + 13, 10, Themes.rectColor);
        if (drag) {
            GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(),
                    GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
            float newValue = (float) MathHelper.clamp(
                    MathUtil.round((mouseX - getX() - 5) / (getWidth() - 10) * (setting.max - setting.min) + setting.min,
                            setting.increment), setting.min, setting.max);
            if (newValue != lastValue) {
                setting.set(newValue);
                lastValue = newValue;
            }
        }
        if (isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, getX() + 5, getY() + 10, getWidth() - 10, 3)) {
                if (!hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.RESIZEH);
                    hovered = true;
                }
            } else {
                if (hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
                    hovered = false;
                }
            }
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        if (MathUtil.isHovered(mouseX, mouseY, getX() + 5, getY() + 10, getWidth() - 10, 3)) {
            drag = true;
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {
        drag = false;
        super.mouseRelease(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }

}