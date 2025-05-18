package im.nucker.functions.impl.render;

import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ColorSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.render.ColorUtils;

@FunctionRegister(name = "Custom Fog", type = Category.Render)
public class CustomFog extends Function {
    public SliderSetting power = new SliderSetting("Сила", 20F, 1F,40F, 1F);
    public final ModeSetting mode = new ModeSetting("Мод","Клиент","Клиент","Свой");
    public ColorSetting color = new ColorSetting("Цвет", ColorUtils.rgb(255,255,255)).setVisible(()-> mode.is("Свой"));

    public CustomFog() {
        addSettings(power,mode,color);
    }

    public boolean state;

    public boolean onEnable() {
        super.onEnable();
        //Shaders.setShaderPack(Shaders.SHADER_PACK_NAME_DEFAULT);
        return false;
    }

    public void onEvent(EventUpdate event) {
    }

    public int getDepth() {
        return 6;
    }
}