package starship.fishhelper;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import starship.fishhelper.fishmessage.FishMessage;

public class MCCIFishHelper implements ClientModInitializer {

	public static final String MOD_ID = "compact-msg";
	public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

	private FishMessage fishmessage;
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		logger.info("Initializing MCCIFishHelper");
		this.fishmessage = new FishMessage(this);
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	public void tick(MinecraftClient client) {
//		this.autofish.tick(client);
		this.fishmessage.tick(client);
	}

}