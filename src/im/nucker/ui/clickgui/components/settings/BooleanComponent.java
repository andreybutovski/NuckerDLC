package im.nucker.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;

import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.BooleanSetting;

import im.nucker.utils.math.MathUtil;


import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.Cursors;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.ui.clickgui.components.builder.Component;
import im.nucker.utils.render.font.Fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

/**
 * BooleanComponent
 */
public class BooleanComponent extends Component {

    private final BooleanSetting setting;


    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
        setHeight(16);
        animation = animation.animate(setting.get() ? 1 : 0, 0.5f, Easings.CIRC_OUT);
    }

    private Animation animation = new Animation();
    private float width, height;
    private boolean hovered = false;
    private final ResourceLocation booleansetting = new ResourceLocation("expensive/images/check.png");

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {

        super.render(stack, mouseX, mouseY);
        animation.update();
        Fonts.sfbold.drawText(stack, setting.getName(), getX() + 7, getY() + 6.5f / 2f + 1, ColorUtils.rgb(153, 153, 153), 6.5f, 0.02f);

        width = 15;
        height = 7;
        DisplayUtils.drawRoundedRect(getX() + getWidth() - width - 1, getY() + getHeight() / 2f - height / 2f-1.5f, width-5, height+3, 3f, ColorUtils.rgba(25,25,25, 170)); //ColorUtility.setAlpha(Themes.textColor, 100)
        int color = ColorUtils.interpolate(ColorUtils.rgb(153, 153, 153),ColorUtils.rgb(153, 153, 153), 1 - (float) animation.getValue());
        DisplayUtils.drawImage(booleansetting, getX() + getWidth() - width, getY() - 1.5f + getHeight() / 2f - height / 2f + 2f, 8.0f, 8.0f, ColorUtils.setAlpha(color, (int) (125 * animation.getValue())));

        if (isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, getX() + getWidth() - width, getY() + getHeight() / 2f - height / 2f , width,
                    height)) {
                if (!hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
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
        if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, getX() + getWidth() - width , getY() + getHeight() / 2f - height / 2f, width,
                height)) {
            setting.set(!setting.get());
            animation = animation.animate(setting.get() ? 1 : 0, 0.2f, Easings.CIRC_OUT);
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }

}