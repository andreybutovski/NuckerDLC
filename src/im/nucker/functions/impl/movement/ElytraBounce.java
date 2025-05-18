package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;

@FunctionRegister(
        name = "ElytraBounce",
        type = Category.Movement
)
public class ElytraBounce extends Function {

    public ElytraBounce() {
        addSettings();
    }
    ItemStack currentStack = ItemStack.EMPTY;

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.currentStack = Minecraft.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.currentStack.getItem() == Items.ELYTRA) {
            if (Minecraft.player.isOnGround() && !Minecraft.player.isInWater() && !Minecraft.player.isSwimming()) {
                Minecraft.player.jump();
                Minecraft.player.rotationPitchHead = -90.0f;
            } else if (ElytraItem.isUsable(this.currentStack) && !Minecraft.player.isElytraFlying() && !Minecraft.player.isInWater() && !Minecraft.player.isSwimming()) {
                Minecraft.player.startFallFlying();
                Minecraft.player.connection.sendPacket(new CEntityActionPacket(Minecraft.player, CEntityActionPacket.Action.START_FALL_FLYING));
                Minecraft.player.rotationPitchHead = -90.0f;
            }
        }
    }

}