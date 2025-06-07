package starship.fishhelper.modMenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import starship.fishhelper.MCCIFishHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;


public class ConfigScreen {

    public static Screen buildScreen(MCCIFishHelper fishHelper, Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.fishhelper.config"));

        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("category.fishhelper.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigData config = fishHelper.getConfig();

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.fishhelper.compact_fishmsg"), config.enableCompactFishmsg)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableCompactFishmsg = newValue)
                .build());

        builder.setSavingRunnable(fishHelper::saveConfig);

        return builder.build();
    }

}
