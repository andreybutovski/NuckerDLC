package im.nucker.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.concurrent.CopyOnWriteArrayList;
import im.nucker.utils.animations.Animation;
import im.nucker.utils.animations.Direction;
import im.nucker.utils.animations.impl.EaseInOutQuad;  // Используем существующую анимацию EaseInOutQuad
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import net.minecraft.util.ResourceLocation;
import static im.nucker.utils.client.IMinecraft.mc;

public class NotificationManager {
    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void add(String text, String content, int time, ImageType imageType) {
        notifications.add(new Notification(text, content, time, imageType));
    }

    public void draw(MatrixStack stack) {
        int yOffset = 0;
        for (Notification notification : notifications) {
            long elapsedTime = System.currentTimeMillis() - notification.getTime();
            if (elapsedTime > notification.time2 * 1000L) {
                notification.animation.setDirection(Direction.BACKWARDS);
            } else {
                notification.animation.setDirection(Direction.FORWARDS);
            }

            if (notification.animation.finished(Direction.BACKWARDS)) {
                notifications.remove(notification);
                continue;
            }

            float alpha = (float) notification.animation.getOutput();
            float x = mc.getMainWindow().scaledWidth() - 150;
            float y = mc.getMainWindow().scaledHeight() - (yOffset * 30 + 50) * alpha;

            notification.setX(x);
            notification.setY(y);
            notification.draw(stack, alpha);
            yOffset++;
        }
    }

    private class Notification {
        float x = 50;
        float y = 50;
        private final String text, content;
        private final long time;
        public final Animation animation = new EaseInOutQuad(700, 1.0, Direction.FORWARDS);  // Плавная анимация с увеличенной продолжительностью
        private final ImageType imageType;
        private final int time2;

        public Notification(String text, String content, int time, ImageType imageType) {
            this.text = text;
            this.content = content;
            this.time2 = time;
            this.imageType = imageType;
            this.time = System.currentTimeMillis();
        }

        public void draw(MatrixStack stack, float alpha) {
            float width = Fonts.sfMedium.getWidth(text, 6.0f) + 40.0f;
            float fadeAlpha = Math.min(1.0f, alpha);  // Убедимся, что альфа не будет меньше 0
            DisplayUtils.drawRoundedRect(x, y, width, 20.0f, 5.0f, ColorUtils.rgba(21, 21, 21, (int) (252 * fadeAlpha)));
            ResourceLocation img = new ResourceLocation(imageType == ImageType.FIRST_PHOTO ? "expensive/images/hud/notify.png" : "expensive/images/hud/notify1.png");
            DisplayUtils.drawImage(img, x + 5, y + 2, 16.0f, 16.0f, ColorUtils.getColor(0));
            Fonts.sfMedium.drawText(stack, text, x + 25, y + 5, ColorUtils.rgba(255, 255, 255, (int) (255 * fadeAlpha)), 6.0f, 0.05f);
            Fonts.sfMedium.drawText(stack, content, x + 25, y + 15, ColorUtils.rgb(0, 255, 0), 4.0f, 0.05f);
        }

        public void setX(float x) { this.x = x; }
        public void setY(float y) { this.y = y; }
        public long getTime() { return this.time; }
    }

    public enum ImageType {
        FIRST_PHOTO, SECOND_PHOTO;
    }
}
