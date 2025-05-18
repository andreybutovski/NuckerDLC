package im.nucker.functions.impl.misc;

import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.TextFormatting;
import im.nucker.events.EventPacket;
import im.nucker.events.EventUpdate;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import im.nucker.utils.math.StopWatch;

@FunctionRegister(
        name = "CasinoBot",
        type = Category.Misc
)
public class CasinoBot extends Function {
    StopWatch timer = new StopWatch();
    PlayerEntity player;
    int balances;
    Random randomGenerator = new Random();


    public CasinoBot() {

    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.timer.isReached( 10000L)) {
            Minecraft var10000 = mc;
            ClientPlayerEntity var2 = Minecraft.player;
            Minecraft var10001 = mc;
            String var3 = Minecraft.player.getScoreboardName();
            var2.sendChatMessage("!Привет! Я - ваш личный казино бот. Попробуйте свою удачу и отправьте мне деньги /pay " + var3 + " сумма. Взамен, я отправлю вам сумму x2 если повезёт. Казино бот работает от суммы 30000");
            this.timer.reset();
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket var3 = e.getPacket();
        if (var3 instanceof SChatPacket p) {
            String raw = p.getChatComponent().getString().toLowerCase(Locale.ROOT);
            if (raw.contains("получено от игрока ")) {
                String[] parts = raw.split(" ");
                String senderName = parts[parts.length - 1];

                // Используем регулярное выражение для извлечения суммы с запятыми
                Pattern pattern = Pattern.compile("\\$(\\d{1,3}(,\\d{3})*)");
                Matcher matcher = pattern.matcher(raw);
                int amount = 0;
                if (matcher.find()) {
                    String amountStr = matcher.group(1).replace(",", "");
                    amount = Integer.parseInt(amountStr);
                }

                // Проверка на минимальную сумму
                if (amount < 30000) {
                    Minecraft var10000 = mc;
                    ClientPlayerEntity var4 = Minecraft.player;
                    var4.sendChatMessage("/pm " + senderName + " Минимальная сумма для игры - 30000 монет.");
                    return;
                }

                boolean win = randomGenerator.nextDouble() < 0.4; // 40% процент шанс на то то выпадет / nucker sigma

                Minecraft var10000 = mc;
                ClientPlayerEntity var4 = Minecraft.player;
                if (win) {
                    var4.sendChatMessage("/pm " + senderName + " вы выйграли");
                    int winnings = amount * 2;
                    var4.sendChatMessage("/pay " + senderName + " " + winnings);
                    var4.sendChatMessage("/pay " + senderName + " " + winnings);
                    TextFormatting var5 = TextFormatting.GREEN;
                } else {
                    var4.sendChatMessage("/pm " + senderName + " вы проиграли");
                    TextFormatting var5 = TextFormatting.RED;
                }
            }
        }
    }
}