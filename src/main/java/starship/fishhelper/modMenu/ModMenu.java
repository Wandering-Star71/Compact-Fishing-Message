package starship.fishhelper.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import starship.fishhelper.MCCIFishHelper;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreen.buildScreen(MCCIFishHelper.getInstance(), parent);
    }
}