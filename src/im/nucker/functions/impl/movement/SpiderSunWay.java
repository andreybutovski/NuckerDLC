package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventMotion;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

@FunctionRegister(name = "WaterClimb", type = Category.Movement)
public class SpiderSunWay extends Function {
    private long lastPlaceTime = 0; // Время последней установки воды

    public SpiderSunWay() {
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (mc.player.getHeldItemMainhand().getItem() == Items.WATER_BUCKET) {
            if (mc.player.collidedHorizontally) {
                long currentTime = System.currentTimeMillis();

                // Проверяем задержку, чтобы избежать мгновенного клика (обход античита)
                if (currentTime - lastPlaceTime > 150) { // 150 мс задержки
                    BlockPos pos = mc.player.getPosition();

                    // Ставим воду
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                    mc.player.setMotion(mc.player.getMotion().x, 0.36, mc.player.getMotion().z);

                    lastPlaceTime = currentTime; // Обновляем время установки воды

                    // Делаем задержку перед забором воды, чтобы обойти античит
                    new Thread(() -> {
                        try {
                            Thread.sleep(200); // 200 мс задержки перед забором воды
                            if (mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
                                mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
