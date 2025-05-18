package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.command.friends.FriendStorage;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

@FunctionRegister(name = "Name - Tags", type = Category.Render)
public class Tags extends Function {
    private static final HashMap<Entity, Vector4i> positions = new HashMap<>();
    private static final BooleanSetting showHealth = new BooleanSetting("Показывать ХП", true);
    private static final BooleanSetting showArmor = new BooleanSetting("Показывать броню", true);
    private static final BooleanSetting hideNametagNearPlayer = new BooleanSetting("Убирать неймтег рядом", true);
    private static final BooleanSetting showItemName = new BooleanSetting("Показывать имя предмета", true);

    public Tags() {
        addSettings(showHealth, showArmor, hideNametagNearPlayer, showItemName);
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (mc.world == null || e.getType() != EventDisplay.Type.PRE) return;

        positions.clear();

        mc.world.getAllEntities().forEach(entity -> {
            if (entity instanceof PlayerEntity || entity instanceof ItemEntity) {
                Vector4i position = projectAABB(entity.getBoundingBox());
                if (position != null) positions.put(entity, position);
            }
        });

        positions.forEach((entity, position) -> {
            if (entity instanceof LivingEntity living) {
                if (hideNametagNearPlayer.get() && mc.player.getDistanceSq(entity) < 36.0) return;
                if (showArmor.get()) renderArmor(e.getMatrixStack(), living, position);
                renderNametag(e.getMatrixStack(), living, position);
            } else if (entity instanceof ItemEntity itemEntity && showItemName.get()) {
                renderItemNametag(e.getMatrixStack(), itemEntity, position);
            }
        });
    }

    private void renderItemNametag(MatrixStack matrixStack, ItemEntity itemEntity, Vector4i position) {
    }

    private static Vector4i projectAABB(AxisAlignedBB aabb) {
        Vector4i position = null;
        for (int i = 0; i < 8; i++) {
            Vector2f vector = ProjectionUtil.project(
                    (i & 1) == 0 ? aabb.minX : aabb.maxX,
                    (i & 2) == 0 ? aabb.minY : aabb.maxY,
                    (i & 4) == 0 ? aabb.minZ : aabb.maxZ
            );
            if (position == null) {
                position = new Vector4i(vector.x, vector.y, vector.x, vector.y);
            } else {
                position.x = Math.min(position.x, vector.x);
                position.y = Math.min(position.y, vector.y);
                position.z = Math.max(position.z, vector.x);
                position.w = Math.max(position.w, vector.y);
            }
        }
        return position;
    }

    private static void renderNametag(MatrixStack stack, LivingEntity entity, Vector4i position) {
        String name = entity.getName().getString();
        float health = entity.getHealth();
        int ping = getPing(entity);
        String info = String.format("%s | HP: %.1f | Ping: %dms", name, health, ping);
        float midX = (position.x + position.z) / 2f;
        float nameWidth = Fonts.montserrat.getWidth(info, 8) + 10;
        DisplayUtils.drawRect(stack, midX - nameWidth / 2, position.y - 10, midX + nameWidth / 2, position.y + 2, 0x90000000);
        Fonts.montserrat.drawText(stack, info, midX - Fonts.montserrat.getWidth(info, 8) / 2, position.y - 8, -1, 8, 0.05f);
    }

    private static void renderArmor(MatrixStack stack, LivingEntity entity, Vector4i position) {
        float midX = (position.x + position.z) / 2f;
        float yOffset = position.y - 20;
        float posX = midX - 20;
        for (ItemStack armorItem : entity.getArmorInventoryList()) {
            if (!armorItem.isEmpty()) {
                GL11.glPushMatrix();
                glCenteredScale(posX, yOffset, 10, 10, 0.5f);
                mc.getItemRenderer().renderItemAndEffectIntoGUI(armorItem, (int) posX, (int) yOffset);
                GL11.glPopMatrix();
                posX += 14;
            }
        }
    }

    private static int getPing(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(player.getGameProfile().getId());
            return info != null ? info.getResponseTime() : 0;
        }
        return 0;
    }

    private static class Vector4i {
        public float x, y, z, w;
        public Vector4i(float x, float y, float z, float w) {
            this.x = x; this.y = y; this.z = z; this.w = w;
        }
    }

    private static void glCenteredScale(final float x, final float y, final float w, final float h, final float f) {
        glTranslatef(x + w / 2, y + h / 2, 0);
        glScalef(f, f, 1);
        glTranslatef(-x - w / 2, -y - h / 2, 0);
    }
}