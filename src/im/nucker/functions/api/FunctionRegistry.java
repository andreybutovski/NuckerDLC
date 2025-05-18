package im.nucker.functions.api;

import com.google.common.eventbus.Subscribe;
import im.nucker.NuckerDLC;
import im.nucker.events.EventKey;
import im.nucker.functions.impl.combat.*;
import im.nucker.functions.impl.combat.PearlTarget;
import im.nucker.functions.impl.misc.*;
import im.nucker.functions.impl.movement.*;
import im.nucker.functions.impl.player.*;
import im.nucker.functions.impl.render.*;
import im.nucker.utils.render.font.Font;
import lombok.Getter;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class FunctionRegistry {
    private final List<Function> functions = new CopyOnWriteArrayList<>();


    private Nuker nuker;
    private Advertising advertising;
    private AhHelper ahHelper;
    private Hotbar hotbar;
    private FOV fov;
    private TotemNotify totemNotify;
    private TargetESP targetESP;
    private MoveUpgrade moveUpgrade;
    private FTHelper ftHelper;
    private SwingAnimation swingAnimation;
    private AutoGapple autoGapple;
    private AutoSprint autoSprint;
    private Velocity velocity;
    private NoRender noRender;
    private Timer timer;
    private AutoTool autoTool;
    private xCarry xcarry;
    private ElytraHelper elytrahelper;
    private Phase phase;
    private ItemSwapFix itemswapfix;
    private AutoPotion autopotion;
    private TriggerBot triggerbot;
    private NoJumpDelay nojumpdelay;
    private ClickFriend clickfriend;
    private InventoryMove inventoryMove;
    private ESP esp;
    private AutoTransfer autoTransfer;
    private ItemCooldown itemCooldown;
    private ClickPearl clickPearl;
    private AutoSwap autoSwap;
    private AutoArmor autoArmor;
    private Hitbox hitbox;
    private HitSound hitsound;
    private NoPush antiPush;
    private FreeCam freeCam;
    private HWStealer hwStealer;
    private AutoLeave autoLeave;
    private AutoAccept autoAccept;
    private NoEventDelay noEventDelay;
    private AutoRespawn autoRespawn;
    private Fly fly;
    private TargetStrafe targetStrafe;
    private ClientSounds clientSounds;
    private AutoTotem autoTotem;
    private NoSlow noSlow;
    private Arrows arrows;
    private AutoExplosion autoExplosion;
    private NoRotate noRotate;
    private KillAura killAura;
    private AntiBot antiBot;
    private Trails trails;
    private Crosshair crosshair;
    private DeathEffect deathEffect;
    private Strafe strafe;
    private WorldTime worldTime;
    private ViewModel viewModel;
    private ElytraFly elytraFly;
    private JumpCircle jumpCircle;
    private ItemPhysic itemPhysic;
    private Predictions predictions;
    private NoEntityTrace noEntityTrace;
    private NoClip noClip;
    private ItemScroller itemScroller;
    private AutoFish autoFish;
    private StorageESP storageESP;
    private Spider spider;
    private NameProtect nameProtect;
    private NoInteract noInteract;
    private GlassHand glassHand;
    private Tracers tracers;
    private Unhook selfDestruct;
    private LeaveTracker leaveTracker;
    private BoatFly boatFly;
    private AntiAFK antiAFK;
    private PortalGodMode portalGodMode;
    private BetterMinecraft betterMinecraft;
    private Backtrack backtrack;
    private SeeInvisibles seeInvisibles;
    private FreeLook freeLook;
    private NoServerDesync noServerDesync;
    private CasinoBot casinoBot;
    private FireFly fireFly;
    private FullBright fullBright;
    private HatModule hatModule;
    private HungryStatus hungryStatus;
    private GlowParticles glowParticles;
    private GlowEsp glowEsp;
    private BetterChat betterChat;
    private InventorySorter inventorySorter;
    private Optimization optimization;
    private HWhelper hWhelper;
    private FastEXP fastEXP;
    private SexAura sexAura;
    private HitParticles hitParticles;
    private ElytraBounce elytraBounce;
    private BaseFinder baseFinder;
    private AttackAura attackaura;
    private Speed1  speed1;
    private WaterSpeedFT waterSpeedFT;
    private UniversalFarmer  universalFarmer;
    private AncientXray ancientXray;
    private ClickGui clickGui;
    private PearlTarget pearlTarget;
    private AspectRatio aspectRatio;
    private ShulkerChecker shulkerChecker;
    private Theme theme;
    private VulcanESP vulcanESP;
    private AutoGPS autoGps;
    private Tags tags;
    private AutoFarm autoFarm;
    private China china;
    private CustomFog customFog;
    private CreeperFarm creeperFarm;
    private WaterMark waterMark;
    private ChunkAnimator chunkAnimator;
    private HUD hud;
    private NoSlowFt noSlowFt;
    private SpeedFT speedFT;
    private FastBreak fastBreak;
    private SpiderSunWay spiderSunWay;
    private AutoFurnace autoFurnace;
    private MoneyBegger moneyBegger;
    private ElytraTarget elytraTarget;
    private TridentPredictions tridentPredictions;
    private ArrowPredictions arrowPredictions;
    private TargetESP2 targetESP2;
    private Snow snow;


    public void init() {
        registerAll(waterMark = new WaterMark(),snow = new Snow(),targetESP2 = new TargetESP2(), arrowPredictions = new ArrowPredictions(),tridentPredictions = new TridentPredictions(),elytraTarget = new ElytraTarget(), moneyBegger = new MoneyBegger(),autoFurnace = new AutoFurnace(),spiderSunWay = new SpiderSunWay(), fastBreak = new FastBreak(),speedFT = new SpeedFT(), noSlowFt = new NoSlowFt(),hud = new HUD(),chunkAnimator = new ChunkAnimator(),customFog = new CustomFog(),creeperFarm = new CreeperFarm(), china = new China(),autoFarm = new AutoFarm(), theme = new Theme(),tags = new Tags(),autoGps = new AutoGPS(), vulcanESP= new VulcanESP(), clickGui = new ClickGui(),shulkerChecker = new ShulkerChecker(), aspectRatio = new AspectRatio(),pearlTarget = new PearlTarget(),new AncientXray(),new Farm(),new UniversalFarmer(),waterSpeedFT = new WaterSpeedFT(),new Speed1(),attackaura = new AttackAura(),  sexAura = new SexAura(),new ClanUpgrader(),new BaseFinder(),  nuker = new Nuker(), ahHelper = new AhHelper(),  advertising = new Advertising(), hotbar = new Hotbar(),  fov = new FOV(), totemNotify = new TotemNotify(),targetESP = new TargetESP(killAura), moveUpgrade = new MoveUpgrade(), ftHelper = new FTHelper(), elytraBounce = new ElytraBounce(), hitParticles = new HitParticles(), fastEXP = new FastEXP(), hWhelper = new HWhelper(), optimization = new Optimization(), inventorySorter = new InventorySorter(), betterChat = new BetterChat(),  glowEsp = new GlowEsp(), glowParticles = new GlowParticles(), hungryStatus = new HungryStatus(), hatModule = new HatModule(), fullBright = new FullBright(), fireFly = new FireFly(), casinoBot = new CasinoBot(), noServerDesync = new NoServerDesync(), freeLook = new FreeLook(), autoGapple = new AutoGapple(), autoSprint = new AutoSprint(), velocity = new Velocity(), noRender = new NoRender(), autoTool = new AutoTool(), xcarry = new xCarry(), seeInvisibles = new SeeInvisibles(), elytrahelper = new ElytraHelper(), phase = new Phase(), itemswapfix = new ItemSwapFix(), autopotion = new AutoPotion(), noClip = new NoClip(), triggerbot = new TriggerBot(), nojumpdelay = new NoJumpDelay(), clickfriend = new ClickFriend(), inventoryMove = new InventoryMove(), esp = new ESP(), autoTransfer = new AutoTransfer(), autoArmor = new AutoArmor(), hitbox = new Hitbox(), hitsound = new HitSound(), antiPush = new NoPush(),  freeCam = new FreeCam(), hwStealer = new HWStealer(), autoLeave = new AutoLeave(), autoAccept = new AutoAccept(), autoRespawn = new AutoRespawn(), fly = new Fly(), clientSounds = new ClientSounds(), noSlow = new NoSlow(), arrows = new Arrows(), autoExplosion = new AutoExplosion(), noRotate = new NoRotate(), antiBot = new AntiBot(), trails = new Trails(), crosshair = new Crosshair(), autoTotem = new AutoTotem(), itemCooldown = new ItemCooldown(), killAura = new KillAura(autopotion), clickPearl = new ClickPearl(itemCooldown), autoSwap = new AutoSwap(autoTotem), targetStrafe = new TargetStrafe(killAura), strafe = new Strafe(targetStrafe, killAura), swingAnimation = new SwingAnimation(killAura), worldTime = new WorldTime(), viewModel = new ViewModel(), elytraFly = new ElytraFly(), jumpCircle = new JumpCircle(), itemPhysic = new ItemPhysic(), predictions = new Predictions(), noEntityTrace = new NoEntityTrace(), itemScroller = new ItemScroller(), autoFish = new AutoFish(), storageESP = new StorageESP(), spider = new Spider(), timer = new Timer(), nameProtect = new NameProtect(), noInteract = new NoInteract(), glassHand = new GlassHand(), tracers = new Tracers(), selfDestruct = new Unhook(), leaveTracker = new LeaveTracker(), antiAFK = new AntiAFK(), portalGodMode = new PortalGodMode(), betterMinecraft = new BetterMinecraft(), backtrack = new Backtrack(), new LongJump(), new XrayBypass(), new Parkour(), new RWHelper());

        NuckerDLC.getInstance().getEventBus().register(this);
    }

    private void registerAll(Function... Functions) {
        Arrays.sort(Functions, Comparator.comparing(Function::getName));

        functions.addAll(List.of(Functions));
    }

    public List<Function> getSorted(Font font, float size) {
        return functions.stream().sorted((f1, f2) -> Float.compare(font.getWidth(f2.getName(), size), font.getWidth(f1.getName(), size))).toList();
    }

    @Subscribe
    private void onKey(EventKey e) {
        if (selfDestruct.unhooked) return;
        for (Function Function : functions) {
            if (Function.getBind() == e.getKey()) {
                Function.toggle();
            }
        }
    }


    }

