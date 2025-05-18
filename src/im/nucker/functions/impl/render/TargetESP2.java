package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.impl.combat.KillAura;
import im.nucker.utils.animations.Animation;
import im.nucker.utils.animations.Direction;
import im.nucker.utils.animations.impl.DecelerateAnimation;
import im.nucker.utils.math.Vector4i;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@FunctionRegister(name = "TargetESP 2", type = Category.Render)
public class TargetESP2 extends Function {
    private final Animation alpha = new DecelerateAnimation(1000, 225);

    private LivingEntity currentTarget;

    private double speed;
    private long lastTime = System.currentTimeMillis();
    private LivingEntity target;

    public TargetESP2() {
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
    private void onDisplay(EventDisplay e) {

        if (e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        if (this.currentTarget != null && this.currentTarget != mc.player) {
            double sin = Math.sin(System.currentTimeMillis() / 800.0);
            float size = 110.0F;

            Vector3d interpolated = currentTarget.getPositon(e.getPartialTicks());
            Vector2f pos = ProjectionUtil.project(interpolated.x, interpolated.y + currentTarget.getHeight() / 2f, interpolated.z);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(pos.x, pos.y, 0);
            GlStateManager.rotatef((float) sin * 120, 0, 0, 1);
            GlStateManager.translatef(-pos.x, -pos.y, 0);
            if (pos != null) {
                DisplayUtils.drawImageAlpha(new ResourceLocation("expensive/images/target1.png"), pos.x - size / 2f, pos.y - size / 2f, size/1, size/1, new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) this.alpha.getOutput()),
                        ColorUtils.setAlpha(HUD.getColor(0, 1), (int) this.alpha.getOutput()),
                        ColorUtils.setAlpha(HUD.getColor(122, 1), (int) this.alpha.getOutput()),
                        ColorUtils.setAlpha(HUD.getColor(255, 1), (int) this.alpha.getOutput())
                ));
                GlStateManager.popMatrix();
            }
        }
    }
}