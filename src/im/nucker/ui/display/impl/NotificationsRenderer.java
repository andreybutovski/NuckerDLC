/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.nucker.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.events.EventUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegistry;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.styles.Style;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.font.Fonts;
import im.nucker.utils.text.GradientUtil;


public class NotificationsRenderer
        implements ElementRenderer {
    private final FunctionRegistry functionRegistry;
    private float width;
    private float height;

    public NotificationsRenderer() {
        this.functionRegistry = NuckerDLC.getInstance().getFunctionRegistry();
    }

    @Override
    public void update(EventUpdate e) {

    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
        float screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
        float posX = screenWidth - this.width - 5.0f;
        float posY = screenHeight - this.height - 5.0f;
        float fontSize = 6.5f;
        float padding = 5.0f;
        StringTextComponent title = (StringTextComponent) GradientUtil.gradient("Функции");
        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();
        DisplayUtils.drawShadow(posX, posY, this.width, this.height, 10, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        this.drawStyledRect(posX, posY, this.width, this.height, 4.0f);
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, this.width, this.height);
        Fonts.sfui.drawCenteredText(ms, title, posX + this.width / 2.0f, posY + padding + 0.5f, fontSize);
        posY += fontSize + padding * 2.0f;
        float maxWidth = Fonts.sfMedium.getWidth(title, fontSize) + padding * 2.0f;
        float localHeight = fontSize + padding * 2.0f;
        for (Function function : this.functionRegistry.getFunctions()) {
            String functionName = function.getName() + " " + (function.isState() ? "включено" : "выключено");
            float nameWidth = Fonts.sfMedium.getWidth(functionName, fontSize);
            Fonts.sfMedium.drawText(ms, functionName, posX + padding, posY, ColorUtils.rgba(210, 210, 210, 255), fontSize);
            if (nameWidth + padding * 2.0f > maxWidth) {
                maxWidth = nameWidth + padding * 2.0f;
            }
            posY += fontSize + padding;
            localHeight += fontSize + padding;
        }
        Scissor.unset();
        Scissor.pop();
        this.width = Math.max(maxWidth, 80.0f);
        this.height = localHeight + 2.5f;
    }

    private void drawStyledRect(float x, float y, float width, float height, float radius) {
        DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1.0f, height + 1.0f, radius + 0.5f, ColorUtils.getColor(0));
        DisplayUtils.drawRoundedRect(x, y, width, height, radius, ColorUtils.rgba(21, 21, 21, 255));
    }

    public NotificationsRenderer(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }
}