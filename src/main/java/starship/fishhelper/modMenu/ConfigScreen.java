package starship.fishhelper.modMenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import starship.fishhelper.MCCIFishHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import java.util.List;


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
                .setTooltip(Text.translatable("tooltip.fishhelper.compact_fishmsg"))
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.fishhelper.trevor_opener"), config.enableTreasureReciMsg)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.enableTreasureReciMsg = newValue)
                .setTooltip(Text.translatable("tooltip.fishhelper.trevor_opener"))
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.fishhelper.record_overlay"), config.enableFishRecordOverlay)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableFishRecordOverlay = newValue)
                .setTooltip(Text.translatable("tooltip.fishhelper.record_overlay"))
                .build());

        Window window = MinecraftClient.getInstance().getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();

        category.addEntry(entryBuilder.startSubCategory(
            Text.translatable("group.fishhelper.render_settings"),
            List.of(
                entryBuilder.startIntSlider(Text.translatable("option.fishhelper.render_x"), config.fishRecordRenderTextX, 0, scaledWidth)
                    .setDefaultValue(10)
                    .setSaveConsumer(newValue -> config.fishRecordRenderTextX = newValue)
                    .build(),
                entryBuilder.startIntSlider(Text.translatable("option.fishhelper.render_y"), config.fishRecordRenderTextY, 0, scaledHeight)
                    .setDefaultValue(10)
                    .setSaveConsumer(newValue -> config.fishRecordRenderTextY = newValue)
                    .build(),
                entryBuilder.startFloatField(Text.translatable("option.fishhelper.font_size"), config.fishRecordRenderScale)
                    .setDefaultValue(1.0f)
                    .setSaveConsumer(newValue -> config.fishRecordRenderScale = newValue)
                    .build(),
                entryBuilder.startIntSlider(Text.translatable("option.fishhelper.bg_color"), config.fishRecordBackgroundAlphaColor, 0, 255)
                    .setDefaultValue(0x88)
                    .setSaveConsumer(newValue -> config.fishRecordBackgroundAlphaColor = newValue)
                    .build(),
                entryBuilder.startColorField(Text.translatable("option.fishhelper.text_color"), config.fishRecordTextRGBColor)
                    .setDefaultValue(0xFFFFFF)
                    .setSaveConsumer(newValue -> config.fishRecordTextRGBColor = newValue)
                    .build(),
                entryBuilder.startBooleanToggle(Text.translatable("option.fishhelper.cute_icon"), config.fishRecordIconShows)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.fishRecordIconShows = newValue)
                    .build()

            )).build());

        builder.setSavingRunnable(fishHelper::saveConfig);

        return builder.build();
    }

}
