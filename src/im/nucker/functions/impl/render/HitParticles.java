package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.events.AttackEvent;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.mojang.blaze3d.platform.GlStateManager.depthMask;
import static net.minecraft.client.renderer.WorldRenderer.frustum;

@FunctionRegister(name = "HitParticles", type = Category.Render)
public class HitParticles extends Function {
    final ResourceLocation star = new ResourceLocation("expensive/images/star.png");
    final ResourceLocation Orbiz = new ResourceLocation("expensive/images/bloom.png");
    final ResourceLocation bloom = new ResourceLocation("expensive/images/glow.png");
    final ResourceLocation d = new ResourceLocation("expensive/images/f.png");
    final ResourceLocation dg = new ResourceLocation("expensive/images/dollar.png");
    final ResourceLocation heart = new ResourceLocation("expensive/images/heart.png");
    private final ModeSetting setting = new ModeSetting("Вид", "Сердечки", "Сердечки", "Звёздочки", "Снежинки", "Доллары", "Орбиз", "Блум");
    private final SliderSetting value = new SliderSetting("Кол-во за удар", 20.0f, 1.0f, 50.0f, 1.0f);
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

    public HitParticles() {
        addSettings(setting, value);
    }

    private boolean isInView(Vector3d pos) {
        frustum.setCameraPosition(mc.getRenderManager().info.getProjectedView().x,
                mc.getRenderManager().info.getProjectedView().y,
                mc.getRenderManager().info.getProjectedView().z);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    @Subscribe
    private void onUpdate(AttackEvent e) {
        if (e.entity == mc.player) return;
        if (e.entity instanceof LivingEntity livingEntity) {
            for (int i = 0; i < value.get(); i++) {
                particles.add(new Particle(livingEntity.getPositon(mc.getRenderPartialTicks()).add(0, livingEntity.getHeight() / 2f, 0)));
            }
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        MatrixStack stack = new MatrixStack();
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }

        for (Particle p : particles) {
            if (System.currentTimeMillis() - p.time > 3500f) {
                particles.remove(p);
                continue;
            }
            if (mc.player.getPositionVec().distanceTo(p.pos) > 100) {
                particles.remove(p);
                continue;
            }
            if (isInView(p.pos)) {
                if (!mc.player.canEntityBeSeen(p.pos)) {
                    particles.remove(p);
                    continue;
                }
                p.update();
                Vector2f pos = ProjectionUtil.project(p.pos.x, p.pos.y, p.pos.z);

                float size = 1.3F - ((System.currentTimeMillis() - p.time) / 3300f);
                DisplayUtils.drawShadowCircle(pos.x + 3.0F, pos.y + 3.0F, 10, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((64 * p.alpha) * size)));

                switch (setting.get()) {
                    case "Сердечки" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(heart, pos.x - 3.0F * size, pos.y - 3.0F * size, 16 * size, 16 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                    case "Звёздочки" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(star, pos.x - 3.0F * size, pos.y - 3.0F * size, 16 * size, 16 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                    case "Орбиз" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(Orbiz, pos.x - 3.0F * size, pos.y - 3.0F * size, 16 * size, 16 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                    case "Блум" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(bloom, pos.x - 3.0F * size, pos.y - 3.0F * size, 32 * size, 32 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                    case "Снежинки" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(d, pos.x - 3.0F * size, pos.y - 3.0F * size, 16 * size, 16 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                    case "Доллары" -> {
                        stack.push();
                        depthMask(false);
                        DisplayUtils.drawImage(dg, pos.x - 3.0F * size, pos.y - 3.0F * size, 16 * size, 16 * size, ColorUtils.setAlpha(HUD.getColor(particles.indexOf(p), 6), (int) ((200 * p.alpha) * size)));
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        depthMask(true);
                        stack.pop();
                    }
                }
            } else {
                particles.remove(p);
            }
        }
    }

    private class Particle {
        private Vector3d pos;
        private Vector3d velocity;
        private final Vector3d end;
        private final long time;

        private float alpha;

        public Particle(Vector3d pos) {
            this.pos = pos;
            this.velocity = new Vector3d(
                    ThreadLocalRandom.current().nextDouble(-0.04, 0.04), // Уменьшили скорость по X
                    ThreadLocalRandom.current().nextDouble(0.01, 0.05), // Уменьшили скорость по Y
                    ThreadLocalRandom.current().nextDouble(-0.04, 0.04)  // Уменьшили скорость по Z
            );
            end = pos.add(
                    ThreadLocalRandom.current().nextDouble(-1.5, 1.5), // Уменьшили диапазон конечного положения по X
                    ThreadLocalRandom.current().nextDouble(-1.5, 1.5), // Уменьшили диапазон конечного положения по Y
                    ThreadLocalRandom.current().nextDouble(-1.5, 1.5)  // Уменьшили диапазон конечного положения по Z
            );
            time = System.currentTimeMillis();
        }
        public void update() {

            alpha = MathUtil.fast(alpha, 1, 10);

            // Обновляем положение с учетом гравитации
            velocity = velocity.add(0, -0.0004, 0); // Гравитация
            pos = pos.add(velocity);

            // Проверка на столкновение с блоками
            BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
            if (mc.world.getBlockState(blockPos).getMaterial().isSolid()) {
                // Корректируем положение, чтобы частица не проваливалась под блок
                double yCorrection = blockPos.getY() + 1 - pos.y; // Коррекция по оси Y, поднятие частицы над блоком
                pos = pos.add(0, yCorrection, 0);

                // Останавливаем частицу после отскока
                velocity = Vector3d.ZERO;
            }

            // Обновление до конечной позиции
            pos = MathUtil.fast(pos, end, 0.5f);
        }


    }
}
