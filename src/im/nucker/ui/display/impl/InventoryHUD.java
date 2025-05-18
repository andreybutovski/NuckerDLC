package im.nucker.ui.display.impl;

import im.nucker.events.EventUpdate;
import im.nucker.functions.impl.render.HUD;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.DisplayUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.nucker.events.EventDisplay;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.math.Vector4i;
import im.nucker.utils.render.font.Fonts;
import im.nucker.utils.text.GradientUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

@FieldDefaults(level = AccessLevel.PRIVATE)

public class InventoryHUD implements ElementRenderer {
    private final int INVENTORY_ROWS = 3;
    private final int INVENTORY_COLUMNS = 9;
    private final int SLOT_SIZE = 18;

    @Override
    public void update(EventUpdate e) {

    }

    private final int SLOT_PADDING = 2;
    final Dragging dragging;
    final ResourceLocation logo = new ResourceLocation("expensive/images/HUD2/bag2.png");
    float width;
    float height;
    private final Minecraft mc = Minecraft.getInstance();
    float padding = 5;

    final float iconSize = 10;

    public InventoryHUD(final Dragging dragging) {
        this.dragging = dragging;
    }


    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 6.5f;
        float padding = 5;

        ITextComponent name = new StringTextComponent("Inventory").mergeStyle(TextFormatting.WHITE);
        ITextComponent name2 = GradientUtil.gradient("A");

        NonNullList<ItemStack> inventorySlots = mc.player.inventory.mainInventory;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLUMNS; col++) {
                float slotX = posX + 3.5f + col * (SLOT_SIZE + SLOT_PADDING); // Начальная позиция слота по X
                float slotY = posY + height - 62 + row * (SLOT_SIZE + SLOT_PADDING); // Начальная позиция слота по Y

                // Получение индекса слота в инвентаре
                int slotIndex = col + (row + 1) * INVENTORY_COLUMNS;

                // Получение предмета в текущем слоте инвентаря
                ItemStack itemStack = inventorySlots.get(slotIndex);

                // Сдвиг отображения предмета на 2 пикселя вправо и на 1 пиксель вниз
                float itemRenderX = slotX + 2.7f; // Сдвиг на 2 пикселя вправо
                float itemRenderY = slotY + 2f; // Сдвиг на 1 пиксель вниз

                // Размеры предмета уменьшены на 20%
                float scaleFactor = 0.8f;
                int scaledWidth = (int) (16 * scaleFactor);
                int scaledHeight = (int) (16 * scaleFactor);

                // Рендеринг предмета в слоте
                if (!itemStack.isEmpty()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(scaleFactor, scaleFactor, scaleFactor); // Уменьшаем размеры предмета
                    mc.getItemRenderer().renderItemIntoGUI(itemStack, (int) (itemRenderX / scaleFactor), (int) (itemRenderY / scaleFactor));
                    GlStateManager.popMatrix();

                    int count = itemStack.getCount();
                    if (count > 1) {
                        String countString = String.valueOf(count);
                        float countX = itemRenderX + (count > 9 ? 8.5f : 11f);
                        float countY = itemRenderY + (count > 9 ? 10f : 10f);

                        // Уменьшаем размер шрифта, если количество больше 9
                        float countScale = count > 9 ? 0.6f : 0.65f;

                        // Сохраняем текущую матрицу и масштабируем её
                        RenderSystem.pushMatrix();
                        RenderSystem.scalef(countScale, countScale, countScale);

                        // Рисуем текст с тенью с новым размером шрифта
                        mc.fontRenderer.drawStringWithShadow(eventDisplay.getMatrixStack(), countString, countX / countScale, countY / countScale, 0xFFFFFF);

                        // Восстанавливаем исходную матрицу
                        RenderSystem.popMatrix();
                    }



// Рендеринг предмета в слоте
                    if (!itemStack.isEmpty()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scalef(scaleFactor, scaleFactor, scaleFactor); // Уменьшаем размеры предмета
                        mc.getItemRenderer().renderItemIntoGUI(itemStack, (int) (itemRenderX / scaleFactor), (int) (itemRenderY / scaleFactor));
                        GlStateManager.popMatrix();
                    }

                }

                // Рендеринг квадрата слота
                DisplayUtils.drawRoundedRect(slotX, slotY, SLOT_SIZE, SLOT_SIZE, 5, 0x55CCCCCC);
            }
        }


        Vector4i colors = new Vector4i(HUD.getColor(0, 10), HUD.getColor(90, 10), HUD.getColor(180, 10), HUD.getColor(270, 10));

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        drawStyledRect3(posX, posY + 1.0f, width, 14, 3);
        //Fonts.icons2.drawCenteredText(ms, name2, posX + 10, posY + 3.5f,8.5f);
        Fonts.sfui.drawCenteredText(ms, name, posX + width / 2, posY + padding + 0.5f, 6.5f);
        DisplayUtils.drawRectVerticalW(posX + 18.0f, posY + 3, 1, 10.0f, 3, ColorUtils.rgba(255, 255, 255, (int) (100 * 0.75f)));
        DisplayUtils.drawImage(logo, posX + 5f, posY + 4.2F, iconSize, iconSize, ColorUtils.rgba(170, 165, 228,255));


        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;



        Scissor.unset();
        Scissor.pop();
        width = Math.max(maxWidth, 185);
        height = localHeight + 65.0f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }

    private void drawStyledRect3(float x, float y, float width, float height, float radius) {
        DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1, height + 1, radius + 0.0f,
                ColorUtils.setAlpha(ColorUtils.rgb(10, 15, 13), 90));
        DisplayUtils.drawRoundedRect(x, y, width, height, radius, ColorUtils.rgba(9, 8, 23, 255));
        //DisplayUtils.drawShadow(x - 2, y - 2, width + 4, height + 4, 0, ColorUtils.rgba(10, 15, 13, 95));
    }




    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {

        DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1, height + 1, radius + 0.5f, ColorUtils.getColor(0)); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height, radius, ColorUtils.rgba(21, 21, 21, 255));
    }
}

