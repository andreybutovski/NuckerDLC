package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeListSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.math.StopWatch;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@FieldDefaults(level = AccessLevel.PRIVATE)
@FunctionRegister(name = "Chest Stealer", type = Category.Player)
public class HWStealer extends Function {
    private final ModeSetting mode = new ModeSetting("Мод", "Умный", "Умный");
    private final BooleanSetting chestClose = new BooleanSetting("Закрывать при полном", true);
    private final SliderSetting stealDelay = new SliderSetting("Задержка", 100, 0, 1000, 1);
    private final BooleanSetting filterLootToggle = new BooleanSetting("Фильтр лута", false).setVisible(() -> mode.is("Умный"));
    private final ModeListSetting filterLoot = new ModeListSetting("Лут",
            new BooleanSetting("Сферы", false),
            new BooleanSetting("Тотемы", false),
            new BooleanSetting("Зелья", false),
            new BooleanSetting("Присмарин шард", false)
    ).setVisible(() -> mode.is("Умный") && filterLootToggle.get());
    private final SliderSetting itemLimit = new SliderSetting("Лимит кол", 12, 1, 64, 1).setVisible(() -> mode.is("Умный"));
    private final SliderSetting missPercent = new SliderSetting("Миссать", 50, 0, 100, 1).setVisible(() -> mode.is("Умный"));
    private final StopWatch timerUtil = new StopWatch();

    public HWStealer() {
        addSettings(mode, chestClose, stealDelay, filterLootToggle, filterLoot, itemLimit, missPercent);
    }

    private boolean filterItem(Item item) {
        if (!filterLootToggle.get()) {
            return true;
        }

        boolean filterOres = filterLoot.get(0).get();
        boolean filterHeads = filterLoot.get(1).get();
        boolean filterNetherite = filterLoot.get(2).get();
        boolean filterEnchantedBooks = filterLoot.get(3).get();
        boolean filterTotems = filterLoot.get(4).get();
        boolean filterPrismarine_shard = filterLoot.get(5).get();
        boolean filterPotions = filterLoot.get(6).get();

        if (filterOres && (
                item == Items.DIAMOND_ORE ||
                        item == Items.EMERALD_ORE ||
                        item == Items.IRON_ORE ||
                        item == Items.GOLD_ORE ||
                        item == Items.COAL_ORE
        )) {
            return true;
        }

        if (filterHeads && item == Items.PLAYER_HEAD) {
            return true;
        }
        if (filterPrismarine_shard && item == Items.PRISMARINE_SHARD) {
            return true;
        }

        if (filterTotems && item == Items.TOTEM_OF_UNDYING) {
            return true;
        }

        if (filterPotions && (
                item == Items.POTION ||
                        item == Items.SPLASH_POTION
        )) {
            return true;
        }

        return false;
    }

    @Subscribe
    public void onEvent(final EventUpdate event) {
        if (mode.is("Умный")) {
            if (mc.player.openContainer instanceof ChestContainer) {
                ChestContainer container = (ChestContainer) mc.player.openContainer;
                IInventory inventory = container.getLowerChestInventory();
                List<Integer> validSlots = new ArrayList<>();

                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    if (inventory.getStackInSlot(i).getItem() != Item.getItemById(0)
                            && inventory.getStackInSlot(i).getCount() <= itemLimit.get()
                            && filterItem(inventory.getStackInSlot(i).getItem())) {
                        validSlots.add(i);
                    }
                }

                if (!validSlots.isEmpty() && timerUtil.isReached(Math.round(stealDelay.get()))) {
                    int randomIndex = new Random().nextInt(validSlots.size());
                    int slotToSteal = validSlots.get(randomIndex);

                    if (new Random().nextInt(100) >= missPercent.get()) {
                        mc.playerController.windowClick(container.windowId, slotToSteal, 0, ClickType.QUICK_MOVE, mc.player);
                    }

                    timerUtil.reset();
                }

                if (inventory.isEmpty() && chestClose.get()) {
                    mc.player.closeScreen();
                }
            }
        }
    }
}