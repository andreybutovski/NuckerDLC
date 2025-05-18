package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;

import java.util.HashMap;
import java.util.Map;

@FunctionRegister(name = "VulcanESP", type = Category.Render)
public class VulcanESP extends Function {
    private final HashMap<Entity, Vector4f> positions = new HashMap<>();
    private final ModeSetting modeSetting = new ModeSetting("Mode", "All", "", "", "", "", "All");

    public VulcanESP() {
        addSettings(modeSetting);
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        positions.clear();
        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity itemEntity)) continue;

            ItemStack itemStack = itemEntity.getItem();
            boolean isDiamond = itemStack.getItem() == Items.DIAMOND;
            boolean isPlayerHead = itemStack.getItem() == Items.PLAYER_HEAD;
            boolean isTotem = itemStack.getItem() == Items.TOTEM_OF_UNDYING;
            boolean isTripwireHook = itemStack.getItem() == Items.TRIPWIRE_HOOK;
            boolean isNetheriteIngot = itemStack.getItem() == Items.NETHERITE_INGOT;
            boolean isNetheriteSword = itemStack.getItem() == Items.NETHERITE_SWORD;
            boolean isNetheritePickaxe = itemStack.getItem() == Items.NETHERITE_PICKAXE;

            if (!isEntityVisible(isDiamond, isPlayerHead, isTotem, isTripwireHook, isNetheriteIngot, isNetheritePickaxe, isNetheriteSword)) continue;

            double x = MathUtil.interpolate(entity.getPosX(), entity.lastTickPosX, e.getPartialTicks());
            double y = MathUtil.interpolate(entity.getPosY(), entity.lastTickPosY, e.getPartialTicks());
            double z = MathUtil.interpolate(entity.getPosZ(), entity.lastTickPosZ, e.getPartialTicks());

            Vector3d size = new Vector3d(entity.getBoundingBox().maxX - entity.getBoundingBox().minX, entity.getBoundingBox().maxY - entity.getBoundingBox().minY, entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ);

            AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 1.5f, y, z - size.z / 1.5f, x + size.x / 1.5f, y + size.y + 0.1f, z + size.z / 1.5f);
            Vector4f position = null;

            for (int i = 0; i < 8; i++) {
                Vector2f vector = ProjectionUtil.project(i % 2 == 0 ? aabb.minX : aabb.maxX, (i / 2) % 2 == 0 ? aabb.minY : aabb.maxY, (i / 4) % 2 == 0 ? aabb.minZ : aabb.maxZ);

                if (position == null) {
                    position = new Vector4f(vector.x, vector.y, 1, 1.0f);
                } else {
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
            }

            positions.put(entity, position);
        }

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        for (Map.Entry<Entity, Vector4f> entry : positions.entrySet()) {
            Vector4f position = entry.getValue();
            ItemEntity itemEntity = (ItemEntity) entry.getKey();
            ItemStack itemStack = itemEntity.getItem();

            int color = 0;
            String itemName = itemStack.getItem().getName().getString(); // Получаем название предмета

            if (itemEntity instanceof ItemEntity) {
                if (itemStack.getItem() == Items.DIAMOND) {
                    color = ColorUtils.rgba(0, 0, 255, 255);
                } else if (itemStack.getItem() == Items.PLAYER_HEAD) {
                    color = ColorUtils.rgba(255, 0, 0, 255);
                } else if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                    color = ColorUtils.rgba(255, 128, 0, 255);
                } else if (itemStack.getItem() == Items.TRIPWIRE_HOOK) {
                    color = ColorUtils.rgba(128, 0, 128, 255);
                } else if (itemStack.getItem() == Items.NETHERITE_INGOT) {
                    color = ColorUtils.rgba(0, 255, 0, 255);
                } else if (itemStack.getItem() == Items.NETHERITE_PICKAXE) {
                    color = ColorUtils.rgba(255, 0, 127, 255);
                } else if (itemStack.getItem() == Items.NETHERITE_SWORD) {
                    color = ColorUtils.rgba(102, 255, 255, 255);
                }

                DisplayUtils.drawBox(position.x, position.y, position.z, position.w, 1.5F, color);
                MatrixStack.drawString(itemName, position.x + (position.z - position.x) / 2, position.y - 10, 0xFFFFFF);
            }
        }

        Tessellator.getInstance().draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private boolean isEntityVisible(boolean isDiamond, boolean isPlayerHead, boolean isTotem, boolean isTripwireHook, boolean isNetheriteIngot, boolean isNetheritePickaxe, boolean isNetheriteSword) {
        switch (modeSetting.getName()) {
            case "Diamonds":
                return isDiamond;
            case "Player_Heads":
                return isPlayerHead;
            case "Totems":
                return isTotem;
            case "NetheriteIngot":
                return isNetheriteIngot;
            case "NetheritePickaxe":
                return isNetheritePickaxe;
            case "NetheriteSword":
                return isNetheriteSword;
            case "TRIPWIRE_HOOK":
                return isTripwireHook;
            case "All":
            default:
                return isDiamond || isPlayerHead || isTotem || isTripwireHook || isNetheriteIngot || isNetheritePickaxe || isNetheriteSword;
        }
    }
}
