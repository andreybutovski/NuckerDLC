//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package im.nucker.functions.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.functions.impl.combat.KillAura;
import im.nucker.functions.settings.Setting;
import im.nucker.functions.settings.impl.BooleanSetting;
import im.nucker.functions.settings.impl.ModeSetting;
import im.nucker.functions.settings.impl.SliderSetting;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

@FunctionRegister(
        name = "SwingAnimation",
        type = Category.Render
)
public class SwingAnimation extends Function {
    public final BooleanSetting scaleChanger = new BooleanSetting("Менять размер", false);
    public ModeSetting animationMode = new ModeSetting("Мод", "Обычный", new String[]{"Обычный", "Горизонтальный", "Вертикальный", "Ревёрс", "Спираль", "Тяжёлый", "Простой", "Динамичный", "Комплекс", "ебучтоле", "Нейро", "Разруха", "Смолд", "Пульсивный", "Вращение", "колапс"});
    public SliderSetting swingPower = new SliderSetting("Сила", 5.0F, 1.0F, 10.0F, 0.05F);
    public SliderSetting swingSpeed = new SliderSetting("Скорость", 10.0F, 3.0F, 10.0F, 1.0F);
    public SliderSetting scaleX = (new SliderSetting("Размер X", 1.0F, -5.0F, 5.0F, 0.05F)).setVisible(() -> {
        return (Boolean)this.scaleChanger.get();
    });
    public SliderSetting scaleY = (new SliderSetting("Размер Y", 1.0F, -5.0F, 5.0F, 0.05F)).setVisible(() -> {
        return (Boolean)this.scaleChanger.get();
    });
    public SliderSetting scaleZ = (new SliderSetting("Размер Z", 1.0F, -5.0F, 5.0F, 0.05F)).setVisible(() -> {
        return (Boolean)this.scaleChanger.get();
    });
    public SliderSetting angle = (new SliderSetting("Угол", 100.0F, 0.0F, 360.0F, 1.0F)).setVisible(() -> {
        return this.animationMode.is("Turn");
    });
    public final BooleanSetting onlyAura = new BooleanSetting("Только с аурой", false);
    public KillAura killAura;

    public SwingAnimation(KillAura killAura) {
        this.killAura = killAura;
        this.addSettings(new Setting[]{this.scaleChanger, this.animationMode, this.swingPower, this.swingSpeed, this.scaleX, this.scaleY, this.scaleZ, this.angle, this.onlyAura});
    }

    public void animationProcess(MatrixStack stack, float swingProgress, HandSide handSide, Runnable runnable) {
        float anim = (float)Math.sin((double)swingProgress * 1.5707963267948966 * 2.0);


        int i = handSide == HandSide.RIGHT ? 1 : -1;
        float sin1 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float sin2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
        Runnable applyScaling = () -> {
            if ((Boolean)this.scaleChanger.get()) {
                stack.scale((Float)this.scaleX.get(), (Float)this.scaleY.get(), (Float)this.scaleZ.get());
            }

        };
        applyScaling.run();
        switch (this.animationMode.getIndex()) {
            case 0:
                runnable.run();
                break;
            case 1:
                stack.translate(0.4000000059604645, 0.10000000149011612, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90.0F));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F - (Float)this.swingPower.get() * 10.0F * anim));
                break;
            case 2:
                stack.translate(0.0, 0.0, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(15.0F * anim));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0F * anim));
                stack.rotate(Vector3f.XP.rotationDegrees((-90.0F - (Float)this.swingPower.get()) * anim));
                break;
            case 3:
                stack.translate(0.4000000059604645, 0.10000000149011612, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90.0F));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F + (Float)this.swingPower.get() * 10.0F * anim));
                break;
            case 4:
                stack.translate(0.4000000059604645, 0.10000000149011612, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90.0F + (Float)this.swingPower.get() * 10.0F * anim));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F + (Float)this.swingPower.get() * 10.0F * anim));
                break;
            case 5:
                stack.translate((double)((float)i * 0.56F), -0.3199999928474426, -0.7200000286102295);
                stack.rotate(Vector3f.YP.rotationDegrees(sin2 + 55.0F));
                stack.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
                stack.rotate(Vector3f.ZP.rotationDegrees(sin2 + 5.0F));
                stack.rotate(Vector3f.YN.rotationDegrees(sin2 * 35.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-135.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(sin1 + 15.0F));
                break;
            case 6:
                stack.translate((double)((float)i * 0.8F), -0.5199999809265137, -1.2000000476837158);
                stack.translate(0.0, 0.0, -1.5 * (double)sin2);
                stack.rotate(Vector3f.ZP.rotationDegrees(0.0F));
                stack.rotate(Vector3f.ZN.rotationDegrees(0.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-100.0F));
                break;
            case 7:
                float power = (Float)this.swingPower.get() * 10.0F;
                float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
                float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                stack.rotate(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * (-power / 4.0F))));
                stack.rotate(Vector3f.ZP.rotationDegrees((float)i * f1 * -(power / 4.0F)));
                stack.rotate(Vector3f.XP.rotationDegrees(-360.0F * swingProgress));
                stack.rotate(Vector3f.YP.rotationDegrees((float)i * -45.0F));
                break;
            case 8:
                stack.translate(0.46000000834465027, -0.30000001192092896, -0.7199999690055847);
                stack.rotate(Vector3f.YP.rotationDegrees(45.0F));
                stack.rotate(Vector3f.YP.rotationDegrees(sin1 * -20.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(sin2 * -20.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(sin2 * -40.0F));
                stack.scale(0.5F, 0.5F, 0.5F);
                stack.translate(-0.5, 0.20000000298023224, 0.0);
                stack.rotate(Vector3f.YP.rotationDegrees(30.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-80.0F));
                stack.rotate(Vector3f.YP.rotationDegrees(60.0F));
                break;
            case 9:
                stack.rotate(Vector3f.YP.rotationDegrees(90.0F));
                stack.rotate(Vector3f.ZP.rotationDegrees(-70.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-(Float)this.angle.get() - (Float)this.swingPower.get() * 10.0F * anim));
                break;
            case 10:
                stack.translate((double)((float)i * 0.56F), -0.3199999928474426, -0.7200000286102295);
                stack.translate(0.0, 0.0, -1.5 * (double)sin2 / 5.0);
                stack.rotate(Vector3f.YP.rotationDegrees(80.0F));
                stack.rotate(Vector3f.ZN.rotationDegrees(45.0F));
                stack.rotate(Vector3f.YP.rotationDegrees(-30.0F));
                stack.translate(0.0, 0.0, -0.5 * (double)sin2);
                stack.rotate(Vector3f.XN.rotationDegrees(sin2 * -100.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(sin2 * -155.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                break;
            case 11:
                float translateY = sin2 * 0.2F;
                float animationSpeed = -0.1F;
                translateY += animationSpeed;
                stack.translate(0.5, (double)(-translateY), -0.8999999761581421);
                stack.rotate(Vector3f.ZP.rotationDegrees(170.0F));
                stack.rotate(Vector3f.ZN.rotationDegrees(15.0F));
                stack.rotate(Vector3f.YP.rotationDegrees(-55.0F));
                stack.push();
                stack.rotate(Vector3f.YP.rotationDegrees(-55.0F));
                stack.pop();
                break;
            case 12:
                stack.translate((double)((float)i * 0.56F), -0.3199999928474426, -0.7200000286102295);
                stack.rotate(Vector3f.YP.rotationDegrees(76.0F));
                stack.rotate(Vector3f.YP.rotationDegrees(sin2 * -5.0F));
                stack.rotate(Vector3f.XN.rotationDegrees(sin2 * -100.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(sin2 * -155.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-100.0F));
                break;
            case 13:
                stack.rotate(Vector3f.XP.rotationDegrees(swingProgress * -360.0F));
                break;
            case 14:
                stack.translate(0.0, (double)(0.1F * anim), (double)(-0.4F * anim));
                stack.rotate(Vector3f.XP.rotationDegrees(-45.0F * anim));
                stack.rotate(Vector3f.YP.rotationDegrees(60.0F * anim));
                stack.rotate(Vector3f.ZP.rotationDegrees(15.0F * anim));
                stack.translate(0.0, (double)(-0.2F * anim), (double)(0.6F * anim));
                float scaleUp = 1.0F + 0.3F * anim;
                stack.scale(scaleUp, scaleUp, scaleUp);
                stack.translate(0.0, (double)(0.1F * anim), (double)(-0.2F * anim));
                float scaleDown = 1.0F - 0.1F * anim;
                stack.scale(scaleDown, scaleDown, scaleDown);
                stack.rotate(Vector3f.YP.rotationDegrees(-30.0F * anim));
                stack.translate(0.0, (double)(0.1F * anim), (double)(0.1F * anim));
                break;
            case 15:
                stack.translate(0.0, 0.20000000298023224, -0.6000000238418579);
                stack.rotate(Vector3f.XP.rotationDegrees(-180.0F * anim));
                stack.rotate(Vector3f.YP.rotationDegrees(10.0F * anim));
                stack.translate(0.0, -0.20000000298023224, 0.6000000238418579);
                break;
            default:
                runnable.run();
        }

    }
}