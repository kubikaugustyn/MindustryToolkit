package MindustryToolkit.sectorized;

import MindustryToolkit.settings.SectorizedSettings;
import arc.Events;
import mindustry.game.EventType;

public class Sectorized {
    public void init() {
        SectorizedSettings.init();
        // Events.on(EventType.ClientServerConnectEvent.class, this::onServerConnect);
    }

    /*private void onServerConnect(EventType.ClientServerConnectEvent event) {
        // Log.info("Connected to: " + event.ip + ":" + event.port);
    }*/
}
