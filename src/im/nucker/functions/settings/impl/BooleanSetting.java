package im.nucker.functions.settings.impl;


import im.nucker.functions.settings.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> bool) {
        return (BooleanSetting) super.setVisible(bool);
    }

}