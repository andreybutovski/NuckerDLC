package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.player.MoveUtils;

@FunctionRegister(name = "Jesus", type = Category.Movement)
public class Jesus extends Function {

    @Subscribe
    private void onUpdate(EventUpdate update) {
        if (mc.player.isInWater()) {
            float moveSpeed = 10.0f;
            moveSpeed /= 100.0f;

            double moveX = mc.player.getForward().x * moveSpeed;
            double moveZ = mc.player.getForward().z * moveSpeed;
            mc.player.motion.y = 0f;
            if (MoveUtils.isMoving()) {
                if (MoveUtils.getMotion() < 0.9f) {
                    mc.player.motion.x *= 1.25f;
                    mc.player.motion.z *= 1.25f;
                }
            }
        }
    }
}