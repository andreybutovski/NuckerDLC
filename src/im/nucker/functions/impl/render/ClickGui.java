package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "ClickGui", type = Category.Render)
public class ClickGui extends Function {

    public static BooleanSetting blur = new BooleanSetting("Размыть", false);
    public static SliderSetting blurPower = new SliderSetting("Сила размытия", 2, 1, 4, 1).setVisible(() -> blur.get());
    public static BooleanSetting images = new BooleanSetting("Картинки", true);
    public static ModeSetting imageType = new ModeSetting("Текстура", "MyLove", "MyLove", "CatGirl", "CatGirl2", "Emilia", "Pinky", "KisKis", "FurryMaid", "Nyashka", "Nolik", "Miku", "Novoura", "PSChan","Clickgui","logo").setVisible(() -> images.get());
    
    public ClickGui() {
        addSettings(blur, blurPower, images, imageType);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public boolean onEnable() {
        super.onEnable();
        return false;
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        toggle();
    }
}
