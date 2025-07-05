package starship.fishhelper.trevorOpener;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Augment extends Recorder {
    public static final Set<String> NAMES = Set.of(
            "Strong Hook Augment", "XP Magnet Augment", "Boosted Rod Augment", "Strong Pot Augment",
            "Wise Hook Augment", "Fish Magnet Augment", "Speedy Rod Augment", "Wise Pot Augment",
            "Glimmering Hook Augment", "Pearl Magnet Augment", "Graceful Rod Augment", "Glimmering Pot Augment",
            "Greedy Hook Augment", "Treasure Magnet Augment", "Glitched Rod Augment", "Greedy Pot Augment",
            "Lucky Hook Augment", "Spirit Magnet Augment", "Stable Rod Augment", "Lucky Pot Augment"

    );
    private static final Map<String, String> ICON_MAP = new LinkedHashMap<>() {{
        put("Strong Hook Augment", ""); // from mcci pack
        put("XP Magnet Augment", "");
        put("Boosted Rod Augment", "");
        put("Strong Pot Augment", "");
        put("Wise Hook Augment", "");
        put("Fish Magnet Augment", "");
        put("Speedy Rod Augment", "");
        put("Wise Pot Augment", "");
        put("Glimmering Hook Augment", "");
        put("Pearl Magnet Augment", "");
        put("Graceful Rod Augment", "");
        put("Glimmering Pot Augment", "");
        put("Greedy Hook Augment", "");
        put("Treasure Magnet Augment", "");
        put("Glitched Rod Augment", "");
        put("Greedy Pot Augment", "");
        put("Lucky Hook Augment", "");
        put("Spirit Magnet Augment", "");
        put("Stable Rod Augment", "");
        put("Lucky Pot Augment", "");
    }};

    @Override
    protected Set<String> getNames() {
        return NAMES;
    }
    @Override
    public Text summary() {
        MutableText root = Text.literal("  ");
        for (Map.Entry<String, String> entry : ICON_MAP.entrySet()) {
            String name = entry.getKey();
            String icon = entry.getValue();
            int count = record.getOrDefault(name, 0);
            if (record.get(name) == 0) continue;
            root.append(Text.literal(icon).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(
                    Identifier.of("mcc", "icon"))));
            root.append(Text.literal(" x"+count+"  ").formatted(Formatting.GRAY));
            if ((name.contains("Strong Pot Augment") || name.contains("Wise Pot Augment")
                    || name.contains("Glimmering Pot Augment") || name.contains("Greedy Pot Augment"))
                    && record.values().stream().allMatch(v -> v != 0))
                root.append(Text.literal("\n  "));
        }
        return root;
    }
}
