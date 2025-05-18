package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockRayTraceResult;

@FunctionRegister(
        name = "FastBreak",
        type = Category.Misc
)
public class FastBreak extends Function {
    private long lastBreakTime = 0L;
    private final float interval = 0.07F; // Минимальная задержка между ударами

    public FastBreak() {}

    @Subscribe
    private void onWorld(WorldEvent worldEvent) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) { // Проверяем зажатие ЛКМ
            return;
        }

        if (mc.objectMouseOver == null || mc.objectMouseOver.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockRayTraceResult blockHit = (BlockRayTraceResult) mc.objectMouseOver;
        BlockPos targetBlock = blockHit.getPos(); // Получаем блок, на который смотрит игрок

        if (Minecraft.world.getBlockState(targetBlock).getBlock() == Blocks.AIR) {
            return;
        }

        if (System.currentTimeMillis() - lastBreakTime >= (long) (interval * 500)) {
            mc.playerController.onPlayerDamageBlock(targetBlock, Direction.UP);
            lastBreakTime = System.currentTimeMillis();
        }
    }
}
