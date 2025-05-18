package im.nucker.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.utils.math.StopWatch;
import im.nucker.utils.player.MoveUtils;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

@FunctionRegister(
        name = "EntitySpeed",
        type = Category.Movement
)
public class Speed1 extends Function {
    ItemStack currentStack;
    public ModeSetting mod;
    private long lastPacketTime;
    public boolean boosting;
    public StopWatch timerUtil;



    public Speed1() {
        this.currentStack = ItemStack.EMPTY;
        this.mod = new ModeSetting("Мод", "FunTime Near", new String[]{"FunTime Near", "FunTimeTest"});
        this.lastPacketTime = -1L;
        this.timerUtil = new StopWatch();
        this.addSettings(new Setting[0]);

    }

    public boolean onEnable() {
        super.onEnable();
        this.timerUtil.reset();
        this.boosting = false;


        return false;
    }

    private void applyMatrixSpeed() {
        double speed = 1.0;
        Vector3d var10000 = mc.player.motion;
        var10000.x *= speed;
        var10000 = mc.player.motion;
        var10000.z *= speed;
        Speed1.StrafeMovement.oldSpeed *= speed;
    }


    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.mod.is("FunTime Near")) {
            this.ft();
        }

        BlockState blockState;
        if (this.mod.is("FunTimeTest") && ((blockState = mc.world.getBlockState(mc.player.getPosition().down())).getBlock() instanceof StairsBlock || blockState.getBlock() instanceof SlabBlock || blockState.getBlock() instanceof BarrelBlock || blockState.getBlock() instanceof ScaffoldingBlock || blockState.getBlock() instanceof CarpetBlock || blockState.getBlock() instanceof FlowerPotBlock) && mc.player.isOnGround() && MoveUtils.isMoving()) {
            mc.gameSettings.keyBindJump.setPressed(true);
            this.applyMatrixSpeed();
        }

    }

    private void ft() {
        AxisAlignedBB aabb = TargetStrafe.mc.player.getBoundingBox().grow(0.1);
        int armorstans = TargetStrafe.mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
        boolean canBoost = armorstans > 1 || TargetStrafe.mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
        if (canBoost && !TargetStrafe.mc.player.isOnGround()) {
            TargetStrafe.mc.player.jumpMovementFactor = armorstans > 1 ? 1.0F / (float)armorstans : 0.16F;
            float var5 = TargetStrafe.mc.player.jumpMovementFactor;
        }

    }

    public static class StrafeMovement {
        public static double oldSpeed;
        public static double contextFriction;

        public StrafeMovement() {
        }

        public static void postMove(double horizontal) {
            oldSpeed = horizontal * contextFriction;
        }

        public static float getAIMoveSpeed(ClientPlayerEntity contextPlayer) {
            boolean prevSprinting = contextPlayer.isSprinting();
            contextPlayer.setSprinting(false);
            float speed = contextPlayer.getAIMoveSpeed() * 1.3F;
            contextPlayer.setSprinting(prevSprinting);
            return speed;
        }
    }
}