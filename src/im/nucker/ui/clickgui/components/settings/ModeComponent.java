package im.nucker.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.functions.settings.impl.ModeSetting;

import im.nucker.ui.clickgui.components.builder.Component;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.Cursors;

import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ModeComponent extends Component {

    final ModeSetting setting;

    float width = 0;
    float heightplus = 0;
    float spacing = 4;

    public ModeComponent(ModeSetting setting) {
        this.setting = setting;
        setHeight(22);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        Fonts.sfbold.drawText(stack, setting.getName(), getX() + 7, getY() + 2, ColorUtils.rgb(153, 153, 153), 5.5f, 0.05f);

        float offset = 0;
        float heightoff = 0;
        boolean plused = false;
        boolean anyHovered = false;
        for (String text : setting.strings) {
            float textWidth = Fonts.sfbold.getWidth(text, 5.5f, 0.05f) + 2;
            float textHeight = Fonts.sfbold.getHeight(5.5f) + 1;
            if (offset + textWidth + spacing >= (getWidth() - 10)) {
                offset = 0;
                heightoff += textHeight + spacing / 2;
                plused = true;
            }
            if (MathUtil.isHovered(mouseX, mouseY, getX() + 8 + offset, getY() + 11.5f + heightoff, textWidth, textHeight)) {
                anyHovered = true;
            }
            if (text.equals(setting.get())) {
                DisplayUtils.drawRoundedRect(getX() + 6.5f + offset, getY() + 9.5f + heightoff, textWidth+2, 9, 1, ColorUtils.setAlpha(ColorUtils.getColor(0), 100) );

                Fonts.sfbold.drawText(stack, text, getX() + 8 + offset, getY() + 11.5f + heightoff, ColorUtils.rgb(153, 153, 153), 5.5f, 0.07f);
            } else {
                DisplayUtils.drawRoundedRect(getX() + 6.5f + offset, getY() + 9.5f + heightoff, textWidth+2, 9, 1, ColorUtils.setAlpha(ColorUtils.rgba(21, 21, 21, 210), 100) );

                Fonts.sfbold.drawText(stack, text, getX() + 8 + offset, getY() + 11.5f + heightoff, ColorUtils.rgb(153, 153, 153), 5.5f, 0.05f);
            }

            offset += textWidth + spacing / 2;
        }
        if (isHovered(mouseX, mouseY)) {
            if (anyHovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
            } else {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            }
        }
        width = plused ? getWidth() - 15 : offset;
        setHeight(22 + heightoff);
        heightplus = heightoff;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        float offset = 0;
        float heightoff = 0;
        for (String text : setting.strings) {
            float textWidth = Fonts.sfbold.getWidth(text, 5.5f, 0.05f) + 2;
            float textHeight = Fonts.sfbold.getHeight(5.5f) + 1;
            if (offset + textWidth + spacing >= (getWidth() - 10)) {
                offset = 0;
                heightoff += textHeight + spacing / 2;
            }
            if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, getX() + 8 + offset, getY() + 10 + heightoff, Fonts.montserrat.getWidth(text, 5.5f, 0.05f), Fonts.montserrat.getHeight(5.5f) + 1)) {
                setting.set(text);
            }
            offset += textWidth + spacing / 2;
        }


        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}