package MindustryToolkit.sectorized;

import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.SectorizedSettings;
import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.game.EventType;

import java.util.Objects;


public class Sectorized {
    public static final Seq<SectorizedServer> sectorizedServers = Seq.with(new SectorizedServer("89.58.37.204", 6567));
    public static final byte StateWait = 0;
    public static final byte StateConnectingToSectorized = 1;
    public static final byte StateConnectingToOther = 2;
    public byte state = Sectorized.StateWait;
    public boolean appliedChanges = false;

    public void init() {
        SectorizedConstants.init();
        SectorizedSettings.init();
        Events.on(EventType.ClientServerConnectEvent.class, this::onServerConnect);
        Events.on(EventType.WorldLoadBeginEvent.class, this::onWorldLoadBegin);
        Events.on(EventType.ClientServerConnectEvent.class, ev -> Log.info("ClientServerConnectEvent"));
        Events.on(EventType.WorldLoadBeginEvent.class, ev -> Log.info("WorldLoadBeginEvent"));
        /*Events.on(EventType.WorldLoadEndEvent.class, ev -> Log.info("WorldLoadEndEvent"));
        Events.on(EventType.WorldLoadEvent.class, ev -> Log.info("WorldLoadEvent"));*/

        // Vars.ui.hudfrag.setHudText("Lmao");
        // Vars.ui.hudfrag.showToast("LMAO");
        // Menus.infoPopup("XD", 5.1f, 0, 50, 50, 0, 0);
    }

    private void onServerConnect(EventType.ClientServerConnectEvent event) {
        if (!IdentitySettings.enabled) return;
        Log.info("Connected to: " + event.ip + ":" + event.port);
        for (SectorizedServer server : Sectorized.sectorizedServers) {
            if (Objects.equals(server.ip, event.ip) && server.port == event.port) {
                state = Sectorized.StateConnectingToSectorized;
                return;
            }
        }
        this.resetChanges();
        state = Sectorized.StateConnectingToOther;
    }

    private void onWorldLoadBegin(EventType.WorldLoadBeginEvent event) {
        if (!IdentitySettings.enabled) return;
        if (state == Sectorized.StateWait) {
            this.resetChanges();
            return;
        }
        if (state == Sectorized.StateConnectingToSectorized) {
            this.applyChanges();
            return;
        }
        if (state == Sectorized.StateConnectingToOther) this.resetChanges();
    }

    /**
     * Do changes reset by resetChanges when joining different server than Sectorized
     */
    private void applyChanges() {
        state = Sectorized.StateWait;
        if (appliedChanges) return;
        Log.info("[cyan]Apply changes!");
        // XD To do
        appliedChanges = true;
    }

    /**
     * Reset changes made by applyChanges when joining Sectorized
     */
    private void resetChanges() {
        state = Sectorized.StateWait;
        if (!appliedChanges) return;
        Log.info("[cyan]Reset changes!");
        // XD To do
        appliedChanges = false;
    }
}
