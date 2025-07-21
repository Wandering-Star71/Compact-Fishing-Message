package starship.fishhelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import starship.fishhelper.fishMessage.FishMessage;
import starship.fishhelper.modMenu.ConfigData;
import starship.fishhelper.trevorOpener.TrevorOpener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCCIFishHelper implements ClientModInitializer {
    public static final String MOD_ID = "compact-fish-msg";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);
    public static MCCIFishHelper instance;
    private FishMessage fishmessage;
    private TrevorOpener trevoropener;

    private GenericContainerScreen pendingScreen = null;
    private int delayTicks = 0;

    public static MCCIFishHelper getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        logger.info("Initializing MCCIFishHelper");
        instance = this;
        this.loadConfig();
        this.fishmessage = new FishMessage(this);
        this.trevoropener = new TrevorOpener(this);

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
//                MCCIFishHelper.logger.info("title is: {}", title);
                if (title.contains("INFINIBAG")) {
                    this.trevoropener.detectScreenINFINIBAG();
//                    pendingScreen = containerScreen;
//                    delayTicks = 2;
                    return;
                }

                if (title.contains("SUMMARY")) {
                    this.trevoropener.detectScreenSUMMARYOpen();
                    ScreenEvents.remove(screen).register((screen1) -> {
//                        MCCIFishHelper.logger.info("GUI closed: {}", title);
                        this.trevoropener.detectScreenSUMMARYClose();
                    });
                    return;
                }
//                pendingScreen = null;
//                delayTicks = 0;
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("TreasureOpenEvent")
                    .then(ClientCommandManager.literal("create").executes(context -> {
                        this.trevoropener.eventStart();
                        return 0;
                    }))
                    .then(ClientCommandManager.literal("stop").executes(context -> {
                        this.trevoropener.eventEnd();
                        return 0;
                    }))
            );
        });

//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (client.player != null && client.player.age == 20) {
//                test();
//            }
//        });
    }

    public void tick(MinecraftClient client) {
        this.fishmessage.tick(client);
        this.trevoropener.tick(client);
//		this.augmentTracker.tick(client);
//        if (pendingScreen != null && delayTicks > 0) {
//            delayTicks--;
//			return;
//        }
//        if (pendingScreen != null) {
//            GenericContainerScreenHandler handler = pendingScreen.getScreenHandler();
//            String title = pendingScreen.getTitle().getString();
//
//			List<Slot> slots = handler.slots;
//			for (int i = 0; i < slots.size(); i++) {
//				Slot slot = slots.get(i);
//				ItemStack stack = slot.getStack();
//				if (stack.getName().getString().contains("Air"))
//					continue;
//				MCCIFishHelper.logger.info("Slot {}: {} ×{}", i, stack.getName().getString(), stack.getCount());
//			}
//
//            pendingScreen = null;
//        }

    }

    public void loadConfig() {
        ConfigData.load();
    }

    public void saveConfig() {
        ConfigData.save();
    }

    public ConfigData getConfig() {
        return ConfigData.getInstance();
    }

}