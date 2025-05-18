package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector2f;

@FunctionRegister(name = "ShulkerShower", type = Category.Render)
public class ShulkerChecker extends Function {
    private GlStateManager GlStateManager;

    @Subscribe
    public void onRender(EventDisplay e) {
        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof PlayerEntity)) continue;

            PlayerEntity player = (PlayerEntity) entity;
            if (player.getName().equals(mc.player.getName())) continue;

            ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
            if (!(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock)) continue;

            CompoundNBT tag = stack.getTag();
            if (tag == null || !tag.contains("BlockEntityTag", 10)) continue;

            CompoundNBT blocksTag = tag.getCompound("BlockEntityTag");
            if (!blocksTag.contains("Items", 9)) continue;

            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(blocksTag, items);

            if (items.isEmpty()) continue;

            GlStateManager.pushMatrix();
            Direction direction = mc.player.getHorizontalFacing();
            Vector2f vec = ProjectionUtil.project(
                    (float) player.getPosX() - (direction.equals(Direction.NORTH) || direction.equals(Direction.WEST) ? (player.getWidth() * 3) : -(player.getWidth() * 3)),
                    (float) player.getPosY() + player.getHeight() + 1.25f,
                    (float) player.getPosZ()
            );
            double scale = MathUtil.getScale(player.getPositionVec(), 0.2f);
            float startX = vec.x;
            float startY = vec.y;
            float posX = startX;
            float posY = startY;

            GlStateManager.translated(startX, startY, 0);
            GlStateManager.scaled(scale, scale, scale);
            DisplayUtils.drawRoundedRect(0, 0, (20 * 9f) + 4.5f, (20 * 3) + 1.5f, 3, ColorUtils.rgb(50, 50, 50));

            for (ItemStack item : items) {
                mc.getItemRenderer().renderItemAndEffectIntoGUI(item, (int) (posX - startX), (int) (posY - startY));
                mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, item, (int) (posX - startX), (int) (posY - startY), null);

                posX += 20;
                if (posX >= startX + 20 * 9f) {
                    posX = startX;
                    posY += 20;
                }
            }

            GlStateManager.popMatrix();
        }
    }
}
