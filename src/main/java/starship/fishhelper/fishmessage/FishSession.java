package starship.fishhelper.fishMessage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.text.*;
import starship.fishhelper.itemCategory.*;

public class FishSession {
    public String lootName = "";
    public int lootCount = 1;
    public MutableText caughtMessage = null;
    public final List<Text> triggerIcons = new ArrayList<>();
    public int xpGained = 0;

    public boolean isActive = false;
    public boolean isLast = false;
    public long catchTime = 0;

    public Text msgSummary = null;
    public FontFactory.CategoryType catType = null;
    public void reset() {
        lootName = "";
        lootCount = 1;
        triggerIcons.clear();
        xpGained = 0;
        isActive = false;
        caughtMessage = null;
        msgSummary = null;
        isLast = false;
        catchTime = 0;
        catType = null;
    }

    public FontFactory.CategoryType extraCategoryFromName(String name){
        if (name == null) return FontFactory.CategoryType.NORMAL_FISH;

        if (Junk.ITEMS.contains(name)) return FontFactory.CategoryType.JUNK;
        if (Pearl.ITEMS.contains(name)) return FontFactory.CategoryType.PEARL;
        if (Treasure.ITEMS.contains(name)) return FontFactory.CategoryType.TREASURE;
        if (Spirit.ITEMS.contains(name)) return FontFactory.CategoryType.SPIRIT;
        if (Fish.ELUSIVE_PATTERN.matcher(name).find()) return FontFactory.CategoryType.ELUSIVE_FISH;

        return FontFactory.CategoryType.NORMAL_FISH;
    }



}
