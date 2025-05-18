
package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.BooleanSetting;

@FunctionRegister(
        name = "Optimization",
        type = Category.Render
)
public class Optimization extends Function {
    public final BooleanSetting ofSky = new BooleanSetting("Оптимизация облаков", true);
    public final BooleanSetting ofCustomSky = new BooleanSetting("Оптимизация неба", true);
    public final BooleanSetting entityShadows = new BooleanSetting("Оптимизация сущностей", true);
    public final BooleanSetting optimizeLighting = new BooleanSetting("Освещение", true);
    public final BooleanSetting optimizeParticles = new BooleanSetting("Партиклы", true);
    public final BooleanSetting optimizeClientHighlight = new BooleanSetting("Подсветка клиента", false);

    public Optimization() {
        this.addSettings(new Setting[]{this.ofSky, this.ofCustomSky, this.entityShadows, this.optimizeLighting, this.optimizeParticles, this.optimizeClientHighlight});
    }

    @Subscribe
    private void onEventUpdate(EventUpdate var1) {
        if ((Boolean)this.ofSky.get()) {
            Minecraft.getInstance().gameSettings.ofSky = false;
        }

        if ((Boolean)this.ofCustomSky.get()) {
            Minecraft.getInstance().gameSettings.ofCustomSky = false;
        }

        if ((Boolean)this.entityShadows.get()) {
            Minecraft.getInstance().gameSettings.entityShadows = false;
        }

        if ((Boolean)this.optimizeLighting.get()) {
        }

        if ((Boolean)this.optimizeParticles.get()) {
        }

        if ((Boolean)this.optimizeClientHighlight.get()) {
        }

        long var2 = Runtime.getRuntime().maxMemory();
        long var4 = Runtime.getRuntime().totalMemory();
        long var6 = Runtime.getRuntime().freeMemory();
        long var8 = var4 - var6 - var2;
    }

    public void onDisable() {
        super.onDisable();
        Minecraft.getInstance().gameSettings.autoJump = true;
        Minecraft.getInstance().gameSettings.ofSky = true;
        Minecraft.getInstance().gameSettings.ofCustomSky = true;
        Minecraft.getInstance().gameSettings.entityShadows = true;
    }
}
