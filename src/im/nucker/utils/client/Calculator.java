package im.nucker.utils.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

@UtilityClass
public class Calculator {
    private double armor(ItemStack stack) {
        if (!stack.isEnchanted()) return 0.0;
        if (!(stack.getItem() instanceof ArmorItem armor)) return 0.0;

        return armor.getDamageReduceAmount() + ((double) EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25);
    }

    public double armor(LivingEntity entity) {
        double armor = entity.getTotalArmorValue();

        for (ItemStack item : entity.getArmorInventoryList()) {
            armor += armor(item);
        }

        return armor;
    }

    public double health(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public double buffs(LivingEntity entity) {
        double buffs = 0.0;

        for (EffectInstance effect : entity.getActivePotionEffects()) {
            if (effect.getPotion() == Effects.ABSORPTION) {
                buffs += 1.2 * (effect.getAmplifier() + 1);
            } else if (effect.getPotion() == Effects.RESISTANCE) {
                buffs += 1.0 * (effect.getAmplifier() + 1);
            } else if (effect.getPotion() == Effects.REGENERATION) {
                buffs += 1.1 * (effect.getAmplifier() + 1);
            }
        }

        return buffs;
    }

    public double entity(LivingEntity entity, boolean health, boolean armor, boolean distance, double maxDistance, boolean buffs) {
        double a = 1.0, b = 1.0, c = 1.0, d = 1.0;

        if (health) a += health(entity);
        if (armor) b += armor(entity);
        if (distance) c += entity.getDistanceSq(Minecraft.getInstance().player) / maxDistance;
        if (buffs) d += buffs(entity);

        return (a * b * d) * c;
    }
}