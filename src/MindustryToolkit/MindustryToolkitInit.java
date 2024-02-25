package MindustryToolkit;

import MindustryToolkit.autofill.AutoFill;
import MindustryToolkit.identity.Identity;
import MindustryToolkit.sectorized.Sectorized;
import MindustryToolkit.settings.Settings;
import arc.util.*;
import mindustry.content.Blocks;
import mindustry.mod.*;

public class MindustryToolkitInit extends Mod {
    public static Settings settings = new Settings();
    public static AutoFill autoFill = new AutoFill();
    public static Identity identity = new Identity();
    public static Sectorized sectorized = new Sectorized();

    @Override
    public void loadContent() {
        Log.info("[blue]Loading some example content.");
    }

    @Override
    public void init() {
        /*Log.info("[blue]Initialising, duo id is: ");
        Log.info("[yellow]" + Blocks.duo.name);*/
        Log.info("[blue]Initialising Mindustry Toolkit");
        Updater.init();
        settings.init();
        autoFill.init();
        identity.init();
        sectorized.init();
        Log.info("[blue]Initialised Mindustry Toolkit");

        Updater.check();
    }
}
