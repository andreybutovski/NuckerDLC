package im.nucker.ui.display.impl;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.display.ElementUpdater;
import im.nucker.utils.math.StopWatch;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import im.nucker.utils.text.GradientUtil;
import net.minecraft.util.text.ITextComponent;
import ru.hogoshi.Animation;

import java.util.List;

public class ArrayListRenderer implements ElementRenderer, ElementUpdater {

    private int lastIndex;
    private List<Function> list;
    private StopWatch stopWatch = new StopWatch();
    private float spacing = 2.5f; // согл, пиздец


    @Override
    public void update(EventUpdate e) {
        if (stopWatch.isReached(1000)) {
            list = NuckerDLC.getInstance().getFunctionRegistry().getSorted(Fonts.sfui, 9 - 1.5f)
                    .stream()
                    .filter(m -> m.getCategory() != Category.Render)
                    .filter(m -> m.getCategory() != Category.Player)
                    .toList();
            stopWatch.reset();
        }
    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float rounding = 6;
        float padding = 3.5f;
        float posX = 4;
        float posY = 4 + 42;
        int index = 0;

        if (list == null) return;

        for (Function f : list) {
            float fontSize = 6.5f;
            Animation anim = f.getAnimation();
            float value = (float) anim.getValue();
            String text = f.getName();
            float textWidth = Fonts.sfui.getWidth(text, fontSize);

            if (value != 0) {
                float localFontSize = fontSize * value;
                float localTextWidth = textWidth * value;
                ITextComponent name = GradientUtil.gradient("s");

                // Рисуем кружок вокруг текста
                DisplayUtils.drawRoundedRect(posX, posY - 4, localTextWidth + padding * 2, localFontSize + padding * 2, rounding - 3f, ColorUtils.rgba(21, 21, 21, 210));

                // Рисуем текст в кружке
                Fonts.sfui.drawText(ms, f.getName(), posX + padding, posY + padding - 4, ColorUtils.rgb(255,255,255), localFontSize);

                posY += (fontSize + padding + 1.5f) * value + spacing; // Добавляем расстояние между кружками
                index++;

            }

        }

        lastIndex = index - 1;
    }

}