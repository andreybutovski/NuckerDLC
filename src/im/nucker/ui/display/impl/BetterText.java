package im.nucker.ui.display.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class BetterText {

    private List<String> texts = new ArrayList<>();
    public String output = "";
    private int delay = 0;

    // Добавьте сюда ваше поле watername или метод для его получения
    private String watername = "Nucker";

    public BetterText(List<String> texts, int delay) {
        this.texts = texts;
        this.delay = delay;
        start();
    }

    private Timer timerUtil = new Timer();

    public String getCurrentOutput() {
        return output;
    }

    // Метод для обновления текста водяного знака
    public void updateRenderText() {
        // Обновите значение watername здесь, если оно изменяется динамически

        // Обновление текста с новым значением watername
        texts.set(0, watername); // Предполагается, что watername должен быть в первой строке
    }

    private void start() {
        new Thread(() -> {
            try {
                int index = 0;
                while (true) {
                    for (int i = 0; i < texts.get(index).length(); i++) {
                        output += texts.get(index).charAt(i);
                        Thread.sleep(100);
                    }
                    Thread.sleep(delay);
                    for (int i = output.length(); i >= 0; i--) {
                        output = output.substring(0, i);
                        Thread.sleep(60);
                    }
                    if (index >= texts.size() - 1) {
                        index = 0;
                    } else {
                        index += 1;
                    }
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}