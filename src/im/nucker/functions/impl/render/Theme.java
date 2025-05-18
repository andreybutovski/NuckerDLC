package im.nucker.functions.impl.render;

import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.render.ColorUtils;

@FunctionRegister(name = "Theme", type = Category.Render)
public class Theme extends Function {


    public static final ModeSetting THEME = new ModeSetting("Выбор Цвета",
            "Nursultan",
            "Nursultan",
            "НУРИК",
            "Морской",
            "Малиновый",
            "Черничный",
            "Необычный",
            "Огненный",
            "Прикольный",
            "Новогодний");

    public static final SliderSetting speedColors = new SliderSetting("Скорость цвета", 10, 0, 20, 1f);

    private final BooleanSetting openColor = new BooleanSetting("Открыть Цветовую панель", false);

    public Theme() {
        toggle();
        addSettings(THEME, speedColors, openColor);
    }
    public static int getColor(int index) {
        return Theme.Temka(index + 16);
    }

    public static int getColor(int index, float mult) {
        return Theme.Temka((int) (index * mult + 16) + 16);
    }

    public static int getColor(int firstColor, int secondColor, int index, float mult) {
        return ColorUtils.gradient(firstColor, secondColor, (int) (index * mult), speedColors.get().intValue());
    }

    public static int Temka(int index) {

        if (THEME.is("Nursultan")) {
            return ColorUtils.gradient(ColorUtils.rgb(0, 255, 255),
                    ColorUtils.rgb(0, 0, 255), index + 16, 15);
        }
        if (THEME.is("НУРИК")) {
            return ColorUtils.gradient(ColorUtils.rgb(255, 255, 0),
                    ColorUtils.rgb(255, 0, 255), index + 16, 15);
        }
        if (THEME.is("Морской")) {
            return ColorUtils.gradient(ColorUtils.rgb(5, 63, 111),
                    ColorUtils.rgb(133, 183, 246), index + 16, 15);
        }
        if (THEME.is("Малиновый")) {
            return ColorUtils.gradient(ColorUtils.rgb(109, 10, 40),
                    ColorUtils.rgb(239, 96, 136), index + 16, 15);
        }
        if (THEME.is("Черничный")) {
            return ColorUtils.gradient(ColorUtils.rgb(78, 5, 127),
                    ColorUtils.rgb(193, 140, 234), index + 16, 15);
        }
        if (THEME.is("Необычный")) {
            return ColorUtils.gradient(ColorUtils.rgb(243, 160, 232),
                    ColorUtils.rgb(171, 250, 243), index + 16, 15);
        }
        if (THEME.is("Огненный")) {
            return ColorUtils.gradient(ColorUtils.rgb(194, 21, 0),
                    ColorUtils.rgb(255, 197, 0), index + 16, 15);
        }
        if (THEME.is("Прикольный")) {
            return ColorUtils.gradient(ColorUtils.rgb(82, 241, 171),
                    ColorUtils.rgb(66, 172, 245), index + 16, 15);
        }
        if (THEME.is("Новогодний")) {
            return ColorUtils.gradient(ColorUtils.rgb(190, 5, 60),
                    ColorUtils.rgb(255, 255, 255), index + 16, 15);
        }


        return index * 16;
    }
}