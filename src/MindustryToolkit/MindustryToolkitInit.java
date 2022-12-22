package MindustryToolkit;

import MindustryToolkit.autofill.AutoFill;
import MindustryToolkit.settings.Settings;
import arc.util.*;
import mindustry.content.Blocks;
import mindustry.mod.*;

public class MindustryToolkitInit extends Mod {
    Settings settings = new Settings();
    AutoFill autoFill = new AutoFill();

    @Override
    public void loadContent() {
        Log.info("Loading some example content.");
    }

    @Override
    public void init() {
        Log.info("[blue]Initialising, duo id is: ");
        Log.info("[yellow]" + Blocks.duo.name);
        settings.init();
        autoFill.init();
    }
}
