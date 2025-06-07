package starship.fishhelper;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.List;

import starship.fishhelper.fishMessage.FishMessage;
import starship.fishhelper.modMenu.ConfigData;

public class MCCIFishHelper implements ClientModInitializer {
	public static MCCIFishHelper instance;
	public static final String MOD_ID = "compact-msg";
	public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

	private FishMessage fishmessage;

//	private GenericContainerScreen pendingScreen = null;
//	private int delayTicks = 0;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		logger.info("Initializing MCCIFishHelper");
		instance = this;
		this.loadConfig();
		this.fishmessage = new FishMessage(this);
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);

//		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
//			if (screen instanceof GenericContainerScreen containerScreen) {
////				String title = containerScreen.getTitle().getString();
//				pendingScreen = containerScreen;
//				delayTicks = 2;
//			}
//		});
	}

	public void tick(MinecraftClient client) {
		this.fishmessage.tick(client);
//		this.augmentTracker.tick(client);

	}

	public static MCCIFishHelper getInstance() {
		return instance;
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