package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.optifine.render.RenderUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@FunctionRegister(name = "AncientXray", type = Category.Misc)
public class AncientXray extends Function {

    private final Map<TileEntityType<?>, Integer> tiles = new HashMap<>();
    private final Map<BlockState, Integer> blocks = new HashMap<>();

    public AncientXray() {
        addBlock(Blocks.DIAMOND_ORE.getDefaultState(), new Color(0, 123, 255).getRGB());
        addBlock(Blocks.ANCIENT_DEBRIS.getDefaultState(), new Color(255, 255, 255).getRGB());
    }

    private void addBlock(BlockState blockState, int color) {
        blocks.put(blockState, color);
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        for (TileEntity tile : mc.world.loadedTileEntityList) {
            TileEntityType<?> type = tile.getType();
            if (tiles.containsKey(type)) {
                BlockPos pos = tile.getPos();
                RenderUtils.drawBlockBox(pos, tiles.get(type));
            }
        }

        for (BlockPos pos : BlockPos.getAllInBoxMutable(mc.player.getPosition().add(-32, -32, -32),
                mc.player.getPosition().add(32, 32, 32))) {
            BlockState state = mc.world.getBlockState(pos);
            if (blocks.containsKey(state)) {
                RenderUtils.drawBlockBox(pos, blocks.get(state));
            }
        }
    }
}