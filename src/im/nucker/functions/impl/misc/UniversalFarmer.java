package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

@FunctionRegister(name = "НЕ РАБОТАЕТ", type = Category.Misc)

public class UniversalFarmer extends Function {

    private final Minecraft mc = Minecraft.getInstance();
    private BlockPos startPosition = null;

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        if (startPosition == null) {
            startPosition = mc.player.getPosition();
        }

        BlockPos carrotPos = findMatureCarrot();
        if (carrotPos != null) {
            harvestAndReplant(carrotPos);
        } else {
            returnToStart();
        }
    }

    private BlockPos findMatureCarrot() {
        World world = mc.world;
        PlayerEntity player = mc.player;
        int radius = 5;

        List<BlockPos> carrotPositions = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = player.getPosition().add(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof CropsBlock) {
                        CropsBlock crop = (CropsBlock) block;
                        if (crop.isMaxAge(state)) {
                            carrotPositions.add(pos);
                        }
                    }
                }
            }
        }
        return carrotPositions.isEmpty() ? null : carrotPositions.get(0);
    }

    private void harvestAndReplant(BlockPos pos) {
        PlayerEntity player = mc.player;
        mc.playerController.clickBlock(pos, player.getHorizontalFacing());
        mc.player.swingArm(Hand.MAIN_HAND);

        if (hasCarrotSeeds()) {
            mc.playerController.processRightClickBlock(player, mc.world, pos, player.getHorizontalFacing(), player.getLookVec(), Hand.MAIN_HAND);
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private boolean hasCarrotSeeds() {
        for (ItemStack stack : mc.player.inventory.mainInventory) {
            if (stack.getItem() == Items.CARROT) {
                return true;
            }
        }
        return false;
    }

    private void returnToStart() {
        if (startPosition != null) {
            mc.player.getNavigator().tryMoveToXYZ(startPosition.getX(), startPosition.getY(), startPosition.getZ(), 1.2);
        }
    }
}