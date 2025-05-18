package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.events.AttackEvent;
import im.nucker.events.EventDisplay;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.renderer.WorldRenderer.frustum;


@FunctionRegister(name = "GlowParticles", type = Category.Render)
public class GlowParticles extends Function {

    private final SliderSetting value=new SliderSetting("Кол-во за удар", 20.0f, 1.0f, 50.0f, 1.0f);
    private final CopyOnWriteArrayList<HitFire> HitFire=new CopyOnWriteArrayList<>();
    private static final ResourceLocation GLOW_TEXTURE=new ResourceLocation("expensive/images/glow.png");
    private static final float MAXTIME=4000f;
    private static final float PARTSIZE=20.0f;
    private static final int TRAIL_MAX_LEN=4;

    public GlowParticles() {
        addSettings(value);
    }


    private boolean isInView(Vector3d pos) {
        frustum.setCameraPosition(mc.getRenderManager().info.getProjectedView().x,
                mc.getRenderManager().info.getProjectedView().y,
                mc.getRenderManager().info.getProjectedView().z);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    private boolean isVisible(Vector3d pos) {
        Vector3d cameraPos=mc.getRenderManager().info.getProjectedView();
        RayTraceContext context=new RayTraceContext(cameraPos, pos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, mc.player);
        BlockRayTraceResult result=mc.world.rayTraceBlocks(context);
        return result.getType() == RayTraceResult.Type.MISS;
    }

    @Subscribe
    private void onUpdate(AttackEvent e) {
        if (e.entity == mc.player) return;
        if (e.entity instanceof LivingEntity livingEntity) {
            Vector3d center=livingEntity.getPositionVec().add(0, livingEntity.getHeight() / 2f, 0);
            for (int i=0; i < value.get(); i++) {
                HitFire.add(new HitFire(center));
            }
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }

        MatrixStack matrixStack=e.getMatrixStack();

        HitFire.removeIf(p -> {
            long elapsedTime=System.currentTimeMillis() - p.creationTime;
            if (elapsedTime > MAXTIME) {
                p.alpha=Math.max(0, 0.75f * (1 - (elapsedTime - MAXTIME) / 1000f));
            }
            boolean shouldRemove=p.alpha <= 0 || mc.player.getPositionVec().distanceTo(p.pos) > 60 || !isInView(p.pos) || !isVisible(p.pos);
            if (shouldRemove) {
                return true;
            }
            p.update();
            renderParticleWithShadows(matrixStack, p);
            return false;
        });
    }

    private void renderParticleWithShadows(MatrixStack stack, HitFire HitFire) {
        float lifetimeFraction=(System.currentTimeMillis() - HitFire.creationTime) / MAXTIME;
        float size=PARTSIZE * (1 - lifetimeFraction);

        for (int i=0; i < HitFire.trailPoints.size(); i++) {
            Vector3d trailPos=HitFire.trailPoints.get(i);
            Vector2f screenPos=ProjectionUtil.project(trailPos.x, trailPos.y, trailPos.z);

            float trailFraction=(float) i / HitFire.trailPoints.size();
            float trailSize=size * (1.0f - trailFraction);
            float trailAlpha=(1.0f - trailFraction) * HitFire.alpha;
            int color=ColorUtils.setAlpha(HUD.getColor(HitFire.indexOf(HitFire), 1), (int) (255 * trailAlpha));

            DisplayUtils.drawImage1(stack, GLOW_TEXTURE, screenPos.x - trailSize / 2, screenPos.y - trailSize / 2, 0,
                    trailSize, trailSize,
                    color, color, color, color);
        }

        Vector2f particleScreenPos=ProjectionUtil.project(HitFire.pos.x, HitFire.pos.y, HitFire.pos.z);
        DisplayUtils.drawImage1(stack, GLOW_TEXTURE, particleScreenPos.x - size / 2, particleScreenPos.y - size / 2, 0,
                size, size,
                ColorUtils.setAlpha(HUD.getColor(HitFire.indexOf(HitFire), 1), (int) (255 * HitFire.alpha)),
                ColorUtils.setAlpha(HUD.getColor(HitFire.indexOf(HitFire), 1), (int) (255 * HitFire.alpha)),
                ColorUtils.setAlpha(HUD.getColor(HitFire.indexOf(HitFire), 1), (int) (255 * HitFire.alpha)),
                ColorUtils.setAlpha(HUD.getColor(HitFire.indexOf(HitFire), 1), (int) (255 * HitFire.alpha)));
    }

    private class HitFire {
        private Vector3d pos;
        private final Vector3d end;
        private final Vector3d velocity;
        private final long creationTime;
        private float alpha;
        private final List<Vector3d> trailPoints=new ArrayList<>();

        public HitFire(Vector3d pos) {
            this.pos=pos;
            this.end=pos.add(
                    ThreadLocalRandom.current().nextDouble(-0.3, 0.3),
                    ThreadLocalRandom.current().nextDouble(-0.3, 0.3),
                    ThreadLocalRandom.current().nextDouble(-0.3, 0.3));
            this.velocity=end.subtract(pos).normalize().scale(0.02);
            this.creationTime=System.currentTimeMillis();
            this.alpha=0.75f;
            this.trailPoints.add(pos);
        }

        public void update() {
            long elapsedTime=System.currentTimeMillis() - creationTime;
            float lifetimeFraction=elapsedTime / MAXTIME;

            if (elapsedTime > MAXTIME) {
                alpha=Math.max(0, 0.75f * (1 - (elapsedTime - MAXTIME) / 2000f));
            }

            if (alpha <= 0) return;

            pos=pos.add(velocity);
            trailPoints.add(0, pos);

            if (trailPoints.size() > TRAIL_MAX_LEN) {
                trailPoints.remove(trailPoints.size() - 1);
            }
        }

        public int indexOf(HitFire hitFire) {
            return 0;
        }
    }
}