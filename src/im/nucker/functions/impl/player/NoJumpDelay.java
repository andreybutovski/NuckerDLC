package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;

@FunctionRegister(name = "NoJumpDelay", type = Category.Player)
public class NoJumpDelay extends Function {
    @Subscribe
    public void onUpdate(EventUpdate e) {
        mc.player.jumpTicks = 0;
    }
}
