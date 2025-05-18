package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventKey;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BindSetting;
import im.nucker.utils.player.InventoryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

@FunctionRegister(name = "HW Helper", type = Category.Misc)
public class HWhelper extends Function {
    private final BindSetting throwKey = new BindSetting("Трапка", -1);
    private final BindSetting secondaryKey = new BindSetting("Станка", -1);
    private final BindSetting snowballKey = new BindSetting("Ком Снега", -1);
    private final BindSetting fireworkStarKey = new BindSetting("Прощальный гул", -1);
    private long delay;
    private boolean actionPending;
    private long lastItemThrowTime = 0L;
    private int originalSlot = -1;
    private int itemSlot = -1;
    private Item currentItem;

    public HWhelper() {
        this.addSettings(this.throwKey, this.secondaryKey, this.snowballKey, this.fireworkStarKey);
    }

    @Subscribe
    public void onKey(EventKey e) {
        if (e.getKey() == (Integer)this.throwKey.get()) {
            this.currentItem = Items.PRISMARINE_SHARD;
            this.actionPending = true;
        } else if (e.getKey() == (Integer)this.secondaryKey.get()) {
            this.currentItem = Items.NETHER_STAR;
            this.actionPending = true;
        } else if (e.getKey() == (Integer)this.snowballKey.get()) {
            this.currentItem = Items.SNOWBALL;
            this.actionPending = true;
        } else if (e.getKey() == (Integer)this.fireworkStarKey.get()) {
            this.currentItem = Items.FIREWORK_STAR;
            this.actionPending = true;
        }

    }

    @Subscribe
    private void onMotion(EventMotion e) {
        if (this.actionPending && System.currentTimeMillis() - this.lastItemThrowTime >= 250L) {
            if (this.currentItem != null) {
                if (!mc.player.getCooldownTracker().hasCooldown(this.currentItem)) {
                    this.itemSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(this.currentItem, true);
                    if (this.itemSlot != -1) {
                        this.performThrow(this.itemSlot);
                    }
                }
            }

            this.lastItemThrowTime = System.currentTimeMillis();
            this.actionPending = false;
        }

    }

    private void performThrow(int itemSlot) {
        this.originalSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = itemSlot;
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CHeldItemChangePacket(itemSlot));
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.player.swingArm(Hand.MAIN_HAND);
        this.delay = System.currentTimeMillis() + 50L;
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.delay > 0L && System.currentTimeMillis() >= this.delay) {
            mc.player.inventory.currentItem = this.originalSlot;
            mc.playerController.updateController();
            mc.player.connection.sendPacket(new CHeldItemChangePacket(this.originalSlot));
            this.delay = -1L;
        }

    }
}
