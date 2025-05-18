package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@FunctionRegister(name = "FastEXP", type = Category.Player)
public class FastEXP extends Function {
    public FastEXP() {
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.fastEXP();
    }

    public void fastEXP() {
        if (mc.player != null) {
            ItemStack mainhandItem = mc.player.getHeldItemMainhand();
            if (!mainhandItem.isEmpty() && mainhandItem.getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
                return;
            }

            ItemStack offhandItem = mc.player.getHeldItemOffhand();
            if (!offhandItem.isEmpty() && offhandItem.getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
            }
        }

    }
}
