package im.nucker.functions.settings.impl;


import im.nucker.functions.settings.Setting;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

public class SliderSetting extends Setting<Float> {
    public float defaultVal;
    public float min;
    public float max;
    public float increment;

    public SliderSetting(String name, float defaultVal, float min, float max, float increment) {
        super(name, defaultVal);
        this.defaultVal = defaultVal;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }
    public double getValue() {
        return MathHelper.clamp(defaultVal, getMin(), getMax());
    }

    @Override
    public SliderSetting setVisible(Supplier<Boolean> bool) {
        return (SliderSetting) super.setVisible(bool);
    }

    public float getMin() {
        return min;
    }
    public float getMax() {
        return max;
    }

    public void setMin(float min) {
        this.min = min;
    }
}