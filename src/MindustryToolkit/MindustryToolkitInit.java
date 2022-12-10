package MindustryToolkit;

import MindustryToolkit.autofill.AutoFill;
import MindustryToolkit.settings.Settings;
import arc.util.*;
import mindustry.mod.*;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.content.Blocks;
import mindustry.world.blocks.storage.CoreBlock;

public class MindustryToolkitInit extends Mod {
    Settings settings = new Settings();
    AutoFill autoFill = new AutoFill();

    @Override
    public void loadContent() {
        Log.info("Loading some example content.");
    }

    @Override
    public void init() {
        settings.init();
        autoFill.init();
    }
}
