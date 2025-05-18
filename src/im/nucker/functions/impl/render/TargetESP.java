package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.impl.combat.KillAura;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.animations.Animation;
import im.nucker.utils.animations.Direction;
import im.nucker.utils.animations.impl.DecelerateAnimation;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.math.Vector4i;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;


import static com.mojang.blaze3d.platform.GlStateManager.GL_QUADS;
import static com.mojang.blaze3d.systems.RenderSystem.depthMask;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;

@FunctionRegister(name = "TargetESP", type = Category.Render)
public class TargetESP extends Function {
    private final ModeSetting type = new ModeSetting("Тип", "Ромб", "Ромб", "Ромб", "Кольцо", "Призраки");
    private final Animation alpha = new DecelerateAnimation(600, 255);
    private LivingEntity currentTarget;
    public static LivingEntity target = null;
    private final KillAura killAura;
    private double speed;
    private long lastTime = System.currentTimeMillis();

    public static long startTime = System.currentTimeMillis();

    public TargetESP(KillAura killAura) {
        this.killAura = killAura;
        addSettings(type);
    }

    public double getScale(Vector3d position, double size) {
        Vector3d cam = mc.getRenderManager().info.getProjectedView();
        double distance = cam.distanceTo(position);
        double fov = mc.gameRenderer.getFOVModifier(mc.getRenderManager().info, mc.getRenderPartialTicks(), true);

        return Math.max(10f, 1000 / distance) * (size / 30f) / (fov == 70 ? 1 : fov / 70.0f);
    }

    @Subscribe
    private void onUpdate(EventUpdate eventUpdate) {
        KillAura killAura = NuckerDLC.getInstance().getFunctionRegistry().getKillAura();

        if (killAura.getTarget() != null) {
            currentTarget = killAura.getTarget();
        }

        alpha.setDirection(!killAura.isState() || killAura.getTarget() == null ? Direction.BACKWARDS : Direction.FORWARDS);
    }

    @Subscribe
    private void onWorldEvent(WorldEvent e) {
        if (this.type.is("Кольцо")) {
            EntityRendererManager rm = mc.getRenderManager();
            if (!killAura.isState() || killAura.getTarget() == null) {
            } else {
                double x = killAura.getTarget().lastTickPosX + (killAura.getTarget().getPosX() - killAura.getTarget().lastTickPosX) * (double) e.getPartialTicks() - rm.info.getProjectedView().getX();
                double y = killAura.getTarget().lastTickPosY + (killAura.getTarget().getPosY() - killAura.getTarget().lastTickPosY) * (double) e.getPartialTicks() - rm.info.getProjectedView().getY();
                double z = killAura.getTarget().lastTickPosZ + (killAura.getTarget().getPosZ() - killAura.getTarget().lastTickPosZ) * (double) e.getPartialTicks() - rm.info.getProjectedView().getZ();
                float height = killAura.getTarget().getHeight();
                double duration = 1500.0;
                double elapsed = (double) System.currentTimeMillis() % duration;
                boolean side = elapsed > duration / 2.0;
                double progress = elapsed / (duration / 2.0);
                progress = side ? --progress : 1.0 - progress;
                progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) / 2.0;
                double eased = (double) (height / 2.0F) * (progress > 0.5 ? 1.0 - progress : progress) * (double) (side ? -1 : 1);
                RenderSystem.pushMatrix();
                GL11.glDepthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.shadeModel(7425);
                RenderSystem.disableCull();
                RenderSystem.lineWidth(1.5F);
                RenderSystem.color4f(-1.0F, -1.0F, -1.0F, -1.0F);
                buffer.begin(8, DefaultVertexFormats.POSITION_COLOR);
                float[] colors = null;

                int i;
                for (i = 0; i <= 360; ++i) {
                    colors = DisplayUtils.IntColor.rgb(HUD.getColor(0));
                    buffer.pos(x + Math.cos(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85, y + (double) height * progress, z + Math.sin(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85).color(colors[0], colors[1], colors[2], 0.5F).endVertex();
                    buffer.pos(x + Math.cos(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85, y + (double) height * progress + eased * 1.5, z + Math.sin(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85).color(colors[0], colors[1], colors[2], 0.2F).endVertex();
                }

                buffer.finishDrawing();
                WorldVertexBufferUploader.draw(buffer);
                RenderSystem.color4f(-1.0F, -1.0F, -1.0F, -1.0F);
                buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
                for (i = 0; i <= 360; ++i) {
                    buffer.pos(x + Math.cos(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85, y + (double) height * progress, z + Math.sin(Math.toRadians((double) i)) * (double) this.killAura.getTarget().getWidth() * 0.85).color(colors[0], colors[1], colors[2], 0.2F).endVertex();
                }

                buffer.finishDrawing();
                WorldVertexBufferUploader.draw(buffer);
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableAlphaTest();
                GL11.glDepthMask(true);
                GL11.glDisable(2848);
                GL11.glHint(3154, 4354);
                RenderSystem.shadeModel(7424);
                RenderSystem.popMatrix();
            }
        }
        if (type.is("Призраки")) {
            KillAura killAura = NuckerDLC.getInstance().getFunctionRegistry().getKillAura();
            if (killAura.isState() && killAura.getTarget() != null) {
                MatrixStack ms = new MatrixStack();
                ms.push();
                RenderSystem.pushMatrix();
                RenderSystem.disableLighting();
                depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.shadeModel(7425);
                RenderSystem.disableCull();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(770, 1, 0, 1);
                double x = killAura.getTarget().getPosX();
                double y = killAura.getTarget().getPosY() + killAura.getTarget().getHeight() / 2f;
                double z = killAura.getTarget().getPosZ();
                double radius = 0.6f;
                float speed = 27;
                float size = 0.32f;
                double distance = 15;
                int lenght = 25;
                int maxAlpha = 255;
                int alphaFactor = 15;
                ActiveRenderInfo camera = mc.getRenderManager().info;
                ms.translate(-mc.getRenderManager().info.getProjectedView().getX(),
                        -mc.getRenderManager().info.getProjectedView().getY(),
                        -mc.getRenderManager().info.getProjectedView().getZ());

                Vector3d interpolated = MathUtil.interpolate(killAura.getTarget().getPositionVec(), new Vector3d(killAura.getTarget().lastTickPosX, killAura.getTarget().lastTickPosY, killAura.getTarget().lastTickPosZ), e.getPartialTicks());
                interpolated.y += 0.8f;
                ms.translate(interpolated.x + 0.2f, interpolated.y + 0.5f, interpolated.z);
                mc.getTextureManager().bindTexture(new ResourceLocation("expensive/images/glow.png"));
                for (int i = 0; i < lenght; i++) {
                    Quaternion r = camera.getRotation().copy();
                    buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = sin(angle) * radius;
                    double c = cos(angle) * radius;
                    ms.translate(s, (c), -c);
                    ms.translate(-size / 2f, -size / 2f, 0);
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.getColor(i);
                    int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
                    buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
                    tessellator.draw();
                    ms.translate(-size / 2f, -size / 2f, 0);
                    r.conjugate();
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    ms.translate(-(s), -(c), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    Quaternion r = camera.getRotation().copy();
                    buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = sin(angle) * radius;
                    double c = cos(angle) * radius;
                    ms.translate(-s, s, -c);
                    ms.translate(-size / 2f, -size / 2f, 0);
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.getColor(i);
                    int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
                    buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
                    tessellator.draw();
                    ms.translate(-size / 2f, -size / 2f, 0);
                    r.conjugate();
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    ms.translate((s), -(s), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    Quaternion r = camera.getRotation().copy();
                    buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = sin(angle) * radius;
                    double c = cos(angle) * radius;
                    ms.translate(-(s), -(s), (c));
                    ms.translate(-size / 2f, -size / 2f, 0);
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.getColor(i);
                    int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
                    buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
                    buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(DisplayUtils.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
                    tessellator.draw();
                    ms.translate(-size / 2f, -size / 2f, 0);
                    r.conjugate();
                    ms.rotate(r);
                    ms.translate(size / 2f, size / 2f, 0);
                    ms.translate((s), (s), -(c));
                }
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
                RenderSystem.enableCull();
                RenderSystem.enableAlphaTest();
                depthMask(true);
                RenderSystem.popMatrix();
                ms.pop();
            }
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        if (currentTarget != null && !alpha.finished(Direction.BACKWARDS) && type.is("Ромб")) {
            double sin = sin(System.currentTimeMillis() / 1000.0);
            Vector3d interpolated = currentTarget.getPositon(e.getPartialTicks());


            float size = (float) getScale(interpolated, 9);

            Vector2f pos = ProjectionUtil.project(interpolated.x, interpolated.y + currentTarget.getHeight() / 1.8f, interpolated.z);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(pos.x, pos.y, 0.0F);
            GlStateManager.rotatef((float) sin * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translatef(-pos.x, -pos.y, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);

            int alpha = (int) this.alpha.getOutput();
            DisplayUtils.drawImageAlpha(new ResourceLocation("expensive/images/target.png"), pos.x - size / 2f, pos.y - size / 2f, size, size, new Vector4i(
                    ColorUtils.setAlpha(HUD.getColor(0, 1), alpha),
                    ColorUtils.setAlpha(HUD.getColor(90, 1), alpha),
                    ColorUtils.setAlpha(HUD.getColor(180, 1), alpha),
                    ColorUtils.setAlpha(HUD.getColor(270, 1), alpha)
            ));

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

    }
}