package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;
import net.minecraftforge.eventbus.api.Event;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@FunctionRegister(name = "Advertising", type = Category.Misc)
public class Advertising extends Function {

    private long lastSentTime = 0;

    private final List<String> messages = Arrays.asList(
            "!&c Хочешь лучшие обходы и 2batь всех? тогда тебе в TGK - @NuckerDLC  1 ",
            "&c Хочешь лучшие обходы и 2batь всех? тогда тебе в TGK - @NuckerDLC  1",
            "&cХочешь лучшие обходы и 2batь всех? тогда тебе в TGK - @NuckerDLC   1",
            "&c Хочешь лучшие обходы и 2batь всех? тогда тебе в TGK - @NuckerDLC   1 ",
            "&c Играй без границ — выбирай TGK NuckerDLC!  1  "

    );

    private final Random random = new Random();

    @Subscribe
    public void onEvent(Event event) {
        func();
    }

    public void func() {
        long currentTime = System.currentTimeMillis();
        long delay = 10000;

        if (currentTime - lastSentTime >= delay) {
            // Выбор случайного сообщения из списка
            String randomMessage = messages.get(random.nextInt(messages.size()));
            // Генерация случайного числа от 0 до 30000
            int randomNumber = random.nextInt(30001); // 30001, чтобы включить 30000
            // Добавление случайного числа к сообщению
            String messageToSend = randomMessage + randomNumber;

            mc.player.sendChatMessage(messageToSend);
            lastSentTime = currentTime;
        }
    }
}