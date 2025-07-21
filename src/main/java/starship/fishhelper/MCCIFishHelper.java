package starship.fishhelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import starship.fishhelper.fishMessage.FishMessage;
import starship.fishhelper.modMenu.ConfigData;
import starship.fishhelper.trevorOpener.TrevorOpener;

public class MCCIFishHelper implements ClientModInitializer {
    public static final String MOD_ID = "compact-msg";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);
    public static MCCIFishHelper instance;
    private FishMessage fishmessage;
    private TrevorOpener trevoropener;

    public static MCCIFishHelper getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        logger.info("Initializing MCCIFishHelper");
        instance = this;
        this.loadConfig();
        this.fishmessage = new FishMessage(this);
        this.trevoropener = new TrevorOpener(this);

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
                if (title.contains("INFINIBAG")) {
                    this.trevoropener.detectScreenINFINIBAG();
                    return;
                }

                if (title.contains("SUMMARY")) {
                    this.trevoropener.detectScreenSUMMARYOpen();
                    ScreenEvents.remove(screen).register((screen1) -> {
                        this.trevoropener.detectScreenSUMMARYClose();
                    });
                    return;
                }
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
    }

    public void tick(MinecraftClient client) {
        this.fishmessage.tick(client);
        this.trevoropener.tick(client);
// test git
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