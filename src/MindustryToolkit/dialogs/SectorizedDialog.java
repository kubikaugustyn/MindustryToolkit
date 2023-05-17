package MindustryToolkit.dialogs;

import MindustryToolkit.settings.SectorizedSettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class SectorizedDialog extends FeatureDialog {
    public static String title = Settings.getText("sectorized.title");

    private final AtomicBoolean enabled = new AtomicBoolean(SectorizedSettings.enabled);
    public SectorizedDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                SectorizedDialog.onClose();
            return false;
        });
    }

    public void rebuild() {
        this.enabled.set(SectorizedSettings.enabled);
        this.main.checkPref(Settings.getText("sectorized.enabled"), SectorizedSettings.enabled, this.enabled::set);
        this.main.pref(new ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        SectorizedSettings.enabled = this.enabled.get();
        SectorizedSettings.saveSettings();
    }

    private static void onClose() {
        SectorizedSettings.readSettings();
    }
}
