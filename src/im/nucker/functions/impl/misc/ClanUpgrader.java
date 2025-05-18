package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.api.Function;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;

import java.lang.reflect.Field;

@FunctionRegister(name = "ClanUpgrader", type = im.nucker.functions.api.Category.Misc)
public class ClanUpgrader extends Function {
    private static final Minecraft mc = Minecraft.getInstance();
    private static int REDSTONE_SLOT = -1;

    public ClanUpgrader() {
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        if (mc.world != null && mc.player != null) {
            // Проверка, если редстоун не найден, пытаемся его найти
            if (REDSTONE_SLOT == -1) {
                REDSTONE_SLOT = findRedstoneSlot();
                if (REDSTONE_SLOT == -1) {
                    // Автоматически находим редстоун, если его нет в горячей строке
                    REDSTONE_SLOT = findRedstoneInInventory();
                    if (REDSTONE_SLOT == -1) {
                        System.out.println("No redstone found in inventory");
                        return;
                    }
                }
            }

            BlockPos playerPos = mc.player.getPosition();
            BlockPos blockBelow = playerPos.down();

            // Автопрыжок: если игрок на земле, он будет прыгать
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }

            // Если блок под игроком пуст, то ставим редстоун
            if (mc.world.getBlockState(blockBelow).isAir()) {
                mc.player.rotationPitch = 90.0F;
                this.placeRedstoneDust(blockBelow);
            } else if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.REDSTONE_WIRE) {
                // Разрушаем редстоун, если он уже есть
                this.breakRedstone(blockBelow);
            }
        }
    }

    private void placeRedstoneDust(BlockPos blockPos) {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            // Переключаем на слот с редстоуном, если это не текущий слот
            if (player.inventory.currentItem != REDSTONE_SLOT) {
                player.inventory.currentItem = REDSTONE_SLOT;
            }

            RayTraceResult rayTrace = mc.objectMouseOver;
            if (rayTrace != null && rayTrace.getType() == Type.BLOCK) {
                BlockPos targetPos = ((BlockRayTraceResult) rayTrace).getPos();
                Direction targetFace = ((BlockRayTraceResult) rayTrace).getFace();
                BlockRayTraceResult blockRay = new BlockRayTraceResult(new Vector3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()), targetFace, targetPos, false);

                // Установка редстоунов в быстром цикле
                for (int i = 0; i < 20; i++) {
                    mc.playerController.processRightClickBlock(player, mc.world, Hand.MAIN_HAND, blockRay);
                }
            }
        }
    }

    private void breakRedstone(BlockPos blockPos) {
        ClientPlayerEntity player = mc.player;
        // Разрушение редстоунов в быстром цикле
        for (int i = 0; i < 20; i++) {
            mc.playerController.onPlayerDamageBlock(blockPos, player.getHorizontalFacing());
        }
    }

    // Функция для поиска редстоуна в горячей строке
    private static int findRedstoneSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.REDSTONE) {
                return i;
            }
        }
        return -1;
    }

    // Функция для поиска редстоуна в инвентаре
    private static int findRedstoneInInventory() {
        for (int i = 9; i < mc.player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.REDSTONE) {
                // Перемещаем редстоун в горячую строку
                mc.player.inventory.currentItem = i % 9;
                return i % 9;  // Возвращаем слот для горячей строки
            }
        }
        return -1;
    }

    public boolean onEnable() {
        super.onEnable();
        setRightClickDelay(0); // Уменьшаем задержку клика до 0
        return false;
    }

    public void onDisable() {
        super.onDisable();
        setRightClickDelay(4); // Восстанавливаем стандартную задержку
    }

    // Использование рефлексии для изменения rightClickDelayTimer
    private void setRightClickDelay(int value) {
        try {
            Field rightClickDelayTimerField = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            rightClickDelayTimerField.setAccessible(true); // Даем доступ к приватному полю
            rightClickDelayTimerField.setInt(mc, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

