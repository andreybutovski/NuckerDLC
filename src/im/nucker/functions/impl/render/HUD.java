package im.nucker.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeListSetting;
import im.nucker.ui.display.impl.*;
import im.nucker.ui.styles.StyleManager;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.render.ColorUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;

@FieldDefaults(level = AccessLevel.PRIVATE)
@FunctionRegister(name = "HUD", type = Category.Render)
public class HUD extends Function {

    private final ModeListSetting elements = new ModeListSetting("Элементы",
            new BooleanSetting("Ватермарка", true),
            new BooleanSetting("Координаты", true),
            new BooleanSetting("Эффекты", true),
            new BooleanSetting("Список модерации", true),
            new BooleanSetting("Активные бинды", true),
            new BooleanSetting("Активный таргет", true),
            new BooleanSetting("Список модулей", true),
            new BooleanSetting("Броня", true),
            new BooleanSetting("Инвентарь", true)
    );

    final WatermarkRenderer watermarkRenderer;
    final CoordsRenderer coordsRenderer;
    final PotionRenderer potionRenderer;
    final KeyBindRenderer keyBindRenderer;
    final ArmorRenderer armorRenderer;
    final ArrayListRenderer arrayListRenderer;
    final StaffListRenderer staffListRenderer;
    final TargetInfoRenderer targetInfoRenderer;
    final InventoryHUD inventoryHUD;



    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        if (elements.getValueByName("Список модерации").get()) staffListRenderer.update(e);
    }


    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (mc.gameSettings.showDebugInfo || e.getType() != EventDisplay.Type.POST) {
            return;
        }

        if (elements.getValueByName("Координаты").get()) coordsRenderer.render(e);
        if (elements.getValueByName("Эффекты").get()) potionRenderer.render(e);
        if (elements.getValueByName("Ватермарка").get()) watermarkRenderer.render(e);
        if (elements.getValueByName("Активные бинды").get()) keyBindRenderer.render(e);
        if (elements.getValueByName("Активный таргет").get()) targetInfoRenderer.render(e);
        if (elements.getValueByName("Координаты").get()) coordsRenderer.render(e);
        if (elements.getValueByName("Броня").get()) armorRenderer.render(e);
        if (elements.getValueByName("Список модулей").get()) arrayListRenderer.render(e);
        if (elements.getValueByName("Список модерации").get()) staffListRenderer.render(e);
        if (elements.getValueByName("Инвентарь").get()) inventoryHUD.render(e);

    }

    public HUD() {
        coordsRenderer = new CoordsRenderer();
        Dragging potions = NuckerDLC.getInstance().createDrag(this, "Эффекты", 278, 5);
        Dragging keyBinds = NuckerDLC.getInstance().createDrag(this, "Активные бинды", 379, 5);
        Dragging dragging = NuckerDLC.getInstance().createDrag(this, "Активный таргет", 80, 128);
        Dragging armor =  NuckerDLC.getInstance().createDrag(this, "Броня", 96, 5);
        Dragging staff =  NuckerDLC.getInstance().createDrag(this, "Список модерации", 480, 5);
        Dragging inv =  NuckerDLC.getInstance().createDrag(this, "Инвентарь", 100, 5);
        Dragging ar = NuckerDLC.getInstance().createDrag(this, "Список модулей", 195, 5);
        potionRenderer = new PotionRenderer(potions);
        arrayListRenderer = new ArrayListRenderer();
        armorRenderer = new ArmorRenderer();
        watermarkRenderer = new WatermarkRenderer();
        staffListRenderer = new StaffListRenderer(staff);
        keyBindRenderer = new KeyBindRenderer(keyBinds);
        targetInfoRenderer = new TargetInfoRenderer(dragging);
        inventoryHUD = new InventoryHUD(inv);
        addSettings(elements);
    }
    public static int getColor(int index) {
        StyleManager styleManager = NuckerDLC.getInstance().getStyleManager();
        return ColorUtils.gradient(styleManager.getCurrentStyle().getFirstColor().getRGB(), styleManager.getCurrentStyle().getSecondColor().getRGB(), index * 16, 10);
    }

    public static int getColor(int index, float mult) {
        StyleManager styleManager = NuckerDLC.getInstance().getStyleManager();
        return ColorUtils.gradient(styleManager.getCurrentStyle().getFirstColor().getRGB(), styleManager.getCurrentStyle().getSecondColor().getRGB(), (int) (index * mult), 10);
    }

    public static int getColor(int firstColor, int secondColor, int index, float mult) {
        return ColorUtils.gradient(firstColor, secondColor, (int) (index * mult), 10);
    }
}