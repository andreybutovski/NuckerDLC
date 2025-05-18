package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.SliderSetting;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;

@FunctionRegister(
        name = "Nuker",
        type = Category.Misc
)
public class Nuker extends Function {
    final SliderSetting range1 = new SliderSetting("Диапозон", 2.0F, 1.0F, 5.0F, 0.1F);
    long last = 0L;
    final Set<Block> blocks = new HashSet();
    BlockPos Render = null;
    BlockPos block = null;
    float interval = 1.0F;

    public Nuker() {
        this.addSettings(new Setting[]{this.range1});
        this.blocks.add(Blocks.COAL_ORE);
        this.blocks.add(Blocks.IRON_ORE);
        this.blocks.add(Blocks.GOLD_ORE);
        this.blocks.add(Blocks.LAPIS_ORE);
        this.blocks.add(Blocks.DIAMOND_ORE);
        this.blocks.add(Blocks.NETHERITE_BLOCK);
        this.blocks.add(Blocks.ANCIENT_DEBRIS);
        this.blocks.add(Blocks.REDSTONE_ORE);
    }

    protected float[] rotations(PlayerEntity player) {
        return new float[0];
    }

    @Subscribe
    private void onWorld(WorldEvent worldEvent) {
        int range = Math.round((Float)this.range1.get());
        long scan = (long)Math.round(this.interval);
        Minecraft var10000 = mc;
        Vector3d positionVec = Minecraft.player.getPositionVec();
        if (this.block != null) {
            var10000 = mc;
            if (Minecraft.world.getBlockState(this.block).getBlock() != Blocks.AIR) {
                if (this.block != null) {
                    double distance = positionVec.distanceTo(new Vector3d((double)this.block.getX(), (double)this.block.getY(), (double)this.block.getZ()));
                    if (distance > (double)range) {
                        this.block = null;
                        this.Render = null;
                    } else if (System.currentTimeMillis() - this.last >= scan) {
                        mc.playerController.onPlayerDamageBlock(this.block, Direction.UP);
                        this.last = System.currentTimeMillis();
                    }
                }

                if (this.block != null) {
                    var10000 = mc;
                    if (Minecraft.world.getBlockState(this.block).getBlock() == Blocks.AIR) {
                        this.block = null;
                        this.Render = null;
                        return;
                    }
                }

                return;
            }
        }

        for(int x = -range; x <= range; ++x) {
            for(int z = -range; z <= range; ++z) {
                for(int y = -4; y <= 4; ++y) {
                    BlockPos target = new BlockPos(positionVec.x + (double)x, positionVec.y + (double)y, positionVec.z + (double)z);
                    if (target != null) {
                        var10000 = mc;
                        Block pos = Minecraft.world.getBlockState(target).getBlock();
                        if (this.blocks.contains(pos)) {
                            BlockState var16 = pos.getDefaultState();
                            Minecraft var10001 = mc;
                            if (var16.getBlockHardness(Minecraft.world, target) > 0.0F) {
                                double distance = positionVec.distanceTo(new Vector3d((double)target.getX(), (double)target.getY(), (double)target.getZ()));
                                if (distance <= (double)range && System.currentTimeMillis() - this.last >= scan) {
                                    mc.playerController.onPlayerDamageBlock(target, Direction.UP);
                                    this.last = System.currentTimeMillis();
                                    this.block = target;
                                    this.Render = target;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Subscribe
    private void render(WorldEvent world) {
        if (this.Render != null) {
            RenderUtils.drawBlockBox(this.Render, -65536);
        }

    }
}

