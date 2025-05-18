package im.nucker.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;


import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;

import im.nucker.NuckerDLC;
import im.nucker.functions.impl.render.ClickGui;


import im.nucker.ui.clickgui.components.builder.IBuilder;
import im.nucker.ui.clickgui.components.ModuleComponent;
import im.nucker.utils.math.MathUtil;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.ui.clickgui.components.builder.Component;
import im.nucker.utils.render.KawaseBlur;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.Stencil;
import im.nucker.utils.text.font.ClientFonts;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Panel implements IBuilder {

    private final Category category;
    protected float x;
    protected float y;
    protected final float width = 120;
    protected final float height = 600 / 2f;
    private float scroll, animatedScrool;
    private boolean draggingScrollbar = false;
    private float lastMouseY;
    private final Vector4f ROUNDING_VECTOR = new Vector4f(8.0f, 8.0f, 8.0f, 8.0f);

    private List<ModuleComponent> modules = new ArrayList<>();
    private DisplayUtils RectUtils;


    public Panel(Category category) {
        this.category = category;

        for (Function function : NuckerDLC.getInstance().getFunctionRegistry().getFunctions()) {
            if (function.getCategory() == category) {
                ModuleComponent component = new ModuleComponent(function);
                component.setPanel(this);
                modules.add(component);
            }
        }

    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        ClickGui clickGui = NuckerDLC.getInstance().getFunctionRegistry().getClickGui();
        animatedScrool = MathUtil.fast(animatedScrool, scroll, 10);
        float header = 55 / 2.3f;
        float headerFont = 9;
        DisplayUtils.drawShadow(x, y-1, width, height+3, 10, ColorUtils.reAlphaInt(ColorUtils.rgba(30,30,30, 255), ColorUtils.rgba(30,30,30, 255)));

        clickGui.blur.get();
        {
            KawaseBlur.blur.updateBlur(2.5f, 0);
            KawaseBlur.blur.render(() -> DisplayUtils.drawRoundedRect(x, y, width, height, 7,
                    ColorUtils.rgba(15, 15, 15, 5)));
        }

        DisplayUtils.drawRoundedRect(x, y, width, height, 7,
                ColorUtils.rgba(15,15,15, 210));

        DisplayUtils.drawRoundedRect(x, y, width, height, 7,
                ColorUtils.rgba(0,0,0, 222));

        float textWidth = ClientFonts.msMedium[24].getWidth(category.name());
        float centerX = x + width / 2f;
        float centerY = y + header / 2f - ClientFonts.msMedium[18].getFontHeight() / 2f;

        ClientFonts.msMedium[24].drawString(stack, category.name(), centerX - textWidth / 2, centerY + 3, ColorUtils.rgba(255, 255, 255, 255));
        ClientFonts.icons_wex[35].drawString(stack, category.getIcon(), x + 10, y + 17 - ClientFonts.icons_wex[30].getFontHeight() / 2f, ColorUtils.getColor(0));

        drawComponents(stack, mouseX, mouseY);

        drawOutline();
    }

    protected void drawOutline() {
        Stencil.initStencilToWrite();

        DisplayUtils.drawRoundedRect(x + 0.5f, y + 0.5f, width - 1, height - 1, new Vector4f(7, 7, 7, 7),
                ColorUtils.rgba(21, 21, 21, (int) (255 * 0.33)));

        Stencil.readStencilBuffer(0);

        Stencil.uninitStencilBuffer();
    }

    float max = 0;

    private void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        float animationValue = (float) DropDown.getAnimation().getValue() * DropDown.scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float height = getHeight();
        float testX = getX() + (getWidth() * halfAnimationValueRest);
        float testY = getY() + 55 / 2f + (height * halfAnimationValueRest);
        float testW = getWidth() * animationValue;
        float testH = height * animationValue;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) *
                halfAnimationValueRest);

        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH-34);
        float offset = 0;
        float header = 55 / 2f;

        if (max > height - header - 10) {
            scroll = MathHelper.clamp(scroll, -max + height - header - 10, 0);
            animatedScrool = MathHelper.clamp(animatedScrool, -max + height - header - 10, 0);
        }
        else {
            scroll = 0;
            animatedScrool = 0;
        }
        for (ModuleComponent component : modules) {
            if(NuckerDLC.getInstance().getDropDown().searchCheck(component.getFunction().getName())){
                continue;
            }

            component.setX(getX() + 6.5f);
            component.setY(getY() + header + offset  + animatedScrool);
            component.setWidth(getWidth() - 13);
            component.setHeight(20);
            component.animation.update();
            if (component.animation.getValue() > 0) {
                float componentOffset = 0;
                for (Component component2 : component.getComponents()) {
                    if (component2.isVisible())
                        componentOffset += component2.getHeight();
                }

                componentOffset *= component.animation.getValue();
                component.setHeight(component.getHeight() + componentOffset);
            }
            component.render(stack, mouseX, mouseY);
            offset += component.getHeight() + 3.5f;
            Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        }
        animatedScrool = MathUtil.fast(animatedScrool, scroll, 10);
        float scrollbarHeight = MathHelper.clamp((height - header - 10) * (height - header - 10) / max, 10, height - header - 10);
        float scrollbarY = getY() + header + (-getScroll() / (max - height + header + 4)) * (height - header - 4 - scrollbarHeight);
        scrollbarHeight = MathHelper.clamp(scrollbarHeight, 10, height - header - 10);
        scrollbarY = MathHelper.clamp(scrollbarY, getY() + header, getY() + height - scrollbarHeight - 4);
        if (max > height - header - 10) {
            setScroll(MathHelper.clamp(getScroll(), -max + height - header - 70, 0));
            setAnimatedScrool(MathHelper.clamp(animatedScrool, -max + height - header - 50, 0));

            if (scroll >= 0) {
                setScroll(0);
                setAnimatedScrool(0);
            }


        } else {
            setScroll(0);
            setAnimatedScrool(0);
        }

        max = offset;

        Scissor.unset();
        Scissor.pop();

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {

        for (ModuleComponent component : modules) {
            if (NuckerDLC.getInstance().getDropDown().searchCheck(component.getFunction().getName()) || !(mouseX >= this.getX()) || !(mouseX <= this.getX() + this.getWidth()) || !(mouseY >= this.getY()) || !(mouseY <= this.getY() + this.getHeight())) continue;

            component.mouseClick(mouseX, mouseY, button);
        }

        if (button == 0) { // ЛКМ
            float header = 55 / 2f;
            float scrollbarHeight = MathHelper.clamp((height - header - 10) * (height - header - 10) / max, 10, height - header - 10);
            float scrollbarY = getY() + header + (-getScroll() / (max - height + header + 4)) * (height - header - 4 - scrollbarHeight);
            scrollbarHeight = MathHelper.clamp(scrollbarHeight, 20, height - header - 10);
            scrollbarY = MathHelper.clamp(scrollbarY, getY() + header, getY() + height - scrollbarHeight - 4);

            if (mouseX >= getX() + getWidth() - 2.5f && mouseX <= getX() + getWidth() + 1.0f && mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                draggingScrollbar = true;
                lastMouseY = mouseY;
            }
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (ModuleComponent component : modules) {
            component.keyPressed(key, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : modules) {
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : modules) {
            component.mouseRelease(mouseX, mouseY, button);
        }
        if (button == 0) { // ЛКМ
            draggingScrollbar = false;
        }

    }

}