
package im.nucker.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@FunctionRegister(
        name = "Inventory Sorter",
        type = Category.Player
)
public class InventorySorter extends Function {
    private static final Set<Item> EXCLUDED_ITEMS;
    private boolean isEnabled = true;

    public InventorySorter() {
    }

    public void toggleFunction() {
        this.isEnabled = !this.isEnabled;
    }

    @Subscribe
    public void onEventUpdate(EventUpdate event) {
        if (this.isEnabled) {
            Minecraft.getInstance();
            PlayerEntity player = Minecraft.player;
            if (player != null) {
                if (Minecraft.getInstance().currentScreen instanceof InventoryScreen) {
                    InventoryScreen inventoryScreen = (InventoryScreen)Minecraft.getInstance().currentScreen;

                    for(int i = 0; i < player.container.getInventory().size(); ++i) {
                        if (player.container.getSlot(i).getHasStack()) {
                            ItemStack stack = player.container.getSlot(i).getStack();
                            if (!EXCLUDED_ITEMS.contains(stack.getItem())) {
                                Minecraft.getInstance().playerController.windowClick(0, i, 0, ClickType.PICKUP, player);
                                Minecraft.getInstance().playerController.windowClick(0, -999, 0, ClickType.PICKUP, player);
                            }
                        }
                    }
                }

            }
        }
    }

    static {
        EXCLUDED_ITEMS = new HashSet(Arrays.asList(Items.TNT, Items.SPAWNER, Items.JACK_O_LANTERN,Items.TRIPWIRE_HOOK, Items.ENDER_CHEST, Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.SNOWBALL, Items.COMPASS, Items.ENDER_PEARL, Items.ENDER_EYE, Items.EXPERIENCE_BOTTLE, Items.PLAYER_HEAD, Items.NETHER_STAR, Items.FIREWORK_STAR, Items.FIREWORK_ROCKET, Items.PRISMARINE_SHARD, Items.ELYTRA, Items.SHIELD, Items.TRIDENT, Items.TOTEM_OF_UNDYING, Items.CROSSBOW, Items.ANCIENT_DEBRIS, Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW, Items.LINGERING_POTION, Items.SPLASH_POTION, Items.POTION, Items.LINGERING_POTION, Items.GOLDEN_CARROT, Items.FEATHER, Items.CHORUS_FRUIT, Items.WITHER_SKELETON_SKULL, Items.GOLDEN_HELMET));
    }
}
