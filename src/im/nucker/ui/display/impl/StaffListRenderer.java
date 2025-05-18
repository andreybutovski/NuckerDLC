package im.nucker.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.nucker.NuckerDLC;
import im.nucker.command.staffs.StaffStorage;
import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.ui.display.ElementRenderer;
import im.nucker.ui.display.ElementUpdater;
import im.nucker.ui.styles.Style;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.render.ColorUtils;
import im.nucker.utils.render.DisplayUtils;
import im.nucker.utils.render.Scissor;
import im.nucker.utils.render.font.Fonts;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StaffListRenderer implements ElementRenderer, ElementUpdater {

    final Dragging dragging;
    final ResourceLocation logo = new ResourceLocation("expensive/images/HUD2/staff.png");
    final float iconSize = 10;

    private final List<Staff> staffPlayers = new ArrayList<>();
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд ).*");

    Map<String, StaffAnimation> staffAnimations = new HashMap<>();

    float width;
    float height;

    @Override
    public void update(EventUpdate e) {
        staffPlayers.clear();

        for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams().stream()
                .sorted(Comparator.comparing(Team::getName)).toList()) {
            String name = team.getMembershipCollection().toString().replaceAll("[\\[\\]]", "");
            boolean vanish = true;
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(name)) {
                    vanish = false;
                }
            }
            if (namePattern.matcher(name).matches() && !name.equals(mc.player.getName().getString())) {
                if (!vanish) {
                    if (prefixMatches.matcher(team.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() ||
                            StaffStorage.isStaff(name)) {
                        Staff staff = new Staff(team.getPrefix(), name, false, Status.NONE);
                        staffPlayers.add(staff);
                    }
                }
                if (vanish && !team.getPrefix().getString().isEmpty()) {
                    Staff staff = new Staff(team.getPrefix(), name, true, Status.VANISHED);
                    staffPlayers.add(staff);
                }
            }
        }
        cleanUpAnimations();
    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float padding = 5;
        float fontSize = 7.6f;

        ITextComponent header = new StringTextComponent("StaffList").mergeStyle(TextFormatting.WHITE);
        Style style = NuckerDLC.getInstance().getStyleManager().getCurrentStyle();

        DisplayUtils.drawShadow(posX, posY, width, height, 7, ColorUtils.rgba(9, 8, 23, 1));
        DisplayUtils.drawRoundedRect(posX - 1.3f, posY - 1.3f, width + 2.8f, height + 2.8f, 5, ColorUtils.rgb(46, 45, 58));
        DisplayUtils.drawRoundedRect(posX - 0.5f, posY - 0.5f, width + 1f, height + 1f, 4, ColorUtils.rgb(9, 8, 23));
        DisplayUtils.drawImage(logo, posX + 60f, posY + padding, iconSize, iconSize, ColorUtils.rgba(170, 165, 228,255));

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        Fonts.sfui.drawCenteredText(ms, header, posX + width / 4, posY + padding + 0.5f, fontSize + 0.45f);
        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfui.getWidth(header, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;

        DisplayUtils.drawRectHorizontalW(posX + 0.5f, posY, width - 1, 1.5f, 3,
                ColorUtils.rgba(46, 45, 58, 255));
        posY += 4f;

        for (Staff f : staffPlayers) {
            String key = f.getName();
            StaffAnimation anim = staffAnimations.get(key);
            if (anim == null) {
                anim = new StaffAnimation();
                staffAnimations.put(key, anim);
            }
            anim.update();

            int animAlpha = (int) (255 * anim.getValue());
            int prefixColor = ColorUtils.rgba(255, 255, 255, animAlpha);
            int nameColor = ColorUtils.rgba(255, 255, 255, animAlpha);
            int statusColor = adjustAlpha(f.getStatus().color, animAlpha);

            ITextComponent prefix = f.getPrefix();
            float prefixWidth = Fonts.sfui.getWidth(prefix, fontSize);
            String staffName = (prefix.getString().isEmpty() ? "" : " ") + f.getName();
            float nameWidth = Fonts.sfui.getWidth(staffName, fontSize);
            float statusWidth = Fonts.sfui.getWidth(f.getStatus().string, fontSize);
            float localWidth = prefixWidth + nameWidth + statusWidth + padding * 3;

            Fonts.sfui.drawText(ms, staffName, posX + padding + prefixWidth, posY + 0.5f,
                    nameColor, fontSize + 0.1f);
            Fonts.sfui.drawText(ms, f.getStatus().string, posX + width - padding - statusWidth, posY + 0.5f,
                    statusColor, fontSize + 0.1f);
            Fonts.sfui.drawText(ms, prefix.getString(), posX + padding, posY + 0.5f, prefixColor, fontSize + 0.1f);
          // вот тести  Fonts.icons2.drawText(eventDisplay.getMatrixStack(), "E", posX + 64, posY + 5.5f, ColorUtils.rgba(190,185,255,255), 9);


            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (fontSize + padding) * anim.getValue();
            localHeight += (fontSize + padding) * anim.getValue();
        }
        Scissor.unset();
        Scissor.pop();

        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
        cleanUpAnimations();
    }

    private int adjustAlpha(int color, int newAlpha) {
        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    private void cleanUpAnimations() {
        Map<String, Boolean> activeKeys = new HashMap<>();
        for (Staff f : staffPlayers) {
            activeKeys.put(f.getName(), true);
        }
        Iterator<String> it = staffAnimations.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!activeKeys.containsKey(key)) {
                it.remove();
            }
        }
    }

    @AllArgsConstructor
    @Data
    public static class Staff {
        ITextComponent prefix;
        String name;
        boolean isSpec;
        Status status;

        public void updateStatus() {
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(name)) {
                    if (info.getGameType() == GameType.SPECTATOR) {
                        return;
                    }
                    status = Status.NONE;
                    return;
                }
            }
            status = Status.VANISHED;
        }
    }

    public enum Status {
        NONE("", -1),
        VANISHED("V", ColorUtils.rgb(254, 68, 68));
        public final String string;
        public final int color;

        Status(String string, int color) {
            this.string = string;
            this.color = color;
        }
    }
    
    private static class StaffAnimation {
        private float value = 0.0f;
        private final float speed = 0.05f;

        public void update() {
            if (value < 1.0f) {
                value += speed;
                if (value > 1.0f) {
                    value = 1.0f;
                }
            }
        }

        public float getValue() {
            return value;
        }
    }
}
