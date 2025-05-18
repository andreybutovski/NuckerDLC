package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

@FunctionRegister(name = "WaterSpeed", type = Category.Player)
public class WaterSpeedFT extends Function {
    private static final float BASE_SPEED = 1.003f; // базовая скорость
    private static final float VARIATION = 0.0015f; // небольшие случайные колебания для лучшего обхода

    @Subscribe
    public void onPlayerUpdate(EventDisplay event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.isAlive()) {
            if (player.isInWater()) {
                // Применяем базовое ускорение
                double motionX = player.getMotion().x * BASE_SPEED;
                double motionZ = player.getMotion().z * BASE_SPEED;

                // Добавляем случайные колебания для плавности и предотвращения детекта
                motionX += (Math.random() - 0.5) * VARIATION;
                motionZ += (Math.random() - 0.5) * VARIATION;

                // Обновляем скорость игрока
                player.setMotion(motionX, player.getMotion().y, motionZ);
            }
        }
    }
}
