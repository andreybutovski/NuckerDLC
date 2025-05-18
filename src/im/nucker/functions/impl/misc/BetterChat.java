package im.nucker.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import im.nucker.events.EventPacket;
import im.nucker.functions.api.Category;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegister;

@FunctionRegister(name = "BetterChat", type = Category.Misc)
public class BetterChat extends Function {

    private String lastMessage = "";
    private int amount = 1;
    private int line = 0;

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SChatPacket chatPacket) {

            final StringTextComponent message = new StringTextComponent(chatPacket.getChatComponent().getString());
            final String rawMessage = message.getUnformattedComponentText();
            final NewChatGui chatGui = mc.ingameGUI.getChatGUI();

            if (this.lastMessage.equals(rawMessage)) {
                chatGui.deleteChatLine(this.line);
                this.amount++;

                message.append(new StringTextComponent(TextFormatting.GRAY + " [x" + this.amount + "]"));
            } else {

                this.amount = 1;
            }

            this.line++;
            this.lastMessage = rawMessage;

            chatGui.printChatMessageWithOptionalDeletion(message, this.line);

            if (this.line > 256) {
                this.line = 0;
            }

            e.cancel();
        }
    }
}