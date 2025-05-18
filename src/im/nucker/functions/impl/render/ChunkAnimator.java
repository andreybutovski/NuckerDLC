package im.nucker.functions.impl.render;

import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.api.Function;
import com.google.common.eventbus.Subscribe;

@FunctionRegister(name="ChunkAnimator", type = Category.Render)
public class ChunkAnimator extends Function {

    @Subscribe
    public void onEvent(EventUpdate event) {

    }
}