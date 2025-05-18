package im.nucker.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.functions.api.Category;
import im.nucker.ui.styles.Style;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.Cursors;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.Scissor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Getter
public class PanelStyle extends Panel {

    private boolean visible = false; // Управление видимостью панели

    public PanelStyle(Category category) {
        super(category);
    }

    float max = 0;
    float y = 237;
    float x = 23;

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        if (!visible) return; // Если панель скрыта, не рендерим

        float header = 25;
        float headerFont = 8;
        setAnimatedScrool(MathUtil.fast(getAnimatedScrool(), getScroll(), 10));

        float panelWidth = width / 2;

        DisplayUtils.drawRoundedRect(x, y + 24, panelWidth - 19, height - 45, 13, ColorUtils.rgba(15, 15, 15, 210));
        DisplayUtils.drawRoundedRect(x + 3.8f, y + 27.5f, panelWidth - 27, height - 53, 12,
                ColorUtils.rgba(15, 15, 15, 165));
        DisplayUtils.drawShadow(x + 3.8f, y + 27.5f, panelWidth - 27, height - 53, 12,
                ColorUtils.rgba(15, 15, 25, 125));

        if (max > height - 24 * 2) {
            setScroll(MathHelper.clamp(getScroll(), -max + height - header - 10, 0));
            setAnimatedScrool(MathHelper.clamp(getAnimatedScrool(), -max + height - header - 10, 0));
        } else {
            setScroll(0);
            setAnimatedScrool(0);
        }

        float animationValue = (float) DropDown.getAnimation().getValue() * DropDown.scale;
        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float height = getHeight();
        float testX = getX() + (panelWidth * halfAnimationValueRest);
        float testY = getY() + 25 + (height * halfAnimationValueRest);
        float testW = panelWidth * animationValue;
        float testH = height * animationValue - 56;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * halfAnimationValueRest);
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);

        int offset = 1;
        boolean hovered = false;

        float x = this.x + 5;
        float y = this.y + header + 5 + offset + getAnimatedScrool();
        float H = 12;

        for (Style style : NuckerDLC.getInstance().getStyleManager().getStyleList()) {
            if (MathUtil.isHovered(mouseX, mouseY, x + 5, y, panelWidth - 10 - 10, H)) {
                hovered = true;
            }
            if (NuckerDLC.getInstance().getStyleManager().getCurrentStyle() == style) {
                DisplayUtils.drawRoundedRect(x + 5f, y + 7, panelWidth - 40, H, 10, style.getFirstColor().getRGB());
                DisplayUtils.drawShadow(x + 5f, y + 7.5f, panelWidth - 40, H, 10, style.getSecondColor().getRGB());
            }
            DisplayUtils.drawRoundedRect(x + 5f, y + 7.5f, panelWidth - 40, H, 2, style.getFirstColor().getRGB());
            y += 5 + H;
            offset++;
        }

        if (MathUtil.isHovered(mouseX, mouseY, x, y, panelWidth, height)) {
            GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), hovered ? Cursors.HAND : Cursors.ARROW);
        }
        Scissor.unset();
        Scissor.pop();
        max = offset * NuckerDLC.getInstance().getStyleManager().getStyleList().size() * 1.21f;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_TAB) {
            visible = !visible; // Переключаем видимость панели
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!visible) return; // Если панель скрыта, клики не обрабатываем

        float header = 25;
        int offset = 0;
        float x = this.x + 5;
        float y = this.y + offset + header + 5 + getAnimatedScrool();

        for (Style style : NuckerDLC.getInstance().getStyleManager().getStyleList()) {
            float barHeight = 12;
            float barY = y + 7.5f;
            if (MathUtil.isHovered(mouseX, mouseY, x + 5, barY, width / 2 - 40, barHeight)) {
                NuckerDLC.getInstance().getStyleManager().setCurrentStyle(style);
            }
            y += 5 + barHeight;
            offset++;
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {}
}
