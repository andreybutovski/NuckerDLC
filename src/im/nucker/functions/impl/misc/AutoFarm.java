package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.math.TimerUtility;
import im.nucker.utils.player.MoveUtils;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

@FunctionRegister(name = "AutoFarm", type = Category.Misc)
public class AutoFarm extends Function {
    public ModeSetting modchen = new ModeSetting("Цена опыта", "150к", new String[]{"150к", "200к", "250к"});
    public ModeSetting mod = new ModeSetting("Количество", "1 стак", new String[]{"1 стак", "2 стака", "3 стака"});
    private final TimerUtility stopWatchMain = new TimerUtility();
    private final TimerUtility stopWatch = new TimerUtility();
    private boolean autoRepair;
    private boolean expValid;
    private float previousPitch;

    public AutoFarm() {
        this.addSettings(new Setting[]{this.modchen, this.mod});
    }

    public void toggle() {
        super.toggle();
        this.autoRepair = false;
        this.expValid = false;
    }

    @Subscribe
    public void onMotion(EventMotion eventMotion) {
        this.previousPitch = 90.0F;
        eventMotion.setPitch(this.previousPitch);
    }

    @Subscribe
    public void onUpdate(EventUpdate eventUpdate) {
        List list = List.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE);
        List list2 = List.of(Items.CARROT, Items.POTATO);
        Slot slot = this.getInventorySlot(Items.EXPERIENCE_BOTTLE);
        Slot slot2 = this.getInventorySlot(list2);
        Slot slot3 = this.getInventorySlot(list);
        int n = this.modchen.is("150к") ? 150000 : (this.modchen.is("200к") ? 200000 : 250000);
        int n2 = this.mod.is("1 стак") ? 62 : (this.mod.is("2 стака") ? 126 : 190);
        int n3 = this.getInventoryCount(Items.EXPERIENCE_BOTTLE);
        Item item = Minecraft.player.getHeldItemMainhand().getItem();
        Item item2 = Minecraft.player.getHeldItemOffhand().getItem();
        if (slot3 != null && !MoveUtils.isMoving() && this.stopWatchMain.isReached(500L)) {
            float f = 1.0F - MathHelper.clamp((float)slot3.getStack().getDamage() / (float)slot3.getStack().getMaxDamage(), 0.0F, 1.0F);
            if ((double)f < 0.05) {
                this.autoRepair = true;
            } else if (f == 1.0F && this.autoRepair) {
                this.stopWatchMain.reset();
                this.autoRepair = false;
                this.expValid = false;
                return;
            }

            this.expValid = n3 >= n2 || n3 != 0 && this.expValid;
            if (Minecraft.player.getFoodStats().needFood()) {
                Slot slot4 = this.getFoodMaxSaturationSlot();
                if (!item2.equals(slot4.getStack().getItem())) {
                    if (mc.currentScreen instanceof ContainerScreen) {
                        Minecraft.player.closeScreen();
                        return;
                    }

                    this.clickSlot(slot4, 40, ClickType.SWAP, true);
                }

                (new Thread(AutoFarm::lambda$onUpdate$0)).start();
            } else if (Minecraft.player.inventory.getFirstEmptyStack() == -1) {
                if (!list2.contains(item2)) {
                    this.clickSlot(slot2, 40, ClickType.SWAP, true);
                    return;
                }

                Screen screen = mc.currentScreen;
                if (screen instanceof ContainerScreen) {
                    ContainerScreen containerScreen = (ContainerScreen)screen;
                    if (containerScreen.getTitle().getString().equals("● Выберите секцию")) {
                        this.clickSlotId(21, 0, ClickType.PICKUP, false);
                        return;
                    }

                    if (containerScreen.getTitle().getString().equals("Скупщик еды")) {
                        this.clickSlotId(item2.equals(Items.CARROT) ? 10 : 11, 0, ClickType.PICKUP, false);
                        return;
                    }
                }

                if (this.stopWatch.isReached(1000L)) {
                    Minecraft.player.sendChatMessage("/buyer");
                    this.stopWatch.reset();
                }
            } else if (this.autoRepair) {
                if (this.expValid) {
                    if (mc.currentScreen instanceof ContainerScreen) {
                        Minecraft.player.closeScreen();
                        this.stopWatchMain.reset();
                        return;
                    }

                    if (!item2.equals(Items.EXPERIENCE_BOTTLE)) {
                        this.clickSlot(slot, 40, ClickType.SWAP, true);
                    }

                    if (!list.contains(item)) {
                        this.clickSlot(slot3, Minecraft.player.inventory.currentItem, ClickType.SWAP, true);
                    }

                    Minecraft.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                } else if (this.stopWatch.isReached(800L)) {
                    Screen screen = mc.currentScreen;
                    if (screen instanceof ContainerScreen) {
                        ContainerScreen containerScreen = (ContainerScreen)screen;
                        if (containerScreen.getTitle().getString().contains("Пузырек опыта")) {
                            boolean bl = (Boolean)Minecraft.player.openContainer.inventorySlots.stream().filter(AutoFarm::lambda$onUpdate$1).filter((arg_0) -> this.lambda$onUpdate$2(n, arg_0)).filter(AutoFarm::lambda$onUpdate$3).min(Comparator.comparingInt(this::lambda$onUpdate$4)).map(this::lambda$onUpdate$5).orElse(false);
                            if (!bl) {
                                (new Thread(this::lambda$onUpdate$6)).start();
                            }

                            this.stopWatch.reset();
                            return;
                        }

                        if (containerScreen.getTitle().getString().contains("Подозрительная цена")) {
                            this.clickSlotId(0, 0, ClickType.QUICK_MOVE, false);
                            this.stopWatch.reset();
                            return;
                        }
                    }

                    Minecraft.player.sendChatMessage("/ah search Пузырёк Опыта");
                    this.stopWatch.reset();
                }
            } else {
                BlockPos blockPos = Minecraft.player.getPosition();
                if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.FARMLAND)) {
                    if (list.contains(item) && list2.contains(item2)) {
                        Minecraft.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(Minecraft.player.getPositionVec(), Direction.UP, blockPos, false)));
                        IntStream.range(0, 3).forEach((arg_0) -> lambda$onUpdate$7(blockPos, arg_0));
                        Minecraft.player.connection.sendPacket(new CPlayerDiggingPacket(Action.START_DESTROY_BLOCK, blockPos.up(), Direction.UP));
                    } else {
                        if (mc.currentScreen instanceof ContainerScreen) {
                            Minecraft.player.closeScreen();
                            this.stopWatchMain.reset();
                            return;
                        }

                        if (!list2.contains(item2)) {
                            this.clickSlot(slot2, 40, ClickType.SWAP, true);
                        }

                        if (!list.contains(item)) {
                            this.clickSlot(slot3, Minecraft.player.inventory.currentItem, ClickType.SWAP, true);
                        }
                    }
                }
            }

        }
    }

    public Slot getInventorySlot(Item item) {
        return (Slot)Minecraft.player.openContainer.inventorySlots.stream().filter((arg_0) -> lambda$getInventorySlot$8(item, arg_0)).findFirst().orElse((Slot) null);
    }

    public Slot getInventorySlot(List<Item> list) {
        return (Slot)Minecraft.player.openContainer.inventorySlots.stream().filter((arg_0) -> lambda$getInventorySlot$9(list, arg_0)).findFirst().orElse((Slot) null);
    }

    public Slot getFoodMaxSaturationSlot() {
        return (Slot)Minecraft.player.openContainer.inventorySlots.stream().filter(AutoFarm::lambda$getFoodMaxSaturationSlot$10).max(Comparator.comparingDouble(AutoFarm::lambda$getFoodMaxSaturationSlot$11)).orElse((Slot) null);
    }

    public int getInventoryCount(Item item) {
        return IntStream.range(0, 45).filter((arg_0) -> lambda$getInventoryCount$12(item, arg_0)).map(AutoFarm::lambda$getInventoryCount$13).sum();
    }

    public void clickSlot(Slot slot, int n, ClickType clickType, boolean bl) {
        if (slot != null) {
            this.clickSlotId(slot.slotNumber, n, clickType, bl);
        }

    }

    public void clickSlotId(int n, int n2, ClickType clickType, boolean bl) {
        if (bl) {
            Minecraft.player.connection.sendPacket(new CClickWindowPacket(Minecraft.player.openContainer.windowId, n, n2, clickType, ItemStack.EMPTY, Minecraft.player.openContainer.getNextTransactionID(Minecraft.player.inventory)));
        } else {
            mc.playerController.windowClick(Minecraft.player.openContainer.windowId, n, n2, clickType, Minecraft.player);
        }

    }

    public int getPrice(ItemStack itemStack) {
        CompoundNBT compoundNBT = itemStack.getTag();
        if (compoundNBT == null) {
            return 1;
        } else {
            String string = StringUtils.substringBetween(compoundNBT.toString(), "\"text\":\" $", "\"}]");
            if (string != null && !string.isEmpty()) {
                string = string.replaceAll(" ", "").replaceAll(",", "");
                return Integer.parseInt(string);
            } else {
                return 1;
            }
        }
    }

    private static int lambda$getInventoryCount$13(int n) {
        return Minecraft.player.inventory.getStackInSlot(n).getCount();
    }

    private static boolean lambda$getInventoryCount$12(Item item, int n) {
        return Minecraft.player.inventory.getStackInSlot(n).getItem().equals(item);
    }

    private static double lambda$getFoodMaxSaturationSlot$11(Slot slot) {
        return (double)slot.getStack().getItem().getFood().getSaturation();
    }

    private static boolean lambda$getFoodMaxSaturationSlot$10(Slot slot) {
        return slot.getStack().getItem().getFood() != null && !slot.getStack().getItem().getFood().canEatWhenFull();
    }

    private static boolean lambda$getInventorySlot$9(List list, Slot slot) {
        if (!list.contains(slot.getStack().getItem())) {
            return false;
        } else {
            return slot.slotNumber >= Minecraft.player.openContainer.inventorySlots.size() - 36;
        }
    }

    private static boolean lambda$getInventorySlot$8(Item item, Slot slot) {
        if (!slot.getStack().getItem().equals(item)) {
            return false;
        } else {
            return slot.slotNumber >= Minecraft.player.openContainer.inventorySlots.size() - 36;
        }
    }

    private static void lambda$onUpdate$7(BlockPos blockPos, int n) {
        Minecraft.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(Minecraft.player.getPositionVec(), Direction.UP, blockPos.up(), false)));
    }

    private void lambda$onUpdate$6() {
        try {
            int n = 124 + (int)(Math.random() * (double)133.0F);
            Thread.sleep((long)n);
            this.clickSlotId(49, 0, ClickType.PICKUP, false);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

    }

    private Boolean lambda$onUpdate$5(Slot slot) {
        this.clickSlot(slot, 0, ClickType.QUICK_MOVE, false);
        return true;
    }

    private int lambda$onUpdate$4(Slot slot) {
        return this.getPrice(slot.getStack()) / slot.getStack().getCount();
    }

    private static boolean lambda$onUpdate$3(Slot slot) {
        return slot.getStack().getCount() == 64;
    }

    private boolean lambda$onUpdate$2(int n, Slot slot) {
        return this.getPrice(slot.getStack()) <= n;
    }

    private static boolean lambda$onUpdate$1(Slot slot) {
        return slot.getStack().getTag() != null && slot.slotNumber < 45;
    }

    private static void lambda$onUpdate$0() {
        mc.gameSettings.keyBindUseItem.setPressed(false);

        try {
            while(Minecraft.player.getFoodStats().needFood()) {
                Thread.sleep(100L);
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } finally {
            mc.gameSettings.keyBindUseItem.setPressed(true);
        }

    }
}
