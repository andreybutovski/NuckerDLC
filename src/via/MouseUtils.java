package via;

import net.minecraft.util.math.vector.Vector2f;

public final class MouseUtils {
    public static boolean mouseOver(double posX, double posY, double width, double height, double mouseX, double mouseY) {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height;
    }
    public static boolean mouseOver(Vector2f position, Vector2f scale, double mouseX, double mouseY) {
        return mouseX > (double) position.x && mouseX < (double) (position.x + scale.x) && mouseY > (double) position.y && mouseY < (double) (position.y + scale.y);
    }
    public static boolean isHovered(int mouseX, int mouseY, float x,float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static boolean isHovered(float mouseX, float mouseY, float x,float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static boolean isHovered(double mouseX, double mouseY, float x,float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}