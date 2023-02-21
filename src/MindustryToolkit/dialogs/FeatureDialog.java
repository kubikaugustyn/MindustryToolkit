package MindustryToolkit.dialogs;

import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class FeatureDialog extends BaseDialog {
    public SettingsMenuDialog.SettingsTable main;

    public FeatureDialog(String title) {
        super(title);
        this.addCloseButton();
        this.cont.add(this.main = new SettingsMenuDialog.SettingsTable());
        this.main.center();
    }
}
