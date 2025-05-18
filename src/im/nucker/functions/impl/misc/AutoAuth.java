// package im.nucker.functions.impl.misc;
//  ДОДЕЛАТЬ!!!!  ДОДЕЛАТЬ!!!!  ДОДЕЛАТЬ!!!!  ДОДЕЛАТЬ!!!!  ДОДЕЛАТЬ!!!!
// import im.nucker.functions.api.Category;  ДОДЕЛАТЬ!!!!
// import im.nucker.functions.api.Function;     ДОДЕЛАТЬ!!!!
// import im.nucker.functions.api.FunctionRegister;  ДОДЕЛАТЬ!!!!
// import net.minecraft.network.IPacket;  ДОДЕЛАТЬ!!!!
// import net.minecraft.network.play.server.SChatPacket;  ДОДЕЛАТЬ!!!!
// import net.minecraft.util.text.TextFormatting;
// import org.apache.commons.lang3.RandomStringUtils;

// @FunctionRegister(name = "Auto Auth", type =  Category.Misc)
// public class AutoAuth extends Function {

   // private final StringValue password = new StringValue("Введите ваш пароль", this, RandomStringUtils.randomAlphabetic(8));
  //  private final TimerUtil timer = TimerUtil.create();   ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!!
  //  private final Listener<PacketEvent> onPacket = event -> {  ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!! ДОДЕЛАТЬ!!!!
    //    if (event.isSent()) return;

    //    IPacket<?> packet = event.getPacket();

      //  if (packet instanceof SChatPacket wrapper) {
        //    String message = TextFormatting.getTextWithoutFormattingCodes(wrapper.getChatComponent().getString());
       //     if (message == null) return;
       //     if (Thread.sleep(1000)) {
           //     if (message.contains("/login")) {
          //          ChatUtil.sendText("/login " + password.getValue());
                  //  timer.reset();
              //  } else if (message.contains("/reg") || message.contains("/register")) {
              //      ChatUtil.sendText("/register " + password.getValue() + " " + password.getValue());
              //      ChatUtil.sendText("/register " + password.getValue());
               //     timer.reset();
              //  }
       //     }

      //  }

   // };

// }