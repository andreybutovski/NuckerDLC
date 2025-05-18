package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FunctionRegister(name = "AutoFurnace", type = Category.Player)
public class AutoFurnace extends Function {
    private final List<BlockPos> foundFurnaces = new ArrayList<>();
    private int currentFurnaceIndex = 0;
    private boolean isCooking = false;

    private final List<Item> rawFoods = Arrays.asList(
            Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON, Items.SALMON, Items.COD, Items.RABBIT
    );

    private final List<Item> fuels = Arrays.asList(
            Items.COAL, Items.CHARCOAL, Items.LAVA_BUCKET, Items.STICK,
            Items.OAK_PLANKS, Items.BIRCH_PLANKS, Items.SPRUCE_PLANKS
    );

    public AutoFurnace() {}

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (foundFurnaces.isEmpty()) {
            findAllFurnaces();
            if (foundFurnaces.isEmpty()) return;
        }

        if (currentFurnaceIndex >= foundFurnaces.size()) {
            currentFurnaceIndex = 0;
        }

        BlockPos furnacePos = foundFurnaces.get(currentFurnaceIndex);

        if (mc.player.openContainer instanceof FurnaceContainer) {
            FurnaceContainer furnace = (FurnaceContainer) mc.player.openContainer;

            if (!isCooking) {
                addItemsToFurnace(furnace);
                isCooking = true;
            } else {
                takeCookedFood(furnace);
            }
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, furnacePos, mc.player.getHorizontalFacing(), mc.player.getPositionVec(), Hand.MAIN_HAND);
        }
    }

    private void findAllFurnaces() {
        foundFurnaces.clear();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.FURNACE) {
                        foundFurnaces.add(pos);
                    }
                }
            }
        }
    }

    private void addItemsToFurnace(FurnaceContainer furnace) {
        if (!addItemToSlot(furnace, 0, rawFoods)) return;
        addItemToSlot(furnace, 1, fuels);
    }

    private void takeCookedFood(FurnaceContainer furnace) {
        ItemStack outputSlot = furnace.getSlot(2).getStack();
        if (!outputSlot.isEmpty()) {
            mc.playerController.windowClick(furnace.windowId, 2, 0, ClickType.PICKUP, mc.player);
            isCooking = false;
            currentFurnaceIndex++;
        }
    }

    private boolean addItemToSlot(FurnaceContainer furnace, int slot, List<Item> items) {
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (items.contains(stack.getItem())) {
                mc.playerController.windowClick(furnace.windowId, i, slot, ClickType.QUICK_MOVE, mc.player);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        foundFurnaces.clear();
        currentFurnaceIndex = 0;
        isCooking = false;
        super.onDisable();
    }
}

