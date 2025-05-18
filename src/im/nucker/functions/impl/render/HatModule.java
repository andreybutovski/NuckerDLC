package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

@FunctionRegister(name = "Hat Module", type = Category.Render)
public class HatModule extends Function {
    private final ModeSetting type = new ModeSetting("Тип", "Шляпа", "Шляпа", "Призраки");
    private long lastTime = System.currentTimeMillis();
    public HatModule() {
        addSettings(type);
    }
    @Subscribe
    private void onRender(WorldEvent e) {
        if (this.type.is("Шляпа")) {
            if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) return;
            float radius = 0.6f;

            GlStateManager.pushMatrix();

            RenderSystem.translated(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z);
            Vector3d interpolated = MathUtil.interpolate(mc.player.getPositionVec(), new Vector3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ), e.getPartialTicks());
            interpolated.y -= 0.05f;
            RenderSystem.translated(interpolated.x, interpolated.y + mc.player.getHeight(), interpolated.z);
            final double pitch = mc.getRenderManager().info.getPitch();
            final double yaw = mc.getRenderManager().info.getYaw();

            GL11.glRotatef((float) -yaw, 0f, 1f, 0f);

            RenderSystem.translated(-interpolated.x, -(interpolated.y + mc.player.getHeight()), -interpolated.z);

            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.shadeModel(7425);
            RenderSystem.lineWidth(3);


            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(interpolated.x, interpolated.y + mc.player.getHeight() + 0.3, interpolated.z).color(ColorUtils.setAlpha(HUD.getColor(0, 1), 128)).endVertex();
            for (int i = 0; i <= 360; i++) {

                float x = (float) (interpolated.x + MathHelper.sin((float) Math.toRadians(i)) * radius);
                float z = (float) (interpolated.z + -MathHelper.cos((float) Math.toRadians(i)) * radius);

                buffer.pos(x, interpolated.y + mc.player.getHeight(), z).color(ColorUtils.setAlpha(HUD.getColor(i, 1), 128)).endVertex();
            }
            tessellator.draw();
            buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 360; i++) {

                float x = (float) (interpolated.x + MathHelper.sin((float) Math.toRadians(i)) * radius);
                float z = (float) (interpolated.z + -MathHelper.cos((float) Math.toRadians(i)) * radius);

                buffer.pos(x, interpolated.y + mc.player.getHeight(), z).color(ColorUtils.setAlpha(HUD.getColor(i, 1), 255)).endVertex();
            }
            tessellator.draw();
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.shadeModel(7424);
            GlStateManager.popMatrix();
        }
        if (this.type.is("Призраки")) {
            MatrixStack stack = new MatrixStack();
            EntityRendererManager rm = mc.getRenderManager();
            float c = (float) ((((System.currentTimeMillis() - lastTime) / 2000F)) + (Math.sin((((System.currentTimeMillis() - lastTime) / 1500F))) / 10f));

            double x = MathHelper.interpolate(mc.getRenderPartialTicks(), mc.player.lastTickPosX, mc.player.getPosX());
            double y = MathHelper.interpolate(mc.getRenderPartialTicks(), mc.player.lastTickPosY, mc.player.getPosY()) + mc.player.getHeight() / 1.35f;
            double z = MathHelper.interpolate(mc.getRenderPartialTicks(), mc.player.lastTickPosZ, mc.player.getPosZ());


            x -= rm.info.getProjectedView().getX();
            y -= rm.info.getProjectedView().getY();
            z -= rm.info.getProjectedView().getZ();
            float alpha = Shaders.shaderPackLoaded ? 1f : 0.5f;
            alpha *= 0.3;

            float pl = 0;

            boolean fa = true;
            for (int b = 0; b < 2; b++) {
                for (float i = c * 360; i < c * 360 + 70; i += 4) {
                    float cur = i;
                    float min = c * 360;
                    float max = c * 360 + 140;
                    float dc = MathHelper.normalize(cur, c * 360 - 70, max);
                    float degrees = i;
                    int color = HUD.getColor(0);
                    int color2 = HUD.getColor(90);
                    if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                        color -= ColorUtils.rgb(0,0,0);
                        color2 -= ColorUtils.rgb(0,0,0);
                    }

                    float rf = 0.40f;
                    double radians = Math.toRadians(degrees);
                    double plY = pl + Math.sin(radians * 1f) * 0.07f;

                    stack.push();
                    stack.translate(x, y, z);
                    stack.rotate(mc.getRenderManager().info.getRotation());
                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.15f : 0.15f) * (Math.max(fa ? 0.15f : 0.15f, fa ? dc : (1f - -(0.4f - dc)) / 2f) + 0.45f);
                    float w = q * (1.7f + ((0.5f - alpha) * 2));
                    DisplayUtils.drawImage1(stack,
                            new ResourceLocation("expensive/images/glow.png"),
                            Math.cos(radians) * rf - w / 2f,
                            plY + 0.30,
                            Math.sin(radians) * rf - w / 2f, w, w,
                            color,
                            color2,
                            color2,
                            color);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    stack.pop();
                }
                c *= -1f;
                fa = !fa;
                pl += 0f;
            }
        }
    }
}