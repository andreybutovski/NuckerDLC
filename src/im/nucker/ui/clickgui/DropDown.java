// DropDown.java

package im.nucker.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import im.nucker.NuckerDLC;
import im.nucker.functions.api.Category;
import im.nucker.functions.impl.render.ClickGui;
import im.nucker.ui.clickgui.components.SearchField;

import im.nucker.utils.CustomFramebuffer;
import im.nucker.utils.animations.impl.CompactAnimation;
import im.nucker.utils.animations.impl.Easing;
import im.nucker.utils.client.ClientUtil;
import im.nucker.utils.client.IMinecraft;


import im.nucker.utils.client.Vec2i;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.math.TimerUtility;
import im.nucker.utils.math.Vector2i;
import im.nucker.utils.render.Cursors;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.*;
import im.nucker.utils.render.GifUtility;
import lombok.Getter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DropDown extends Screen implements IMinecraft {

    public SearchField searchField;



    private static final Animation gradientAnimation = new Animation();
    private final List<Panel> panels = new ArrayList<>();

    @Getter
    private static final Animation globalAnim = new Animation();
    @Getter
    private static Animation animation = new Animation();
    @Getter
    private static final Animation imageAnimation = new Animation();
    private boolean exit = false, open = false;

    private static final CompactAnimation scaleAnimation = new CompactAnimation(Easing.EASE_IN_QUAD, 200);
    private static final CompactAnimation psChanAnimation = new CompactAnimation(Easing.LINEAR, 700);
    private static final CompactAnimation psChanOverlayAnimation = new CompactAnimation(Easing.LINEAR, 1400);
    private final TimerUtility psChatYAnimTimer = new TimerUtility();
    private final TimerUtility psChatOverlayAnimTimer = new TimerUtility();

    public DropDown(ITextComponent titleIn) {
        super(titleIn);
        for (Category category : Category.values()) {
            if (category == Category.Themes) continue;
            panels.add(new Panel(category));
        }
        panels.add(new PanelStyle(Category.Themes));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        gradientAnimation.animate(1, 0.25f, Easings.EXPO_OUT);
        imageAnimation.animate(1, 0.5, Easings.BACK_OUT);
        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());

        float x = (windowWidth / 2f) - (60);
        float y = windowHeight / 2.1f + (510 / 2f) / 2.1f + 30;
        ClickGui clickGui = NuckerDLC.getInstance().getFunctionRegistry().getClickGui();

        searchField = new SearchField((int) x, (int) y, 120, 20, "Search your function  ");

        exit = false;
        open = true;

        animation.animate(1, 0.25f, Easings.EXPO_OUT);
        super.init();
    }

    public static float scale = 1.0f;

    public boolean isSearching() {

            return !searchField.isEmpty();

    }

    public String getSearchText() {

        return searchField.getText();

    }

    public boolean searchCheck(String text) {

            return isSearching() && !text
                    .replaceAll(" ", "")
                    .toLowerCase()
                    .contains(getSearchText()
                            .replaceAll(" ", "")
                            .toLowerCase());

    }

    @Override
    public void closeScreen() {
        super.closeScreen();
        GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Vector2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();

        for (Panel panel : panels) {
            if (MathUtil.isHovered((float) mouseX, (float) mouseY, panel.getX(), panel.getY(), panel.getWidth(),
                    panel.getHeight())) {
                panel.setScroll((float) (panel.getScroll() + (delta * 20)));
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchField.charTyped(codePoint, modifiers)) {
            return true;
        }

        for (Panel panel : panels) {
            panel.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ClickGui clickGui = NuckerDLC.getInstance().getFunctionRegistry().getClickGui();
        KawaseBlur.blur.updateBlur(3, 3);
        mc.gameRenderer.setupOverlayRendering(2);
        Stream.of(animation, imageAnimation, gradientAnimation).forEach(Animation::update);
        boolean allow = !(animation.getValue() > 0.4);

        if (Stream.of(animation, imageAnimation, gradientAnimation).allMatch(anim -> anim.getValue() <= 0.1 && anim.isDone())) {
            closeScreen();
        }

        if (animation.getValue() < 0.1) {
            closeScreen();
        }


        final float off = 7;
        float width = panels.size() * (97 + off);

        updateScaleBasedOnScreenWidth();

        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());

        Vector2i fixMouse = adjustMouseCoordinates(mouseX, mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();





        if (clickGui.blur.get()) {
            KawaseBlur.blur.updateBlur(clickGui.blurPower.get(), clickGui.blurPower.get().intValue());
        }

        if (ClickGui.images.get()) {
            GifUtility gifUtility = new GifUtility();
            String image = clickGui.imageType.get().toLowerCase();
            String path = "expensive/images/gui/";
            String psChanOverlay = "expensive/images/gui/pschan/ps_overlay.png";
            int totalFrames = 0;
            int frameDelay = 0;
            boolean fromZero = false;

            long durY = 700;
            long durOverlayAlpha = 1400;

            if (ClickGui.imageType.is("Miku")) {
                totalFrames = 9;
                frameDelay = 40;
            } else if (ClickGui.imageType.is("Novoura")) {
                totalFrames = 4;
                frameDelay = 80;
            }else if (ClickGui.imageType.is("cat")) {
                totalFrames = 8;
                frameDelay = 80;
            }

            if (ClickGui.imageType.is("PSChan")) {
                path = "expensive/images/gui/pschan/";
                image = "ps_base";
            }

            if (Arrays.asList("Miku", "Novoura", "cat").contains(ClickGui.imageType.get())) {
                int i = gifUtility.getFrame(totalFrames, frameDelay, fromZero);
                path = "expensive/images/gif/" + ClickGui.imageType.get().toLowerCase() + "/frame_" + i;
                image = "";
            }

            if (psChanAnimation.getValue() != 10 && !psChatYAnimTimer.isReached(durY)) {
                psChanAnimation.run(10);
            } if (psChanAnimation.getValue() != 0 && psChatYAnimTimer.isReached(durY)) {
                psChanAnimation.run(0);
            } if (psChatYAnimTimer.isReached(durY * 2)) {
                psChatYAnimTimer.reset();
            }

            if (psChanOverlayAnimation.getValue() != 255 && !psChatOverlayAnimTimer.isReached(durOverlayAlpha)) {
                psChanOverlayAnimation.run(255);
            } if (psChanOverlayAnimation.getValue() != 0 && psChatOverlayAnimTimer.isReached(durOverlayAlpha * 2)) {
                psChanOverlayAnimation.run(0);
            } if (psChatOverlayAnimTimer.isReached(durOverlayAlpha * 3)) {
                psChatOverlayAnimTimer.reset();
            }

            float offset = (float) (ClickGui.imageType.is("PSChan") ? psChanAnimation.getValue() : 0);
            float size = (float) ((512f / 2f) - 100 + 100 * imageAnimation.getValue());
            float x1 = (windowWidth - size);
            float x2 = (windowWidth);
            float y1 = (windowHeight - size);
            float y2 = (windowHeight);

            DisplayUtils.drawImage(new ResourceLocation(path + image + ".png"), x1, y1 + offset, x2 - x1, y2 - y1, ColorUtils.reAlphaInt(-1, (int) ((255 * (imageAnimation.getValue())) * getAnimation().getValue())));

            if (ClickGui.imageType.is("PSChan")) {
                DisplayUtils.drawImage(new ResourceLocation(psChanOverlay), x1, y1 + offset, x2 - x1, y2 - y1, ColorUtils.reAlphaInt(-1, (int) ((psChanOverlayAnimation.getValue() * getAnimation().getValue()))));
            }
        }





        Stencil.initStencilToWrite();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(windowWidth / 2f, windowHeight / 2f, 0);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1);
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.translatef(-windowWidth / 2f, -windowHeight / 2f, 0);



        GlStateManager.popMatrix();
        Stencil.readStencilBuffer(1);
        GlStateManager.bindTexture(KawaseBlur.blur.BLURRED.framebufferTexture);
        CustomFramebuffer.drawTexture();
        Stencil.uninitStencilBuffer();


        GlStateManager.pushMatrix();
        GlStateManager.translatef(windowWidth / 2f, windowHeight / 2f, 0);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1);
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.translatef(-windowWidth / 2f, -windowHeight / 2f, 0);
        MainWindow mainWindow = mc.getMainWindow();

        for (Panel panel : panels) {
            panel.setY(windowHeight / 2f - (625 / 2) / 2f);
            panel.setX((windowWidth / 2f - 5) - (width / 2f) + panel.getCategory().ordinal() *
                    (116 + off) + off / 2f);
            float animationValue = (float) animation.getValue() * scale;

            float halfAnimationValueRest = (1 - animationValue) / 2f;

            float testX = panel.getX() + (panel.getWidth() * halfAnimationValueRest);
            float testY = panel.getY() + (panel.getHeight() * halfAnimationValueRest);
            float testW = panel.getWidth() * animationValue;
            float testH = panel.getHeight() * animationValue;

            testX = testX * animationValue + ((windowWidth - testW) *
                    halfAnimationValueRest);

            Scissor.push();
            Scissor.setFromComponentCoordinates(testX - 9, testY-9, testW+20, testH+20);
            panel.render(matrixStack, mouseX, mouseY);
            Scissor.unset();
            Scissor.pop();

        }

            searchField.render(matrixStack, mouseX, mouseY, partialTicks);



        GlStateManager.popMatrix();
        mc.gameRenderer.setupOverlayRendering();


    }

    private void updateScaleBasedOnScreenWidth() {
        final float PANEL_WIDTH = 165;
        final float MARGIN = 10;
        final float MIN_SCALE = 0.5f;

        float totalPanelWidth = panels.size() * (PANEL_WIDTH + MARGIN);
        float screenWidth = mc.getMainWindow().getScaledWidth();

        if (totalPanelWidth >= screenWidth) {
            scale = screenWidth / totalPanelWidth;
            scale = MathHelper.clamp(scale, MIN_SCALE, 1.0f);
        } else {
            scale = 1f;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

            if (searchField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

        for (Panel panel : panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            animation = animation.animate(0, 0.25f, Easings.EXPO_OUT);
            imageAnimation.animate(0.0, 0.3, Easings.BACK_OUT);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Vector2i adjustMouseCoordinates(int mouseX, int mouseY) {
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();

        float adjustedMouseX = (mouseX - windowWidth / 2f) / scale + windowWidth / 2f;
        float adjustedMouseY = (mouseY - windowHeight / 2f) / scale + windowHeight / 2f;

        return new Vector2i((int) adjustedMouseX, (int) adjustedMouseY);
    }

    private double pathX(float mouseX, float scale) {
        if (scale == 1) return mouseX;
        int windowWidth = mc.getMainWindow().scaledWidth();
        int windowHeight = mc.getMainWindow().scaledHeight();
        mouseX /= (scale);
        mouseX -= (windowWidth / 2f) - (windowWidth / 2f) * (scale);
        return mouseX;
    }

    private double pathY(float mouseY, float scale) {
        if (scale == 1) return mouseY;
        int windowWidth = mc.getMainWindow().scaledWidth();
        int windowHeight = mc.getMainWindow().scaledHeight();
        mouseY /= scale;
        mouseY -= (windowHeight / 2f) - (windowHeight / 2f) * (scale);
        return mouseY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();

            if (searchField.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

        for (Panel panel : panels) {
            panel.mouseClick((float) mouseX, (float) mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();
        for (Panel panel : panels) {
            panel.mouseRelease((float) mouseX, (float) mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

}
