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

import arc.Core;
import mindustry.Vars;

import java.net.*;

public class Updater {

    public static final String repo = "kubikaugustyn/MindustryToolkit";

    public static LoadedMod mod;
    public static String url;

    public static float progress;
    public static String download;

    public static void init() {
        mod = Vars.mods.getMod(MindustryToolkitInit.class);
        url = Vars.ghApi + "/repos/" + repo + "/releases/latest";

        Jval meta = Jval.read(new ZipFi(mod.file).child("mod.hjson").readString());
        mod.meta.author = meta.getString("author"); // restore colors in mod's meta
        mod.meta.description = meta.getString("description");
    }

    public static void check() {
        Http.get(url, res -> {
            Jval json = Jval.read(res.getResultAsString());
            String latest = json.getString("tag_name").substring(1);
            Jval.JsonArray assets = json.get("assets").asArray();
            for (Jval obj : assets) Log.info("Asset: " + obj.toString());
            download = json.get("assets").asArray().get(0).getString("browser_download_url");

            if (!latest.equals(mod.meta.version)) Vars.ui.showCustomConfirm(
                    Settings.getText("updater.title"), Core.bundle.format(Settings.getText("updater.info"), mod.meta.version, latest),
                    Settings.getText("updater.load"), "@ok", Updater::update, () -> {
                    });
        }, a -> Log.err("[cyan]Failed to check for updates."));
    }

    public static void update() {
        try { // dancing with tambourines, just to remove the old mod
            if (mod.loader instanceof URLClassLoader cl) cl.close();
            mod.loader = null;
        } catch (Throwable e) {
            Log.err(e);
        } // this has never happened before, but everything can be

        Vars.ui.loadfrag.show("@downloading");
        Vars.ui.loadfrag.setProgress(() -> progress);

        Http.get(download, Updater::handle, a -> Log.err("[cyan]Failed to update."));
    }

    public static void handle(HttpResponse res) {
        try {
            Fi file = Vars.tmpDirectory.child(repo.replace("/", "") + ".zip");
            Streams.copyProgress(res.getResultAsStream(), file.write(false), res.getContentLength(), 4096, p -> progress = p);

            Vars.mods.importMod(file).setRepo(repo);
            file.delete();

            Core.app.post(Vars.ui.loadfrag::hide);
            Vars.ui.showInfoOnHidden("@mods.reloadexit", Core.app::exit);
        } catch (Throwable e) {
            Log.err(e);
        }
    }
}