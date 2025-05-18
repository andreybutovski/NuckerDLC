package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "FOV", type = Category.Render)
public class FOV extends Function {
    public SliderSetting value = new SliderSetting("Значение", 60.0F, 15.0F, 170.0F, 1.0E-4F);

    public FOV() {
        this.addSettings(value);
    }

    @Subscribe
    private void onUpdate(EventUpdate var1) {
        float var2 = (Float)this.value.get();
        mc.gameSettings.fov = (double)var2;
    }

    public void onDisable() {
        super.onDisable();
        mc.gameSettings.fov = 105.0;
    }
}
