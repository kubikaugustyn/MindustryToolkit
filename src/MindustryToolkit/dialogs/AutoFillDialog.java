package MindustryToolkit.dialogs;

import MindustryToolkit.settings.Settings;
import mindustry.ui.dialogs.BaseDialog;

public class AutoFillDialog extends BaseDialog {
    public AutoFillDialog() {
        super(Settings.getText("autofill.title"));
    }
}
