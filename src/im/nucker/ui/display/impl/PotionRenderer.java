package im.nucker.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.styles.Style;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.font.Fonts;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.gui.DisplayEffectsScreen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PotionRenderer implements ElementRenderer {

    final Dragging dragging;

    private final ResourceLocation logo = new ResourceLocation("expensive/images/HUD2/potions.png");
    final float iconSize = 10;

    float width;
    float height;

    Map<String, Animation> effectAnimations = new HashMap<>();

    @Override
    public void update(EventUpdate e) {

    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 7.6f;
        float padding = 5;

        ITextComponent name = new StringTextComponent("Potions").mergeStyle(TextFormatting.WHITE);
        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();

        DisplayUtils.drawShadow(posX, posY, width, height, 7, ColorUtils.rgba(9, 8, 23, 1));
        DisplayUtils.drawRoundedRect(posX - 1.3f, posY - 1.3f, width + 2.8f, height + 2.8f, 5, ColorUtils.rgb(46, 45, 58));
        DisplayUtils.drawRoundedRect(posX - 0.5f, posY - 0.5f, width + 1f, height + 1f, 4, ColorUtils.rgb(9, 8, 23));
        DisplayUtils.drawImage(logo, posX + 65f, posY + padding, iconSize, iconSize, ColorUtils.rgba(170, 165, 228,255));

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        Fonts.sfui.drawCenteredText(ms, name, posX + width / 4, posY + padding + 0.5f, fontSize + 0.45f);
        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfui.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;

        DisplayUtils.drawRectHorizontalW(posX + 0.5f, posY, width - 1, 1.5f, 3, ColorUtils.rgba(46, 45, 58, 255));
        posY += 4f;

        for (EffectInstance ef : mc.player.getActivePotionEffects()) {
            String key = ef.getEffectName() + "_" + ef.getAmplifier();
            Animation anim = effectAnimations.get(key);
            if (anim == null) {
                anim = new Animation();
                effectAnimations.put(key, anim);
            }
            anim.update();

            if (anim.getValue() <= 0)
                continue;

            int amp = ef.getAmplifier();
            String ampStr = "";
            if (amp >= 1 && amp <= 9) {
                ampStr = " " + I18n.format("enchantment.level." + (amp + 1));
            }
            String nameText = I18n.format(ef.getEffectName()) + ampStr;
            float nameWidth = Fonts.sfui.getWidth(nameText, fontSize);

            String durationText = EffectUtils.getPotionDurationString(ef, 1);
            float durationWidth = Fonts.sfui.getWidth(durationText, fontSize);

            TextureAtlasSprite potionSprite = mc.getPotionSpriteUploader().getSprite(ef.getPotion());
            mc.getTextureManager().bindTexture(potionSprite.getAtlasTexture().getTextureLocation());

            DisplayEffectsScreen.blit(ms, (int) (posX - 1), (int) posY + 2, 8, 8, 8, potionSprite);

            float localWidth = nameWidth + durationWidth + padding * 3;
            Fonts.sfui.drawText(ms, nameText, posX + padding + 3, posY + 0.5f, ColorUtils.rgba(255, 255, 255, (int) (255 * anim.getValue())), fontSize + 0.1f);
            Fonts.sfui.drawText(ms, durationText, posX + width - padding - durationWidth - 0.3F, posY + 0.5f, ColorUtils.rgba(255, 255, 255, (int) (255 * anim.getValue())), fontSize + 0.1f);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (fontSize + padding) * anim.getValue();
            localHeight += (fontSize + padding) * anim.getValue();
        }
        Scissor.unset();
        Scissor.pop();

        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);

        cleanUpAnimations();
    }

    private void cleanUpAnimations() {
        Map<String, Boolean> activeKeys = new HashMap<>();
        for (EffectInstance ef : mc.player.getActivePotionEffects()) {
            String key = ef.getEffectName() + "_" + ef.getAmplifier();
            activeKeys.put(key, true);
        }
        Iterator<String> iterator = effectAnimations.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!activeKeys.containsKey(key)) {
                iterator.remove();
            }
        }
    }

    public static class Animation {
        @Getter
        private float value = 0.0f;
        private final float speed = 0.05f;

        public void update() {
            if (value < 1.0f) {
                value += speed;
                if (value > 1.0f) {
                    value = 1.0f;
                }
            }
        }
    }
}
