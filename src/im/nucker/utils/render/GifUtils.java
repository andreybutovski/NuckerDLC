package im.nucker.utils.render;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GifUtils {
    private static final List<ResourceLocation> WTGIFFS = new ArrayList<>();
    private static final List<ResourceLocation> Companion = new ArrayList<>();
    private static final List<ResourceLocation> KotGIFFS = new ArrayList<>();
    private static final List<ResourceLocation> Heart360 = new ArrayList<>();
    private static final List<ResourceLocation> Mishagif = new ArrayList<>();
    private static int currentFrame = 0;
    private static long lastFrameTime = 0;
    private static long frameDelay = 15;
    public static void GifRender(String resourceLocation,int x,int y,int width,int height,int numImages) {
        for (int i = 0; i < numImages; i++) {
            WTGIFFS.add(new ResourceLocation("assets/minecraft/expensive/Gif_WT/" + WTGIFFS + i + ".png"));
            Companion.add(new ResourceLocation("assets/minecraft/expensive/Gif_WT/Gif_Companion/" + Companion + i + ".png"));
            KotGIFFS.add(new ResourceLocation("assets/minecraft/expensive/Gif_WT/Gif_KOTT/" + KotGIFFS + i + ".png"));
            Heart360.add(new ResourceLocation("assets/minecraft/expensive/gifs/" + Heart360 + i + ".png"));
            Mishagif.add(new ResourceLocation("assets/minecraft/expensive/mishagif/" + Mishagif + i + ".png"));
        }
        if (System.currentTimeMillis() - lastFrameTime > frameDelay) {
            lastFrameTime = System.currentTimeMillis();
            currentFrame = (currentFrame + 1) % WTGIFFS.size();
        }
        ResourceLocation currentImage = WTGIFFS.get(currentFrame);

        DisplayUtils.drawImage(currentImage, x, y, width, height, -1);
    }
}