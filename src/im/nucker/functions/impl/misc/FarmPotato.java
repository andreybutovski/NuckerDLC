package im.nucker.functions.impl.misc;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeListSetting;

@FunctionRegister(name = "Фарм картошки", type = Category.Misc)
public class FarmPotato extends Function {
    private final Set<BlockPos> brokenBlocks = new HashSet<>();
    private final Map<BlockPos, Long> blockBreakingTimes = new HashMap<>();
    private final ModeListSetting elements = new ModeListSetting(
            "Что ломать?",
            new BooleanSetting("Картошка", true) // Ломать только картошку
    );
    private boolean running = false;
    private Thread nukerThread;

    public FarmPotato() {
        this.addSettings(this.elements);
    }

    @Override
    public boolean onEnable() {
        super.onEnable();
        this.running = true;
        this.nukerThread = new Thread(() -> {
            while (this.running) {
                if (mc == null || mc.player == null || mc.world == null) continue;
                this.nukeBlocks();
                this.clearBrokenBlocks();
                try {
                    Thread.sleep(1L); // Минимальная задержка для ускорения
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.nukerThread.start();
        return true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.running = false;
        if (this.nukerThread != null) {
            this.nukerThread.interrupt();
        }
    }

    private void clearBrokenBlocks() {
        long currentTime = System.currentTimeMillis();
        brokenBlocks.removeIf(pos -> currentTime - blockBreakingTimes.getOrDefault(pos, currentTime) >= 400); // Уменьшено время ожидания
    }

    private double getDistanceSquared(BlockPos pos1, BlockPos pos2) {
        double dx = pos1.getX() - pos2.getX();
        double dy = pos1.getY() - pos2.getY();
        double dz = pos1.getZ() - pos2.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private void nukeBlocks() {
        if (mc != null && mc.world != null && mc.player != null) {
            BlockPos playerPos = new BlockPos(mc.player.getPosition());
            int rangeValue = 4; // Увеличен радиус поиска до 4 блоков
            List<BlockPos> blockPositions = new ArrayList<>();

            for (int x = -rangeValue; x <= rangeValue; x++) {
                for (int y = -1; y <= 1; y++) {  // Поиск на том же уровне
                    for (int z = -rangeValue; z <= rangeValue; z++) {
                        BlockPos blockPos = playerPos.add(x, y, z);
                        Block block = mc.world.getBlockState(blockPos).getBlock();

                        // Проверяем картошку
                        if (((Boolean) this.elements.getValueByName("Картошка").get()) && block == Blocks.POTATOES) {

                            Block blockBelow = mc.world.getBlockState(blockPos.down()).getBlock();
                            if (blockBelow == Blocks.FARMLAND) {
                                blockPositions.add(blockPos);
                            }
                        }
                    }
                }
            }

            blockPositions.removeIf(this.brokenBlocks::contains);
            blockPositions.sort(Comparator.comparingDouble(pos -> this.getDistanceSquared(pos, playerPos)));

            List<BlockPos> blocksToBreak = blockPositions.subList(0, Math.min(1, blockPositions.size()));  // 1 блок

            blocksToBreak.forEach(blockToBreak -> {
                if (!this.brokenBlocks.contains(blockToBreak)) {
                    try {
                        // 1. Ставим картошку (с левой руки) на грядку
                        mc.player.connection.sendPacket(
                                new CPlayerTryUseItemOnBlockPacket(
                                        Hand.OFF_HAND, // Левую руку для посадки
                                        new BlockRayTraceResult(new Vector3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                )
                        );

                        Thread.sleep(15); // Минимальная задержка для посадки

                        for (int i = 0; i < 3; i++) {
                            mc.player.connection.sendPacket(
                                    new CPlayerTryUseItemOnBlockPacket(
                                            Hand.MAIN_HAND, // Правую руку для ПКМ
                                            new BlockRayTraceResult(new Vector3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                    )
                            );
                            Thread.sleep(5); // Уменьшена задержка между кликами правой рукой
                        }

                        Thread.sleep(40); // Минимальная задержка перед ломанием

                        mc.player.connection.sendPacket(
                                new CPlayerDiggingPacket(
                                        CPlayerDiggingPacket.Action.START_DESTROY_BLOCK,
                                        blockToBreak,
                                        Direction.UP
                                )
                        );

                        Thread.sleep(3);

                        mc.player.connection.sendPacket(
                                new CPlayerTryUseItemOnBlockPacket(
                                        Hand.OFF_HAND,
                                        new BlockRayTraceResult(new Vector3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                )
                        );

                        this.brokenBlocks.add(blockToBreak);
                        this.blockBreakingTimes.put(blockToBreak, System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
