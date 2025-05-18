package im.nucker.utils.drag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import im.nucker.functions.api.Function;
import im.nucker.utils.client.ClientUtil;
import im.nucker.utils.client.Vec2i;
import im.nucker.utils.math.MathUtil;

import im.nucker.utils.render.DisplayUtils;
import net.minecraft.client.MainWindow;

public class Dragging {
    @Expose
    @SerializedName("x")
    private float xPos;
    @Expose
    @SerializedName("y")
    private float yPos;

    public float initialXVal;
    public float initialYVal;

    private float startX, startY;
    private boolean dragging;
    private float closestVerticalLine = 0;
    private float closestHorizontalLine = 0;
    private static final float grid = 2;
    private static final float snap_thr = 10;
    private float width, height;
    boolean showVerticalLine, showHorizontalLine;
    @Expose
    @SerializedName("name")
    private final String name;
    private final Function module;
    private float lineAlpha = 0.0f;
    private long lastUpdateTime;
    private int fontSize = 22;

    public Dragging(Function module, String name, float initialXVal, float initialYVal) {
        this.module = module;
        this.name = name;
        this.xPos = initialXVal;
        this.yPos = initialYVal;
        this.initialXVal = initialXVal;
        this.initialYVal = initialYVal;
    }

    public Function getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }

    public final void onDraw(int mouseX, int mouseY, MainWindow res) {
        Vec2i fixed = ClientUtil.getMouse(mouseX,mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        if (dragging) {
            float radius = 5;
            xPos = (mouseX - startX);
            yPos = (mouseY - startY);

            xPos = snap(xPos, grid, snap_thr);
            yPos = snap(yPos, grid, snap_thr);

            if (xPos + width > res.scaledWidth()) {
                xPos = res.scaledWidth() - width;
            }
            if (yPos + height > res.scaledHeight()) {
                yPos = res.scaledHeight() - height;
            }
            if (xPos < 0) {
                xPos = 0;
            }
            if (yPos < 0) {
                yPos = 0;
            }
            float alpha = lineAlpha * 1;
            int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
            if (xPos + (width / 2) >= res.getScaledWidth() / 2 - radius && xPos + (width / 2) <= res.getScaledWidth() / 2 + radius && (mouseX >= xPos)) {
                xPos = res.getScaledWidth() / 2 - (width / 2);
                DisplayUtils.drawRoundedRect(res.getScaledWidth() / 2, 0, 1, res.getScaledHeight(), 0, color);
                showHorizontalLine = false;
                showVerticalLine = false;
            } else {
                checkClosestGridLines();
            }

            updateLineAlpha(true);
        } else {
            updateLineAlpha(false);
        }
        drawGridLines(res);
    }

    private void drawGridLines(MainWindow res) {
        float alpha = lineAlpha * 1;
        int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        if (showVerticalLine) {
            DisplayUtils.drawRoundedRect(closestVerticalLine, 0, 1, res.scaledHeight(), 0, color);
        }

        if (showHorizontalLine) {
            DisplayUtils.drawRoundedRect(0, closestHorizontalLine, res.scaledWidth(), 1, 0, color);
        }
    }

    private void updateLineAlpha(boolean increasing) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        if (increasing) {
            lineAlpha += deltaTime * 4;
            if (lineAlpha > 1.0f) {
                lineAlpha = 1.0f;
            }
        } else {
            lineAlpha -= deltaTime * 4;
            if (lineAlpha < 0.0f) {
                lineAlpha = 0.0f;
            }
        }
    }

    private void checkClosestGridLines() {
        closestVerticalLine = Math.round(xPos / grid) * grid;
        closestHorizontalLine = Math.round(yPos / grid) * grid;

        showVerticalLine = Math.abs(xPos - closestVerticalLine) < snap_thr;
        showHorizontalLine = Math.abs(yPos - closestHorizontalLine) < snap_thr;
    }

    private float snap(float pos, float gridSpacing, float snapThreshold) {
        float gridPos = Math.round(pos / gridSpacing) * gridSpacing;
        if (Math.abs(pos - gridPos) < snapThreshold) {
            return gridPos;
        }
        return pos;
    }

    public final boolean onClick(double mouseX, double mouseY, int button) {
        Vec2i fixed = ClientUtil.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        if (button == 0 && MathUtil.isHovered((float) mouseX, (float) mouseY, xPos, yPos, width, height)) {
            dragging = true;
            startX = (int) (mouseX - xPos);
            startY = (int) (mouseY - yPos);
            lastUpdateTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public final void onRelease(int button) {
        if (button == 0) dragging = false;
    }

    public void resetPosition() {
        this.xPos = this.initialXVal;
        this.yPos = this.initialYVal;
    }

}