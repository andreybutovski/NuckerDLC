package im.nucker.functions.impl.render;


import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;

@FunctionRegister(name = "FullBright", type = Category.Render)
public class FullBright extends Function {
    public ModeSetting fbType = new ModeSetting("Type", "Gamma");
    private float originalGamma;

    @Override
    public boolean onEnable() {
        super.onEnable();
        if (fbType.is("Gamma")) {
            originalGamma = (float) mc.gameSettings.gamma;
            mc.gameSettings.gamma = 100;
        }
        return false;
    }

    @Override
    public void onDisable() {
        if (fbType.is("Gamma")) {
            mc.gameSettings.gamma = originalGamma;
        }
        super.onDisable();
    }
}
