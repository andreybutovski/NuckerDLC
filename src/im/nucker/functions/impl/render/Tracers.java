package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.command.friends.FriendStorage;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.impl.combat.AntiBot;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.utils.EntityUtils;
import im.nucker.utils.render.ColorUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;

import static org.lwjgl.opengl.GL11.*;

@FunctionRegister(name = "Tracers", type = Category.Render)
public class Tracers extends Function {
    private final BooleanSetting ignoreNaked = new BooleanSetting("Игнорировать голых", true);

    public Tracers() {
        addSettings(ignoreNaked);
    }

    @Subscribe
    public void onRender(WorldEvent e) {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        glLineWidth(1);

        Vector3d cam = new Vector3d(0, 0, 150)
                .rotatePitch((float) -(Math.toRadians(mc.getRenderManager().info.getPitch())))
                .rotateYaw((float) -Math.toRadians(mc.getRenderManager().info.getYaw()));

        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (!player.isAlive()
                    || AntiBot.isBot(player)
                    || player.getTotalArmorValue() == 0.0f && ignoreNaked.get()) continue;

            Vector3d pos = EntityUtils.getInterpolatedPositionVec(player)
                    .subtract(mc.getRenderManager().info.getProjectedView());

            ColorUtils.setColor(FriendStorage.isFriend(player.getGameProfile().getName()) ? FriendStorage.getColor() : -1);

            buffer.begin(1, DefaultVertexFormats.POSITION);

            buffer.pos(cam.x, cam.y, cam.z).endVertex();
            buffer.pos(pos.x, pos.y, pos.z).endVertex();


            tessellator.draw();
        }

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glPopMatrix();
    }
}
