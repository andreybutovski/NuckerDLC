package im.nucker.ui.display.impl;

import im.nucker.NuckerDLC;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.styles.Style;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.font.Fonts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.ResourceLocation;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WatermarkRenderer implements ElementRenderer {

    private final ResourceLocation logo = new ResourceLocation("expensive/images/tr/logoN.png");

    private final ResourceLocation user = new ResourceLocation("expensive/images/hud/user.png");
    @Override
    public void update(EventUpdate e) {

    }


    @Override
    public void render(EventDisplay eventDisplay) {
        float basePanelX = 10;
        float basePanelY = 5;
        float padding = 5;
        float fontSize = 7.6f;
        float iconSize = 10;
        float panelHeight = 15;
        float offsetFromPanel = 5;

        float spacingAfterFirstText = 2f;
        float spacingAfterFpsIcon = 2f;
        float spacingBeforeUserIcon = 1f;

        String firstText = "         |  NuckerDLC  love  you  <3  | ";
        String fpsText = mc.debugFPS + " fps  | ";
        String playerName = mc.player.getName().getString();
        String fpsIcon = "X";


        float firstTextWidth = Fonts.sfui.getWidth(firstText, fontSize + 0.3f);
        float fpsIconWidth = Fonts.icons2.getWidth(fpsIcon, 10);
        float fpsTextWidth = Fonts.sfui.getWidth(fpsText, fontSize + 0.3f);
        float playerNameWidth = Fonts.sfui.getWidth(playerName, fontSize + 0.3f);
        float firstTextX = basePanelX + offsetFromPanel;
        float fpsIconX = firstTextX + firstTextWidth + spacingAfterFirstText;
        float fpsTextX = fpsIconX + fpsIconWidth + spacingAfterFpsIcon;
        float userIconX = fpsTextX + fpsTextWidth + spacingBeforeUserIcon;
        float playerNameX = userIconX + iconSize + spacingBeforeUserIcon;
        float panelWidth = playerNameX + playerNameWidth + padding - basePanelX;

        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();

        DisplayUtils.drawShadow(basePanelX, basePanelY, panelWidth, panelHeight, 7, ColorUtils.rgba(9, 8, 23, 1));
     //   DisplayUtils.drawRoundedRect(basePanelX - 1.3f, basePanelY - 1.3f, panelWidth + 2.8f, panelHeight + 2.8f, 5, ColorUtils.rgb(46, 45, 58));
        DisplayUtils.drawRoundedRect(basePanelX - 0.5f, basePanelY - 0.5f, panelWidth + 1f, panelHeight + 1f, 4, ColorUtils.rgb(9, 8, 23));

        float logoX = basePanelX + 2;
        float logoY = basePanelY + 3.5f;

        Fonts.sfui.drawText(eventDisplay.getMatrixStack(), firstText, firstTextX, basePanelY + 4f, ColorUtils.rgb(255, 255, 255), fontSize + 0.3f);
        Fonts.icons2.drawText(eventDisplay.getMatrixStack(), fpsIcon, fpsIconX, basePanelY + 3.5f, ColorUtils.rgba(190,185,255,255), 10);
        Fonts.sfui.drawText(eventDisplay.getMatrixStack(), fpsText, fpsTextX, basePanelY + 4f, ColorUtils.rgb(255, 255, 255), fontSize + 0.3f);

        float userIconY = basePanelY + (panelHeight - iconSize) / 2 ;
        DisplayUtils.drawImage(user, userIconX, userIconY, iconSize, iconSize, ColorUtils.rgba(190,185,255,255));
        iconSize = 30;
        DisplayUtils.drawImage(logo, logoX - 7, logoY - 11, iconSize , iconSize, ColorUtils.rgba(170, 165, 228,255));

        Fonts.sfui.drawText(eventDisplay.getMatrixStack(), playerName, playerNameX, basePanelY + 4f, ColorUtils.rgb(255, 255, 255), fontSize + 0.3f);
    }
}
