package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

@FunctionRegister(name = "NoSlowFt", type = Category.Movement)
public class NoSlowFt extends Function {

    private static final UUID NO_SLOW_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final AttributeModifier NO_SLOW_MODIFIER =
            new AttributeModifier(NO_SLOW_UUID, "NoSlow", 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Subscribe
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        ModifiableAttributeInstance speedAttribute = mc.player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(NO_SLOW_UUID);

            if (!speedAttribute.hasModifier(NO_SLOW_MODIFIER)) {
                speedAttribute.removeModifier(NO_SLOW_MODIFIER);
            }
        }

        // Убираем замедление при использовании предметов
        if (mc.player.isUsingItem()) {
            mc.player.setSprinting(true);
        }

        // Убираем замедление при атаке
        if (mc.player.isBlocking()) {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;

        ModifiableAttributeInstance speedAttribute = mc.player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(NO_SLOW_UUID);
        }
    }
}
