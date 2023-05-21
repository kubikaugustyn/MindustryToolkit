package MindustryToolkit.identity;

import MindustryToolkit.dialogs.IdentityServerDialog;
import MindustryToolkit.settings.IdentitySettings;
import arc.Core;
import arc.input.KeyCode;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.Log;
import mindustry.Vars;
import mindustry.net.Net;

import static mindustry.Vars.state;
import static mindustry.Vars.ui;

public class Identity {
    private static IdentityServerDialog identityServerDialog;

    public void init() {
        Identity.identityServerDialog = new IdentityServerDialog();
        IdentitySettings.init();

        Core.scene.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, KeyCode keyCode) {
                if (!state.isMenu() &&
                        !ui.chatfrag.shown() &&
                        !ui.schematics.isShown() &&
                        !ui.database.isShown() &&
                        !ui.consolefrag.shown() &&
                        !ui.content.isShown() &&
                        Vars.net.active()) {
                    Log.info("Key event: " + keyCode.value);
                    if (IdentitySettings.enabled && IdentitySettings.quickKey.equalsIgnoreCase(keyCode.value)) {
                        identityServerDialog.show();
                    }
                }

                return super.keyDown(event, keyCode);
            }
        });
    }
}
