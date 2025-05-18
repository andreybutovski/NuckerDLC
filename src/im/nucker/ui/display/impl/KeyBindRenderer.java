package im.nucker.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Function;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.styles.Style;
import im.nucker.utils.client.KeyStorage;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.font.Fonts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KeyBindRenderer implements ElementRenderer {

    final Dragging dragging;

    float width;
    float height;

    private final ResourceLocation on_function = new ResourceLocation("expensive/images/on_function.png");

    @Override
    public void update(EventUpdate e) {

    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 7.6f;
        float padding = 5;

        ITextComponent name = new StringTextComponent("Hotkeys").mergeStyle(TextFormatting.WHITE);

        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();

        DisplayUtils.drawShadow(posX, posY, width, height, 7, ColorUtils.rgba(9, 8, 23, 1));
        DisplayUtils.drawRoundedRect(posX - 1.3f, posY - 1.3f, width + 2.8f, height + 2.8f, 5, ColorUtils.rgb(46, 45, 58));
        DisplayUtils.drawRoundedRect(posX - 0.5f, posY - 0.5f, width + 1f, height + 1f, 4, ColorUtils.rgb(9, 8, 23));
        Fonts.icons2.drawText(eventDisplay.getMatrixStack(), "C", posX + 63, posY + 5.5f, ColorUtils.rgba(190,185,255,255), 10);


        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        Fonts.sfui.drawCenteredText(ms, name, posX + width / 4, posY + padding + 0.5f, fontSize + 0.45f);
        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfui.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;


        DisplayUtils.drawRectHorizontalW(posX + 0.5f, posY, width - 1, 1.5f, 3, ColorUtils.rgba(46, 45, 58, (int) (255 * 1f)));
        posY += 4f;

        for (Function f : NuckerDLC.getInstance().getFunctionRegistry().getFunctions()) {
            f.getAnimation().update();
            if (!(f.getAnimation().getValue() > 0) || f.getBind() == 0)
                continue;
            String nameText = f.getName();
            float nameWidth = Fonts.sfui.getWidth(nameText, fontSize);

            String bindText = "[" + KeyStorage.getKey(f.getBind()) + "]";
            float bindWidth = Fonts.sfui.getWidth(bindText, fontSize);

            float localWidth = nameWidth + bindWidth + padding * 3;

            Fonts.sfui.drawText(ms, nameText, posX + padding, posY + 0.5f,
                    ColorUtils.rgba(255, 255, 255, (int) (255 * f.getAnimation().getValue())), fontSize + 0.1f);
            Fonts.icons2.drawText(eventDisplay.getMatrixStack(), "J", posX + 63, posY + 0.5f,
                    ColorUtils.rgba(255, 255, 255, (int) (255 * f.getAnimation().getValue())), fontSize + 2);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (fontSize + padding) * f.getAnimation().getValue();
            localHeight += (fontSize + padding) * f.getAnimation().getValue();
        }
        Scissor.unset();
        Scissor.pop();

        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }
}
