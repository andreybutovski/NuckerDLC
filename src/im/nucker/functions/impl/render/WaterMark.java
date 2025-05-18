package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.ui.styles.Style;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import im.nucker.utils.text.GradientUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

@FunctionRegister(name = "WaterMark", type = Category.Render)
public class WaterMark extends Function {

    private final ModeSetting modes = new ModeSetting("Мод",  "Delta", "Delta");

    final ResourceLocation logo = new ResourceLocation("expensive/images/hud/logo.png");
    final ResourceLocation user = new ResourceLocation("expensive/images/hud/user.png");
    final ResourceLocation fpslogo = new ResourceLocation("expensive/images/hud/fps.png");
    final ResourceLocation logodelta = new ResourceLocation("expensive/images/hud/logodelta.png");

    public WaterMark()
    {
        addSettings(modes);
    }

    @Subscribe
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = 5;
        float posY = 5;
        float padding = 5;
        float fontSize = 8f;
        float fontSize2 = 9f;
        float iconSize = 11.9f;

        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();

        //texts
        ITextComponent text = GradientUtil.gradient(" Nucker recode");
        ITextComponent text3 = GradientUtil.gradient(" Nucker");

        float textWidth1 = Fonts.sfbold.getWidth(text, fontSize);
        float localPosX = posX + iconSize + padding * 3;
        float textPosX1 = localPosX - 45 + iconSize + padding * 1.5f - 1;
        float textPosY = posY - 1.5f + iconSize / 2 + 1.5f;

        if (modes.get().equals("Delta")) {
            //osnova rect
            drawStyledRect(localPosX - 32, posY, textWidth1, iconSize + padding * 1.5f, 4);
            //user rect
            drawStyledRect(localPosX + 42, posY, textWidth1+35, iconSize + padding * 1.5f, 4);


            //texts
            Fonts.sfui.drawText(ms, text, textPosX1 * 17f, textPosY, fontSize+1, 255);
            Fonts.sfui.drawText(ms, text3, textPosX1 + 15, textPosY, fontSize+1, 255);

            //logotyps
            DisplayUtils.drawImage(logodelta, posX + 2f, posY + 5, 11, 10, ColorUtils.getColor(0));
            DisplayUtils.drawImage(user, posX * 15f, posY + 4, 13, 13, ColorUtils.getColor(0));

        }


    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {


        DisplayUtils.drawRoundedRect(x, y+2, width-1, height-3, 2, ColorUtils.rgb(21,21,21));


    }

}