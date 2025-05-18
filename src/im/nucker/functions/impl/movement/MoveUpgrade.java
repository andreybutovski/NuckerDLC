package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.events.EventMotion;
import im.nucker.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "MoveUpgrade", type = Category.Movement)
public class MoveUpgrade extends Function {

    private final SliderSetting value = new SliderSetting("Сила", 50.0f, 10.0f, 500.0f, 5.0f);
    private float rotationYaw = 0;

    public MoveUpgrade() {
        addSettings(value);
    }

    @Subscribe
    private void onWalking(EventMotion e) {
        if (mc.player == null) return;

        // Проверяем, движется ли игрок
        if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
            rotationYaw -= value.get(); // Ускоренное вращение

            // Визуальное вращение (видно только клиенту)
            mc.player.rotationYawHead = rotationYaw;
            mc.player.renderYawOffset = rotationYaw;

            // НЕ меняем yaw в ивенте, чтобы сервер не видел изменение
        }
    }
}
