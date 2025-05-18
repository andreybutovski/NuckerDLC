package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventDisplay;
import im.nucker.events.WorldEvent;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.math.Vector4i;
import im.nucker.utils.projections.ProjectionUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@FunctionRegister(name = "ArrowPredictions", type = Category.Render)
public class ArrowPredictions extends Function {

    public ArrowPredictions() {
        addSettings();
    }

    record ArrowPoint(Vector3d position, int ticks) {}

    final List<ArrowPoint> arrowPoints = new ArrayList<>();

    @Subscribe
    public void renderPrediction(EventDisplay e) {
        for (ArrowPoint arrowPoint : arrowPoints) {
            Vector3d pos = arrowPoint.position;
            Vector2f projection = ProjectionUtil.project(pos.x, pos.y - 0.3F, pos.z);
            int ticks = arrowPoint.ticks;

            if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
                continue;
            }

            double time = ticks * 50 / 1000.0;
            String text = String.format("%.1f сек.", time);
            float width = Fonts.montserrat.getWidth(text, 7);

            float textWidth = width + 22;
            float posX = projection.x - textWidth / 2;
            float posY = projection.y;

            DisplayUtils.drawRoundedRect(posX + 3, posY - 1, textWidth - 4, 13, 0, ColorUtils.rgba(24, 24, 24, 80));
            DisplayUtils.drawImage(new ResourceLocation("textures/item/arrow.png"), (int) posX + 5 , (int) posY - 2, 11 , 11, -1);
            Fonts.montserrat.drawText(e.getMatrixStack(), text, posX + 18, posY + 5, -1, 7);
        }
    }

    @Subscribe
    public void onRender(WorldEvent event) {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        Vector3d renderOffset = mc.getRenderManager().info.getProjectedView();
        glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);
        glLineWidth(3);
        buffer.begin(1, DefaultVertexFormats.POSITION);

        arrowPoints.clear();
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof AbstractArrowEntity arrow) {
                Vector3d motion = arrow.getMotion();
                Vector3d pos = arrow.getPositionVec();
                Vector3d prevPos;
                int ticks = 0;

                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);
                    motion = getNextMotion(arrow, motion);
                    ColorUtils.setAlpha(ColorUtils.getColor(0), 165);
                    buffer.pos(prevPos.x, prevPos.y, prevPos.z).endVertex();

                    RayTraceContext rayTraceContext = new RayTraceContext(
                            prevPos,
                            pos,
                            RayTraceContext.BlockMode.COLLIDER,
                            RayTraceContext.FluidMode.NONE,
                            arrow
                    );

                    BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
                    boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;

                    if (isLast) {
                        pos = blockHitResult.getHitVec();
                    }
                    buffer.pos(pos.x, pos.y, pos.z).endVertex();

                    if (blockHitResult.getType() == BlockRayTraceResult.Type.BLOCK || pos.y < -128) {
                        arrowPoints.add(new ArrowPoint(pos, ticks));
                        break;
                    }
                    ticks++;
                }
            }
        }

        tessellator.draw();
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    private Vector3d getNextMotion(AbstractArrowEntity arrow, Vector3d motion) {
        if (arrow.isInWater()) {
            motion = motion.scale(0.8);
        } else {
            motion = motion.scale(0.99);
        }

        if (!arrow.hasNoGravity()) {
            motion = new Vector3d(motion.x, motion.y - 0.05, motion.z);
        }

        return motion;
    }
}