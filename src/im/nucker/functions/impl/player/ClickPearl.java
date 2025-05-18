package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.events.EventKey;
import im.nucker.events.EventMotion;
import im.nucker.events.EventPacket;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.api.FunctionRegistry;
import im.nucker.functions.settings.impl.BindSetting;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.utils.math.StopWatch;
import im.nucker.utils.player.InventoryUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

@FieldDefaults(level = AccessLevel.PRIVATE)
@FunctionRegister(name = "ClickPearl", type = Category.Player)
public class ClickPearl extends Function {
    final BindSetting throwKey = new BindSetting("Кнопка", -98);
    final BooleanSetting obxod = new BooleanSetting("Бросать легитно",false);
    final StopWatch stopWatch = new StopWatch();
    final InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    private final long pearlThrowDelay = 250L;
    private final long returnDelay = 50;
    private long lastPearlThrowTime = 0L;
    private int originalSlot = -1;
    private int pearlSlot = -1;

    final ItemCooldown itemCooldown;
    long delay;
    boolean throwPearl;
    public ClickPearl(ItemCooldown itemCooldown) {
        this.itemCooldown = itemCooldown;
        addSettings(throwKey,obxod);
    }

    @Subscribe
    public void onKey(EventKey e) {
        throwPearl = e.getKey() == throwKey.get();
    }

    @Subscribe
    private void onMotion(EventMotion e) {
        if (throwPearl) {
            if (obxod.get()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - this.lastPearlThrowTime >= 5350L) {
                    if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL)) {
                        this.findPearl();
                        if (this.pearlSlot != -1) {
                            this.originalSlot = mc.player.inventory.currentItem;
                            mc.player.inventory.currentItem = this.pearlSlot;
                            mc.playerController.updateController();
                            mc.player.connection.sendPacket(new CHeldItemChangePacket(this.pearlSlot));
                            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                            mc.player.swingArm(Hand.MAIN_HAND);
                            this.delay = System.currentTimeMillis() + 2050L;
                        }
                    }

                    this.throwPearl = false;
                }
            }
            if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL)) {
                boolean isOffhandEnderPearl = mc.player.getHeldItemOffhand().getItem() instanceof EnderPearlItem;
                if (isOffhandEnderPearl) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.swingArm(Hand.MAIN_HAND);
                } else {
                    int slot = findPearlAndThrow();
                    if (slot > 8) {
                        mc.playerController.pickItem(slot);
                    }
                }
            }
            throwPearl = false;
        }
    }


    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (obxod.get()) {
            if (this.delay > 400L && System.currentTimeMillis() >= this.delay) {
                mc.player.inventory.currentItem = this.originalSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CHeldItemChangePacket(this.originalSlot));
                this.delay = -1L;
            }
        }
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 300L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    private int findPearlAndThrow() {
        int hbSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.ENDER_PEARL, true);
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            if (hbSlot != mc.player.inventory.currentItem) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            }
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);

            FunctionRegistry functionRegistry = NuckerDLC.getInstance().getFunctionRegistry();
            ItemCooldown itemCooldown = functionRegistry.getItemCooldown();
            ItemCooldown.ItemEnum itemEnum = ItemCooldown.ItemEnum.getItemEnum(Items.ENDER_PEARL);

            if (itemCooldown.isState() && itemEnum != null && itemCooldown.isCurrentItem(itemEnum)) {
                itemCooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
            }

            if (hbSlot != mc.player.inventory.currentItem) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
            }
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }

        int invSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.ENDER_PEARL, false);

        if (invSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);

            FunctionRegistry functionRegistry = NuckerDLC.getInstance().getFunctionRegistry();
            ItemCooldown itemCooldown = functionRegistry.getItemCooldown();
            ItemCooldown.ItemEnum itemEnum = ItemCooldown.ItemEnum.getItemEnum(Items.ENDER_PEARL);

            if (itemCooldown.isState() && itemEnum != null && itemCooldown.isCurrentItem(itemEnum)) {
                itemCooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
            }
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }
    private void findPearl() {
        if (obxod.get()) {
            this.pearlSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.ENDER_PEARL, true);
        }
    }

    @Override
    public void onDisable() {
        throwPearl = false;
        delay = 0;
        super.onDisable();
    }
}
