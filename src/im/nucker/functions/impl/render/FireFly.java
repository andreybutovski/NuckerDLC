package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.client.IMinecraft;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.renderer.WorldRenderer.frustum;

@FunctionRegister(name = "FireFly", type = Category.Render)
public class FireFly extends Function {

    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("expensive/images/glow.png");
    private static final float MAX_LIFETIME = 5000f;
    private static final float PART_SIZE = 8.5f;
    private static final int SHADOW_COUNT = 9;

    public FireFly() {
        addSettings();
    }

    private boolean isInView(Vector3d pos) {
        frustum.setCameraPosition(IMinecraft.mc.getRenderManager().info.getProjectedView().x,
                IMinecraft.mc.getRenderManager().info.getProjectedView().y,
                IMinecraft.mc.getRenderManager().info.getProjectedView().z);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }

        // Spawn a new particle
        particles.add(new Particle());

        for (Particle p : particles) {
            if (System.currentTimeMillis() - p.time > MAX_LIFETIME) {
                particles.remove(p);
                continue;
            }
            if (mc.player.getPositionVec().distanceTo(p.pos) > 30) {
                particles.remove(p);
                continue;
            }
            if (isInView(p.pos)) {
                if (!mc.player.canEntityBeSeen(p.pos)) {
                    particles.remove(p);
                    continue;
                }
                p.update();
                renderParticleWithShadows(p);
            } else {
                particles.remove(p);
            }
        }
    }

    private void renderParticleWithShadows(Particle particle) {
        Vector2f screenPos = ProjectionUtil.project(particle.pos.x, particle.pos.y, particle.pos.z);
        float lifetimeFraction = (System.currentTimeMillis() - particle.time) / MAX_LIFETIME;
        float size = PART_SIZE * (1 - lifetimeFraction);

        MatrixStack stack = new MatrixStack();
        stack.push();

        float shadowDistance = 0.2f;

        for (int i = 0; i < SHADOW_COUNT; i++) {
            float shadowAlpha = (1.0f - (float) i / SHADOW_COUNT) * particle.alpha;
            float shadowFactor = 1.0f - lifetimeFraction;
            Vector3d shadowPos = particle.pos.add(particle.velocity.scale(-shadowDistance * i * shadowFactor));
            Vector2f shadowScreenPos = ProjectionUtil.project(shadowPos.x, shadowPos.y, shadowPos.z);

            DisplayUtils.drawImage1(stack, GLOW_TEXTURE, shadowScreenPos.x - size / 2, shadowScreenPos.y - size / 2, 0,
                    size, size,
                    ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * shadowAlpha)),
                    ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * shadowAlpha)),
                    ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * shadowAlpha)),
                    ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * shadowAlpha)));
        }

        DisplayUtils.drawImage1(stack, GLOW_TEXTURE, screenPos.x - size / 2, screenPos.y - size / 2, 0,
                size, size,
                ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * particle.alpha)),
                ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * particle.alpha)),
                ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * particle.alpha)),
                ColorUtils.setAlpha(HUD.getColor(particles.indexOf(particle), 1), (int) (255 * particle.alpha)));

        stack.pop();
    }

    private class Particle {
        private Vector3d pos;
        private final Vector3d end;
        private final Vector3d velocity;
        private final long time;
        private float alpha;

        public Particle() {

            pos = mc.player.getPositionVec().add(
                    ThreadLocalRandom.current().nextDouble(-30, 30),
                    ThreadLocalRandom.current().nextDouble(-10, 30),
                    ThreadLocalRandom.current().nextDouble(-30, 30));
            end = pos.add(
                    ThreadLocalRandom.current().nextFloat(-3, 3),
                    ThreadLocalRandom.current().nextFloat(-3, 3),
                    ThreadLocalRandom.current().nextFloat(-3, 3));
            velocity = end.subtract(pos).normalize().scale(0.2);
            time = System.currentTimeMillis();
            alpha = 0.75f;
        }

        public void update() {
            alpha = MathUtil.fast(alpha, 1, 10);
            pos = MathUtil.fast(pos, end, 0.5f);
        }
    }

    @Override
    public void onDisable() {
        particles.clear();
        super.onDisable();
    }

}
