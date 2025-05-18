package im.nucker.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.command.friends.FriendStorage;
import im.nucker.events.EventInput;
import im.nucker.events.EventMotion;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeListSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import im.nucker.utils.math.SensUtils;
import im.nucker.utils.math.StopWatch;
import im.nucker.utils.player.InventoryUtil;
import im.nucker.utils.player.MouseUtil;
import im.nucker.utils.player.MoveUtils;
import lombok.Getter;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.hypot;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

@FunctionRegister(name = "KillAura", type = Category.Combat)
public class KillAura extends Function {
    @Getter
    private static final ModeSetting type = new ModeSetting("Тип", "Плавная", "Плавная", "Snap","ReallyWorld","FunSky","HVH","Legit","FUNTIME360SNAP","SpookyTime");
    private final SliderSetting attackRange = new SliderSetting("Дистанция аттаки", 3f, 3f, 6f, 0.1f);
    final ModeListSetting targets = new ModeListSetting("Таргеты",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Голые", true),
            new BooleanSetting("Мобы", false),
            new BooleanSetting("Животные", false),
            new BooleanSetting("Друзья", false),
            new BooleanSetting("Голые невидимки", true),
            new BooleanSetting("Невидимки", true));
    private final BooleanSetting fovMode = new BooleanSetting("Мод FOV", false);
    public static final SliderSetting rotationFoV = new SliderSetting(
            "FOV",
            90f,
            10f,
            180f,
            1f
    ).setVisible(() -> type.is("Legit"));
    @Getter
    final ModeListSetting options = new ModeListSetting("Опции",
            new BooleanSetting("Только криты", true),
            new BooleanSetting("Ломать щит", true),
            new BooleanSetting("Отжимать щит", true),
            new BooleanSetting("Ускорять ротацию", false),
            new BooleanSetting("TPS Sync", false),
            new BooleanSetting("Фокусировать одну цель", true),
            new BooleanSetting("Коррекция движения", true));
    final ModeSetting correctionType = new ModeSetting("Тип коррекции", "Незаметный", "Незаметный", "Сфокусированный");

    @Getter
    private final StopWatch stopWatch = new StopWatch();
    private Vector2f rotateVector = new Vector2f(0, 0);
    @Getter
    private LivingEntity target;
    private Entity selected;

    int ticks = 0;
    boolean isRotated;

    final AutoPotion autoPotion;

    public KillAura(AutoPotion autoPotion) {
        this.autoPotion = autoPotion;
        addSettings(type, attackRange, targets, options, correctionType, rotationFoV.setVisible(fovMode::get), fovMode.setVisible(() -> type.is("Legit")));

    }

    @Subscribe
    public void onInput(EventInput eventInput) {
        if (options.getValueByName("Коррекция движения").get() && correctionType.is("Незаметная") && target != null && mc.player != null) {
            MoveUtils.fixMovement(eventInput, rotateVector.x);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (options.getValueByName("Фокусировать одну цель").get() && (target == null || !isValid(target)) || !options.getValueByName("Фокусировать одну цель").get()) {
            updateTarget();
        }


        if (target != null && !(autoPotion.isState() && autoPotion.isActive())) {
            isRotated = false;


// Логика для атаки
            if (type.is("Legit")) {
                if (shouldPlayerFalling() && (stopWatch.hasTimeElapsed())) {
                    updateAttack();
                    ticks = 1;  // Сделаем удары быстрее, уменьшая количество тиков
                }
                if (ticks > 0) {
                    updateRotation(true, 180, 90);
                    ticks--;
                } else {
                    reset();
                }
            } else {
                if (shouldPlayerFalling() && (stopWatch.hasTimeElapsed())) {
                    updateAttack();
                    ticks = 2;  // Ускоряем атаки для других типов
                }
                if (!isRotated) {
                    updateRotation(false, 80, 35);
                }
            }
        } else {
            stopWatch.setLastMS(0);
            reset();
        }
    }

    @Subscribe
    private void onWalking(EventMotion e) {
        if (target == null || autoPotion.isState() && autoPotion.isActive()) return;

        float yaw = rotateVector.x;
        float pitch = rotateVector.y;

        e.setYaw(yaw);
        e.setPitch(pitch);
        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;
        mc.player.rotationPitchHead = pitch;
    }

    private void updateTarget() {
        List<LivingEntity> potentialTargets = new ArrayList<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isValid(living)) {
                if (type.is("Legit") && !isInFoV(living)) continue;
                potentialTargets.add(living);
            }
        }

        if (potentialTargets.isEmpty()) {
            target = null;
            return;
        }


        potentialTargets.sort(Comparator.comparingDouble(object -> {
            if (object instanceof PlayerEntity player) {
                return -getEntityArmor(player);
            }
            return -((LivingEntity) object).getTotalArmorValue();
        }).thenComparing((object1, object2) -> {
            double health1 = getEntityHealth((LivingEntity) object1);
            double health2 = getEntityHealth((LivingEntity) object2);
            return Double.compare(health1, health2);
        }).thenComparingDouble(object -> mc.player.getDistance((Entity) object)));

        target = potentialTargets.get(0);
    }


    public boolean isInFoV(LivingEntity entity) {
        if (!fovMode.get()) {
            return true;
        }
        Vector3d targetVec = entity.getPositionVec().subtract(mc.player.getEyePosition(1.0F));

        float targetYaw = (float) Math.toDegrees(Math.atan2(targetVec.z, targetVec.x)) - 90.0F;

        float playerYaw = mc.player.rotationYaw;

        float deltaYaw = Math.abs(wrapDegrees(targetYaw - playerYaw));

        return deltaYaw <= rotationFoV.get();
    }
    float lastYaw, lastPitch;

    private void updateRotation(boolean attack, float rotationYawSpeed, float rotationPitchSpeed) {
        Vector3d vec = target.getPositionVec().add(0, clamp(mc.player.getPosYEye() - target.getPosY(),
                        0, target.getHeight() * (mc.player.getDistanceEyePos(target) / attackRange.get())), 0)
                .subtract(mc.player.getEyePosition(1.0F));

        isRotated = true;

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, hypot(vec.x, vec.z))));

        float yawDelta = (wrapDegrees(yawToTarget - rotateVector.x));
        float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.y));
        int roundedYaw = (int) yawDelta;

        switch (type.get()) {
            case "Плавная" -> {
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed);

                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);
                } else {
                    clampedPitch /= 3f;
                }


                if (Math.abs(clampedYaw - this.lastYaw) <= 0f) {
                    clampedYaw = this.lastYaw + 0f;
                }

                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);


                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;


                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }
            case "FunSky" -> {
                // Ограничение минимальных и максимальных значений для yaw и pitch
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed);

                // Ускоряем ротацию при атаке, если установлено соответствующее значение
                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);  // Ускоряем только pitch (наклон)
                } else {
                    clampedPitch /= 3f;  // Если не атакуем, делаем плавную ротацию
                }

                // Приводим yaw к корректному значению
                if (Math.abs(clampedYaw - this.lastYaw) <= 0f) {
                    clampedYaw = this.lastYaw;  // Используем последнее значение yaw для стабильности
                }

                // Вычисляем новый yaw с учетом скорости поворота
                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);

                // Применяем корректировку для yaw в диапазоне от -180 до 180
                if (yaw > 180) yaw -= 360;  // Если yaw превышает 180, то корректируем
                else if (yaw < -180) yaw += 360;  // Если yaw меньше -180, то корректируем

                // Применяем плавную коррекцию для pitch, ограничиваем диапазон
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);

                // Определяем наибольший общий делитель (GCD) для минимизации погрешностей
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;  // Уменьшаем погрешности yaw
                pitch -= (pitch - rotateVector.y) % gcd;  // Уменьшаем погрешности pitch

                // Обновляем угол поворота
                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;  // Сохраняем значение yaw для следующего кадра
                lastPitch = clampedPitch;  // Сохраняем значение pitch для следующего кадра

                // Если активирована опция коррекции движения, обновляем yaw
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;  // Обновляем yaw для движения
                }

                // Ускорение критических ударов
                if (attack && options.getValueByName("Ускорять удары").get()) {
                    // Проверяем, если доступна сила удара или критический удар
                    float attackStrength = mc.player.getCooledAttackStrength(0);  // Получаем текущую силу атаки
                    if (attackStrength >= 1.0F) {
                        // Если сила атаки достигла максимума, сразу совершаем удар
                        mc.player.swingArm(mc.MAIN_HAND);  // Выполним удар сразу
                    } else {
                        // Ускоряем регенерацию силы атаки
                        // Используем небольшой задержки для ускорения атаки
                        // Ожидаем, пока сила атаки не восстановится до 1.0
                        mc.player.attackEntityFrom(DamageSource.causePlayerDamage(mc.player), 0); // Просто вызываем атаку (эффект будет зависеть от контекста)
                    }
                }
            }


            case "ReallyWorld" -> {
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed) * 1.2f; // Увеличиваем скорость
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed) * 1.1f;

                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f) * 1.3f; // Дополнительное ускорение
                } else {
                    clampedPitch /= 2.8f;
                }

                if (Math.abs(clampedYaw - this.lastYaw) <= 0.01f) {
                    clampedYaw = this.lastYaw + 0.01f; // Мелкий обход детекта
                }

                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);

                // GCD обход
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % (gcd + 0.0001f); // Мелкий сдвиг для обхода античита
                pitch -= (pitch - rotateVector.y) % (gcd + 0.0001f);

                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;

                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }

            case "HVH" -> {
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed);

                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);
                } else {
                    clampedPitch /= 3f;
                }


                if (Math.abs(clampedYaw - this.lastYaw) <= 0f) {
                    clampedYaw = this.lastYaw + 0f;
                }

                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);


                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;


                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }
            case "Snap" -> {
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0.2f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0.2f), rotationPitchSpeed);

                if (attack && selected != target && options.getValueByName("Ускорять ротацию при атаке").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);
                } else {
                    clampedPitch /= 3f;
                }


                if (Math.abs(clampedYaw - this.lastYaw) <= 0.0f) {
                    clampedYaw = this.lastYaw + 0.0f;
                }

                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -360.0F, 360.0F);


                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;


                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }
            case "FUNTIME360SNAP" -> {
                // Применяем clamping для yaw и pitch
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed);

                // Проверяем опцию для ускоренной ротации
                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);
                } else {
                    clampedPitch /= 3f;
                }

                // Обработка clampedYaw в случае минимальных изменений
                if (Math.abs(clampedYaw - this.lastYaw) <= 0f) {
                    clampedYaw = this.lastYaw + 0.1f;  // Устанавливаем минимальное изменение
                }

                // Расчет углов вращения для yaw и pitch
                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);

                // Используем GCD для корректировки значений углов
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;

                // Применяем новое вращение
                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;

                // Проверка на коррекцию движения, если она включена
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                    mc.player.setSprinting(false);
                }
            }
            case "Legit" -> {
                float yawDeltaAbs = Math.abs(roundedYaw);
                float pitchDeltaAbs = Math.abs(pitchDelta);

                // Ускоренная регулировка скорости поворота
                float yawSpeedFactor = Math.max(0.75f, 1.4f - (yawDeltaAbs / 100f)); // Чем дальше враг, тем быстрее доводка
                float pitchSpeedFactor = 0.85f; // Чуть выше скорость доводки по высоте

                float smoothedYaw = roundedYaw * yawSpeedFactor;
                float smoothedPitch = pitchDelta * pitchSpeedFactor;

                // Легкие вариации для обхода детекта
                smoothedYaw += (float) Math.random() * 0.15f - 0.075f;
                smoothedPitch += (float) Math.random() * 0.1f - 0.05f;

                // Ускоренные криты (быстрая доводка при атаке)
                if (attack && selected == target) {
                    smoothedPitch *= 1.4f; // Быстрее доводка вверх-вниз
                    smoothedYaw *= 1.25f;  // Быстрее доводка в сторону
                }

                float yaw = rotateVector.x + smoothedYaw;
                float pitch = clamp(rotateVector.y + smoothedPitch, -65, 95);

                // Коррекция GCD (антидетект)
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % (gcd + 0.0001f);
                pitch -= (pitch - rotateVector.y) % (gcd + 0.0001f);

                // Уменьшаем дерганность при малых углах доводки
                if (yawDeltaAbs < 4f) yaw *= 0.92f;
                if (pitchDeltaAbs < 2f) pitch *= 0.88f;

                // Естественная наводка при движении (ускоренная коррекция)
                if (mc.player.isSprinting()) {
                    yaw += mc.player.moveStrafing * 0.15f;
                }

                rotateVector = new Vector2f(yaw, pitch);

                if (options.getValueByName("Ускорять ротацию").get()) {
                    mc.player.rotationYawOffset = yaw;
                    mc.player.setSprinting(false);
                    ;
                }
            }
            case "SpookyTime" -> {
                // Применяем clamping для yaw и pitch
                float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 0f), rotationPitchSpeed);

                // Проверяем опцию для ускоренной ротации
                if (attack && selected != target && options.getValueByName("Ускорять ротацию").get()) {
                    clampedPitch = Math.max(Math.abs(pitchDelta), 0f);
                } else {
                    clampedPitch /= 2f;
                }

                // Обработка clampedYaw в случае минимальных изменений
                if (Math.abs(clampedYaw - this.lastYaw) <= 0f) {
                    clampedYaw = this.lastYaw + 0.5f;  // Устанавливаем минимальное изменение
                }

                // Расчет углов вращения для yaw и pitch
                float yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -65, 90);

                // Используем GCD для корректировки значений углов
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;

                // Применяем новое вращение
                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;

                // Проверка на коррекцию движения, если она включена
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;

                    mc.player.setSprinting(false);
                    ;
                }
            }


        };
    }


    private void attackTarget(Entity target) {

    }


    private void updateAttack() {
        // Получаем объект под мышкой, в пределах атакующего диапазона
        selected = MouseUtil.getMouseOver(target, rotateVector.x, rotateVector.y, attackRange.get());

        // Если опция "Ускорять ротацию" включена, обновляем ротацию с заданными параметрами
        if (options.getValueByName("Ускорять ротацию").get()) {
            updateRotation(true, 75, 45);
        }

        // Если выбранный объект не совпадает с целью или цель не выбрана, а игрок не летит на Элитре — выходим
        if (selected != target && !mc.player.isElytraFlying()) {
            return;
        }

        // Если игрок блокирует щитом и опция "Отжимать щит" активирована, прекращаем блокировку
        if (mc.player.isBlocking() && options.getValueByName("Отжимать щит").get()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }

        // Устанавливаем таймер с задержкой
        stopWatch.setLastMS(500);

        // Атакуем цель
        mc.playerController.attackEntity(mc.player, target);

        // Игрок выполняет анимацию удара
        mc.player.swingArm(Hand.MAIN_HAND);

        // Если цель — это игрок и опция "Ломать щит" активирована, ломаем щит игрока
        if (target instanceof PlayerEntity player && options.getValueByName("Ломать щит").get()) {
            breakShieldPlayer(player);
        }
    }


    private boolean shouldPlayerFalling() {
        boolean cancelReason = mc.player.isInWater() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.isInLava() || mc.player.isOnLadder() || mc.player.isPassenger() || mc.player.abilities.isFlying;

        float attackStrength = mc.player.getCooledAttackStrength(options.getValueByName("TPS Sync").get()
                ? NuckerDLC.getInstance().getTpsCalc().getAdjustTicks() : 1.5f);

        if (attackStrength < 0.92f) {
            return false;
        }

        if (!cancelReason && options.getValueByName("Только криты").get()) {
            return !mc.player.isOnGround() && mc.player.fallDistance > 0;
        }

        return true;
    }

    private boolean isValid(LivingEntity entity) {
        if (entity instanceof ClientPlayerEntity) return false;

        if (entity.ticksExisted < 3) return false;
        if (mc.player.getDistanceEyePos(entity) > attackRange.get()) return false;

        if (entity instanceof PlayerEntity p) {
            if (AntiBot.isBot(entity)) {
                return false;
            }
            if (!targets.getValueByName("Друзья").get() && FriendStorage.isFriend(p.getName().getString())) {
                return false;
            }
            if (p.getName().getString().equalsIgnoreCase(mc.player.getName().getString())) return false;
        }

        if (entity instanceof PlayerEntity && !targets.getValueByName("Игроки").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.getTotalArmorValue() == 0 && !targets.getValueByName("Голые").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && entity.getTotalArmorValue() == 0 && !targets.getValueByName("Голые невидимки").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && !targets.getValueByName("Невидимки").get()) {
            return false;
        }

        if (entity instanceof MonsterEntity && !targets.getValueByName("Мобы").get()) {
            return false;
        }
        if (entity instanceof AnimalEntity && !targets.getValueByName("Животные").get()) {
            return false;
        }

        return !entity.isInvulnerable() && entity.isAlive() && !(entity instanceof ArmorStandEntity);
    }

    private void breakShieldPlayer(PlayerEntity entity) {
        if (entity.isBlocking()) {
            int invSlot = InventoryUtil.getInstance().getAxeInInventory(false);
            int hotBarSlot = InventoryUtil.getInstance().getAxeInInventory(true);

            if (hotBarSlot == -1 && invSlot != -1) {
                int bestSlot = InventoryUtil.getInstance().findBestSlotInHotBar();
                mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, mc.player);

                mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                mc.playerController.attackEntity(mc.player, entity);
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));

                mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, mc.player);
            }

            if (hotBarSlot != -1) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                mc.playerController.attackEntity(mc.player, entity);
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
            }
        }
    }


    private void reset() {
        if (options.getValueByName("Коррекция движения").get()) {
            mc.player.rotationYawOffset = Integer.MIN_VALUE;
        }
        rotateVector = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    @Override
    public boolean onEnable() {
        super.onEnable();
        reset();
        target = null;
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
        stopWatch.setLastMS(0);
        target = null;
    }

    private double getEntityArmor(PlayerEntity entityPlayer2) {
        double d2 = 0.0;
        for (int i2 = 0; i2 < 4; ++i2) {
            ItemStack is = entityPlayer2.inventory.armorInventory.get(i2);
            if (!(is.getItem() instanceof ArmorItem)) continue;
            d2 += getProtectionLvl(is);
        }
        return d2;
    }

    private double getProtectionLvl(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem i) {
            double damageReduceAmount = i.getDamageReduceAmount();
            if (stack.isEnchanted()) {
                damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
            }
            return damageReduceAmount;
        }
        return 0;
    }

    private double getEntityHealth(LivingEntity ent) {
        if (ent instanceof PlayerEntity player) {
            return (double) (player.getHealth() + player.getAbsorptionAmount()) * (getEntityArmor(player) / 20.0);
        }
        return ent.getHealth() + ent.getAbsorptionAmount();
    }
}
