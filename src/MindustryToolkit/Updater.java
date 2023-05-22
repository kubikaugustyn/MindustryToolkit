package MindustryToolkit;

import MindustryToolkit.settings.Settings;
import arc.util.Http;
import arc.util.Http.HttpResponse;
import arc.util.Log;
import arc.util.io.Streams;
import arc.util.serialization.Jval;
import arc.files.Fi;
import arc.files.ZipFi;
import mindustry.mod.Mods.LoadedMod;

import static arc.Core.*;
import static mindustry.Vars.*;

import java.net.*;

public class Updater {

    public static final String repo = "kubikaugustyn/MindustryToolkit";

    public static LoadedMod mod;
    public static String url;

    public static float progress;
    public static String download;

    public static void init() {
        mod = mods.getMod(MindustryToolkitInit.class);
        url = ghApi + "/repos/" + repo + "/releases/latest";

        Jval meta = Jval.read(new ZipFi(mod.file).child("mod.hjson").readString());
        mod.meta.author = meta.getString("author"); // restore colors in mod's meta
        mod.meta.description = meta.getString("description");
    }

    public static void check() {
        Http.get(url, res -> {
            Jval json = Jval.read(res.getResultAsString());
            String latest = json.getString("tag_name").substring(1);
            download = json.get("assets").asArray().get(0).getString("browser_download_url");

            if (!latest.equals(mod.meta.version)) ui.showCustomConfirm(
                    Settings.getText("updater.title"), bundle.format(Settings.getText("updater.info"), mod.meta.version, latest),
                    Settings.getText("updater.load"), "@ok", Updater::update, () -> {});
        }, a->Log.err("[cyan]Failed to check for updates."));
    }

    public static void update() {
        try { // dancing with tambourines, just to remove the old mod
            if (mod.loader instanceof URLClassLoader cl) cl.close();
            mod.loader = null;
        } catch (Throwable e) { Log.err(e); } // this has never happened before, but everything can be

        ui.loadfrag.show("@downloading");
        ui.loadfrag.setProgress(() -> progress);

        Http.get(download, Updater::handle, a->Log.err("[cyan]Failed to update."));
    }

    public static void handle(HttpResponse res) {
        try {
            Fi file = tmpDirectory.child(repo.replace("/", "") + ".zip");
            Streams.copyProgress(res.getResultAsStream(), file.write(false), res.getContentLength(), 4096, p -> progress = p);

            mods.importMod(file).setRepo(repo);
            file.delete();

            app.post(ui.loadfrag::hide);
            ui.showInfoOnHidden("@mods.reloadexit", app::exit);
        } catch (Throwable e) { Log.err(e); }
    }
}