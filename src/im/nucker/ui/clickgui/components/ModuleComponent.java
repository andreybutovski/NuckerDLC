package im.nucker.ui.clickgui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.functions.api.Function;
import im.nucker.functions.impl.render.HUD;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.*;
import im.nucker.ui.clickgui.Panel;
import im.nucker.ui.clickgui.components.builder.Component;
import im.nucker.ui.clickgui.components.settings.*;
import im.nucker.utils.math.MathUtil;

import im.nucker.utils.math.Vector4i;
import im.nucker.utils.render.Cursors;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.font.Fonts;
import im.nucker.utils.render.Stencil;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.text.BetterText;
import im.nucker.utils.text.font.ClientFonts;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.glfw.GLFW;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;
import im.nucker.utils.client.KeyStorage;

import java.awt.*;
import java.util.List;

@Getter
public class ModuleComponent extends Component {
    private final Vector4f ROUNDING_VECTOR = new Vector4f(5, 5, 5, 5);
    private final Vector4i BORDER_COLOR = new Vector4i(ColorUtils.rgb(45, 46, 53), ColorUtils.rgb(25, 26, 31), ColorUtils.rgb(45, 46, 53), ColorUtils.rgb(25, 26, 31));
    private final Function function;
    protected Panel panel1;
    public Animation expandAnim = new Animation();

    public Animation hoverAnim = new Animation();
    public Animation animation = new Animation();
    public Animation bindAnim = new Animation();
    public Animation noBindAnim = new Animation();

    public boolean open;

    public boolean bind;

    private double openAnimValue = 0.3, noOpenAnimValue = 0.4;

    private final ObjectArrayList<Component> components = new ObjectArrayList<>();

    public ModuleComponent(Function function) {
        this.function = function;
        for (Setting<?> setting : function.getSettings()) {
            if (setting instanceof BooleanSetting bool) {
                components.add(new BooleanComponent(bool));
            }
            if (setting instanceof SliderSetting slider) {
                components.add(new SliderComponent(slider));
            }
            if (setting instanceof BindSetting bind) {
                components.add(new BindComponent(bind));
            }
            if (setting instanceof ModeSetting mode) {
                components.add(new ModeComponent(mode));
            }
            if (setting instanceof ModeListSetting mode) {
                components.add(new MultiBoxComponent(mode));
            }
            if (setting instanceof StringSetting string) {
                components.add(new StringComponent(string));
            }
            if (setting instanceof ColorSetting color) {
                components.add(new ColorComponent(color));
            }


        }
        animation = animation.animate(open ? 1 : 0, 0.3);
    }

    public void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        if (animation.getValue() > 0) {
            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(getX() + 0.5f, getY() + 0.5f, getWidth() - 1, getHeight() - 1, ROUNDING_VECTOR, ColorUtils.rgba(23, 23, 23, (int) (255 * 0.33)));
            Stencil.readStencilBuffer(1);
            float y = getY() + 20;
            for (Component component : components) {
                if (component.isVisible()){
                    component.setX(getX());
                    component.setY(y);
                    component.setWidth(getWidth());
                    component.render(stack, mouseX, mouseY );
                    y += component.getHeight();
                }
            }
            Stencil.uninitStencilBuffer();

        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {

        for (Component component : components) {
            component.mouseRelease(mouseX, mouseY, mouse);
        }

        super.mouseRelease(mouseX, mouseY, mouse);
    }

    private boolean hovered = false;

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        int color = ColorUtils.interpolate(ColorUtils.getColor(0), ColorUtils.rgb(161, 164, 177), (float) function.getAnimation().getValue());

        function.getAnimation().update();
        super.render(stack, mouseX, mouseY);

        drawOutlinedRect(mouseX, mouseY, color);
        drawText(stack, color);
        drawComponents(stack, mouseX, mouseY);

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (isHovered(mouseX, mouseY, 20)) {
            if (button == 0) function.toggle();
            if (button == 1) {
                if (!function.getSettings().isEmpty()) {
                    open = !open;
                    animation = animation.animate(open ? 1 : 0, open ? 0.2 : 0.1, Easings.CIRC_OUT);
                }
            }
            if (button == 2) {
                bind = !bind;
            }
        }
        if (isHovered(mouseX, mouseY)) {
            if (open) {
                for (Component component : components) {
                    if (component.isVisible()) component.mouseClick(mouseX, mouseY, button);
                }
            }
        }
        super.mouseClick(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (Component component : components) {
            if (component.isVisible()) component.charTyped(codePoint, modifiers);
        }
        super.charTyped(codePoint, modifiers);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (Component component : components) {
            if (component.isVisible()) component.keyPressed(key, scanCode, modifiers);
        }
        if (bind) {
            if (key == GLFW.GLFW_KEY_DELETE) {
                function.setBind(0);
            } else function.setBind(key);
            bind = false;
        }
        super.keyPressed(key, scanCode, modifiers);
    }


    private void drawOutlinedRect(float mouseX, float mouseY, int color) {
        int alpha = (int) (20 * function.getAnimation().getValue());
        int i = function.isState() ? ColorUtils.setAlpha(ColorUtils.getColor(1), alpha + 55) : ColorUtils.setAlpha(ColorUtils.rgb(153,153,153), 10);

        Vector4i z = new Vector4i(
                ColorUtils.setAlpha(HUD.getColor(90), 65),
                ColorUtils.setAlpha(HUD.getColor(90), 65),
                ColorUtils.setAlpha(HUD.getColor(90), 0),
                ColorUtils.setAlpha(HUD.getColor(90), 0)
        );
        Vector4i disabledColor = new Vector4i(
                ColorUtils.setAlpha(ColorUtils.rgb(153, 153, 153), 15),
                ColorUtils.setAlpha(ColorUtils.rgb(153, 153, 153), 15),
                ColorUtils.setAlpha(ColorUtils.rgb(153, 153, 153), 0),
                ColorUtils.setAlpha(ColorUtils.rgb(153, 153, 153), 0)
        );

        DisplayUtils.drawRoundedRect(
                getX() + 0.7f,
                getY() + 1,
                getWidth() - 2f,
                getHeight() - 2f,
                this.ROUNDING_VECTOR,
                function.isState() ? z : disabledColor
        );


        if (MathUtil.isHovered(mouseX, mouseY, getX(), getY(), getWidth(), 20.0F)) {
            if (!this.hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
                this.hovered = true;
            }
        } else if (this.hovered) {
            GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            this.hovered = false;
        }
    }

    private final BetterText gavno = new BetterText(List.of(
            "...", "...", "..."
    ), 100);

    private void drawText(MatrixStack stack, int color) {
        gavno.update();
        int i = ColorUtils.interpolate(ColorUtils.getColor(0), ColorUtils.rgba(153, 153, 153,15), (float) function.getAnimation().getValue());
        int i2 = function.isState() ? ColorUtils.setAlpha(ColorUtils.getColor(0),  255) : ColorUtils.setAlpha(ColorUtils.rgb(153,153,153), 255);

        float fontWidth = ClientFonts.msMedium[17].getWidth(function.getName());

        ClientFonts.msMedium[17].drawString(stack, !bind ? function.getName() : "",  (int)( getX() + getWidth() / 12f) , getY() + 7.5F, i2);

        if (this.components.stream().filter(Component::isVisible).count() >= 1L) {
            if (bind) {
                Fonts.montserrat.drawCenteredText(stack, (function.getBind() == 0) ? "Bind" + gavno.getOutput().toString() : KeyStorage.getReverseKey(function.getBind()), getX() + getWidth() / 2f, getY() + 7.5F, ColorUtils.rgb(153, 153, 153), 6.0F, 0.1F);
            } else {
                 DisplayUtils.drawImage(new ResourceLocation("expensive/images/3pa.png"),getX() + getWidth() - 15, getY() + 4 + 0.0f, 11f,12f, i);
            }
        } else if (bind) {
            Fonts.montserrat.drawCenteredText(stack, (function.getBind() == 0) ? "Bind" + gavno.getOutput().toString() : KeyStorage.getReverseKey(function.getBind()), getX() + getWidth() / 2f, getY() + 7.5F, ColorUtils.rgb(153, 153, 153), 6.0F, 0.1F);
        }
    }
}