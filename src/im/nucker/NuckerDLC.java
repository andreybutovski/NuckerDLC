package im.nucker;

import com.google.common.eventbus.EventBus;
import im.nucker.command.*;
import im.nucker.command.friends.FriendStorage;
import im.nucker.command.impl.*;
import im.nucker.command.impl.feature.*;
import im.nucker.command.staffs.StaffStorage;
import im.nucker.config.ConfigStorage;
import im.nucker.events.EventKey;
import im.nucker.functions.api.Function;
import im.nucker.functions.api.FunctionRegistry;
import im.nucker.scripts.client.ScriptManager;
import im.nucker.ui.NotificationManager;
import im.nucker.ui.ab.factory.ItemFactory;
import im.nucker.ui.ab.factory.ItemFactoryImpl;
import im.nucker.ui.ab.logic.ActivationLogic;
import im.nucker.ui.ab.model.IItem;
import im.nucker.ui.ab.model.ItemStorage;
import im.nucker.ui.ab.render.Window;
import im.nucker.ui.autobuy.AutoBuyConfig;
import im.nucker.ui.autobuy.AutoBuyHandler;
import im.nucker.ui.clickgui.DropDown;
import im.nucker.ui.mainmenu.AltConfig;
import im.nucker.ui.mainmenu.AltWidget;
import im.nucker.ui.styles.Style;
import im.nucker.functions.api.NaksonPaster;
import im.nucker.ui.styles.StyleFactory;
import im.nucker.ui.styles.StyleFactoryImpl;
import im.nucker.ui.styles.StyleManager;
import im.nucker.utils.TPSCalc;
import im.nucker.utils.client.ServerTPS;
import im.nucker.utils.drag.DragManager;
import im.nucker.utils.drag.Dragging;
import im.nucker.utils.text.font.ClientFonts;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import via.ViaMCP;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NuckerDLC {

    public static UserData userData;
    public boolean playerOnServer = false;
    public static final String CLIENT_NAME = "Nucker client";

    // Экземпляр NuckerDLC
    @Getter
    private static NuckerDLC instance;

    // Менеджеры
    private FunctionRegistry functionRegistry;
    private ConfigStorage configStorage;
    private CommandDispatcher commandDispatcher;
    private ServerTPS serverTPS;
    private MacroManager macroManager;
    private StyleManager styleManager;

    // Менеджер событий и скриптов
    private final EventBus eventBus = new EventBus();
    private final ScriptManager scriptManager = new ScriptManager();

    // Директории
    private final File clientDir = new File(Minecraft.getInstance().gameDir + "\\nucker");
    private final File filesDir = new File(Minecraft.getInstance().gameDir + "\\nucker\\files");
    public NotificationManager notificationManager;

    // Элементы интерфейса
    private AltWidget altWidget;
    private AltConfig altConfig;
    private DropDown dropDown;
 //OBS   private DropDown2 dropDown2;
    private Window autoBuyUI;

    // Конфигурация и обработчики
    private AutoBuyConfig autoBuyConfig = new AutoBuyConfig();
    private AutoBuyHandler autoBuyHandler;
    private ViaMCP viaMCP;
    private TPSCalc tpsCalc;
    private ActivationLogic activationLogic;
    private ItemStorage itemStorage;

    public NuckerDLC() {
        instance = this;

        if (!clientDir.exists()) {
            clientDir.mkdirs();
        }
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        clientLoad();
        StaffStorage.load();
        FriendStorage.load();
    }



    public Dragging createDrag(Function module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

    private void clientLoad() {
        notificationManager = new NotificationManager();
        viaMCP = new ViaMCP();
        serverTPS = new ServerTPS();
        functionRegistry = new FunctionRegistry();
        macroManager = new MacroManager();
        configStorage = new ConfigStorage();
        functionRegistry.init();
        initCommands();
        initStyles();
        altWidget = new AltWidget();
        altConfig = new AltConfig();
        tpsCalc = new TPSCalc();
        NaksonPaster.NOTIFICATION_MANAGER = new NotificationManager();


        userData = new UserData("paster", 1);

        try {
            autoBuyConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            altConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            configStorage.init();
        } catch (IOException e) {
            System.out.println("Ошибка при подгрузке конфига.");
        }
        try {
            macroManager.init();
        } catch (IOException e) {
            System.out.println("Ошибка при подгрузке конфига макросов.");
        }
        DragManager.load();
        dropDown = new DropDown(new StringTextComponent(""));
        initAutoBuy();
        autoBuyUI = new Window(new StringTextComponent(""), itemStorage);
        //autoBuyUI = new AutoBuyUI(new StringTextComponent("A"));
        autoBuyHandler = new AutoBuyHandler();
        autoBuyConfig = new AutoBuyConfig();
        ClientFonts.init();
        eventBus.register(this);
    }

    private final EventKey eventKey = new EventKey(-1);

    public void onKeyPressed(int key) {
        if (functionRegistry.getSelfDestruct().unhooked) return;
        eventKey.setKey(key);
        eventBus.post(eventKey);

        macroManager.onKeyPressed(key);

        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            Minecraft.getInstance().displayGuiScreen(dropDown);
        }
       // if (this.functionRegistry.getAutoBuyUI().isState() && this.functionRegistry.getAutoBuyUI().setting.get() == key) {
          //  Minecraft.getInstance().displayGuiScreen(autoBuyUI);
        }


    private void initAutoBuy() {
        ItemFactory itemFactory = new ItemFactoryImpl();
        CopyOnWriteArrayList<IItem> items = new CopyOnWriteArrayList<>();
        itemStorage = new ItemStorage(items, itemFactory);

        activationLogic = new ActivationLogic(itemStorage, eventBus);
    }

    private void initCommands() {
        Minecraft mc = Minecraft.getInstance();
        Logger logger = new MultiLogger(List.of(new ConsoleLogger(), new MinecraftLogger()));
        List<Command> commands = new ArrayList<>();
        Prefix prefix = new PrefixImpl();
        commands.add(new ListCommand(commands, logger));
        commands.add(new FriendCommand(prefix, logger, mc));
        commands.add(new BindCommand(prefix, logger));
        commands.add(new GPSCommand(prefix, logger));
        commands.add(new ConfigCommand(configStorage, prefix, logger));
        commands.add(new MacroCommand(macroManager, prefix, logger));
        commands.add(new VClipCommand(prefix, logger, mc));
        commands.add(new HClipCommand(prefix, logger, mc));
        commands.add(new StaffCommand(prefix, logger));
        commands.add(new MemoryCommand(logger));
        commands.add(new RCTCommand(logger, mc));

        AdviceCommandFactory adviceCommandFactory = new AdviceCommandFactoryImpl(logger);
        ParametersFactory parametersFactory = new ParametersFactoryImpl();

        commandDispatcher = new StandaloneCommandDispatcher(commands, adviceCommandFactory, prefix, parametersFactory, logger);
    }

    private void initStyles() {
        StyleFactory styleFactory = new StyleFactoryImpl();
        List<Style> styles = new ArrayList<>();

        styles.add(styleFactory.createStyle("Дефолт", new Color(124, 124, 124,255), new Color(175, 175, 175,255)));
        styles.add(styleFactory.createStyle("Client", new Color(5, 197, 255,255), new Color(255, 23, 23,255)));
        styles.add(styleFactory.createStyle("Морской", new Color(26, 60, 176,255), new Color(0, 111, 255,255)));
        styles.add(styleFactory.createStyle("Черничный", new Color(96, 0, 155,255), new Color(138, 65, 196,255)));
        styles.add(styleFactory.createStyle("Альстрофо", new Color(243, 160, 232,255), new Color(171, 250, 243,255)));
        styles.add(styleFactory.createStyle("Хайс", new Color(33, 121, 255, 255), new Color(255, 255, 255,255)));
        styles.add(styleFactory.createStyle("Розовая озеро", new Color(255, 0, 79,255), new Color(95, 195, 228,255)));
        styles.add(styleFactory.createStyle("Кайф", new Color(171, 39, 245,255), new Color(255, 255, 255,255)));
        styles.add(styleFactory.createStyle("Ультра", new Color(13, 60, 77,255), new Color(42, 229, 245,255)));
        styles.add(styleFactory.createStyle("Красная роза", new Color(250, 0, 0,255), new Color(255, 255, 255,255)));
        styles.add(styleFactory.createStyle("Мята", new Color(26, 93, 44, 255), new Color(99, 239, 133, 255)));
        styles.add(styleFactory.createStyle("Лайт", new Color(255, 43, 130, 255), new Color(144, 255, 111, 255)));
        styles.add(styleFactory.createStyle("Кайт", new Color(255, 132, 0, 255), new Color(21, 220, 24,255)));
        styles.add(styleFactory.createStyle("Вейрон", new Color(128, 255, 235, 255), new Color(193, 88, 255,255)));
        styles.add(styleFactory.createStyle("Красный", new Color(255, 0, 0, 255), new Color(193, 14, 14,255)));
        styles.add(styleFactory.createStyle("хз", new Color(207, 0, 255, 255), new Color(255, 0, 244,255)));
        styles.add(styleFactory.createStyle("Лазурный", new Color(0, 191, 255, 255), new Color(0, 123, 255, 255)));
        styles.add(styleFactory.createStyle("Золотой", new Color(255, 215, 0, 255), new Color(218, 165, 32, 255)));
        styles.add(styleFactory.createStyle("Изумруд", new Color(46, 204, 113, 255), new Color(39, 174, 96, 255)));
        styles.add(styleFactory.createStyle("Фиолетовый", new Color(128, 0, 128, 255), new Color(186, 85, 211, 255)));
        styles.add(styleFactory.createStyle("Океан", new Color(0, 102, 204, 255), new Color(0, 51, 102, 255)));
        styles.add(styleFactory.createStyle("Теплый закат", new Color(255, 94, 77, 255), new Color(255, 165, 0, 255)));
        styles.add(styleFactory.createStyle("Градиент", new Color(75, 0, 130, 255), new Color(238, 130, 238, 255)));
        styles.add(styleFactory.createStyle("Глубокий космос", new Color(25, 25, 112, 255), new Color(75, 0, 130, 255)));
        styles.add(styleFactory.createStyle("Лимон", new Color(255, 255, 102, 255), new Color(204, 204, 0, 255)));
        styles.add(styleFactory.createStyle("Грозовой", new Color(112, 128, 144, 255), new Color(47, 79, 79, 255)));
        styles.add(styleFactory.createStyle("Тропический", new Color(255, 140, 0, 255), new Color(0, 128, 0, 255)));
        styles.add(styleFactory.createStyle("Глубина", new Color(0, 0, 139, 255), new Color(70, 130, 180, 255)));
        styles.add(styleFactory.createStyle("Сладкая вата", new Color(255, 182, 193, 255), new Color(255, 105, 180, 255)));
        styles.add(styleFactory.createStyle("Закат в пустыне", new Color(255, 87, 51, 255), new Color(255, 195, 0, 255)));
        styles.add(styleFactory.createStyle("Песчаный бриз", new Color(210, 180, 140, 255), new Color(244, 164, 96, 255)));
        styles.add(styleFactory.createStyle("Ночная тень", new Color(25, 25, 25, 255), new Color(75, 75, 75, 255)));





     /*   styles.add(styleFactory.createStyle("Mojito", "#1D976C", "#1D976C"));
        styles.add(styleFactory.createStyle("Rose Water", "#E55D87", "#5FC3E4"));
        styles.add(styleFactory.createStyle("Anamnisar", "#9796f0", "#fbc7d4"));
        styles.add(styleFactory.createStyle("Ultra Voilet", "#654ea3", "#eaafc8"));
        styles.add(styleFactory.createStyle("Quepal", "#11998e", "#38ef7d"));
        styles.add(styleFactory.createStyle("Intergalactic", "#5cb8f", "#c657f9"));
        styles.add(styleFactory.createStyle("Blush", "#B24592", "#F15F79"));
        styles.add(styleFactory.createStyle("Back to the Future", "#C02425", "#F0CB35"));
        styles.add(styleFactory.createStyle("Green and Blue", "#52f1ab", "#42acf5"));
        styles.add(styleFactory.createStyle("Sin City Red", "#ED213A", "#93291E"));
        styles.add(styleFactory.createStyle("Evening Night", "#005AA7", "#FFFDE4"));
        styles.add(styleFactory.createStyle("Compare Now", "#EF3B36", "#FFFFFF"));
        styles.add(styleFactory.createStyle("Netflix", "#8E0E00", "#1F1C18"));
        styles.add(styleFactory.createStyle("Passion", "#e53935", "#e35d5b"));*/

        styleManager = new StyleManager(styles, styles.get(0));
    }

    public boolean getGlow() {
        return false;
    }

    public FriendStorage getFriendStorage() {
        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserData {
        final String user;
        final int uid;
    }

}
