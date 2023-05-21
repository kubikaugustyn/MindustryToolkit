package MindustryToolkit.dialogs;

import arc.scene.ui.Dialog;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;
import arc.scene.Element;

public class FeatureDialog extends BaseDialog {
    public SettingsMenuDialog.SettingsTable main;

    public FeatureDialog(String title) {
        super(title);
        this.addCloseButton();
    }

    public Dialog show() {
        this.cont.removeChild(this.main);
        this.main = new SettingsMenuDialog.SettingsTable();
        this.main.center();
        this.cont.add(main);
        this.rebuild();
        return super.show();
    }

    public void rebuild() {
    }

    public static class ElementSetting<T extends Element> extends SettingsMenuDialog.SettingsTable.Setting {
        T elem;

        public ElementSetting(T elem) {
            super(null);
            this.elem = elem;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            table.add(this.elem);
            table.row();
        }
    }

    // Sorry Pointifix#4403 for stealing your classes :-D
    public static class DescriptionSetting extends SettingsMenuDialog.SettingsTable.Setting {
        String desc;
        boolean doRow = true;

        public DescriptionSetting(String desc) {
            super(null);
            this.desc = desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void doRow(boolean doRow) {
            this.doRow = doRow;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            table.labelWrap(this.desc).fillX().center().get().setWrap(true);
            if (doRow) table.row();
        }
    }

    public static class DividerSetting extends SettingsMenuDialog.SettingsTable.Setting {
        public DividerSetting() {
            super(null);
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            table.image().growX().pad(10, 0, 10, 0).color(Pal.gray);
            table.row();
        }
    }

    public static class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting {
        String name;
        Runnable clicked;

        public ButtonSetting(String name, Runnable clicked) {
            super(name);
            this.name = name;
            this.clicked = clicked;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            table.button(name, clicked).margin(14).width(240f).pad(6);
            table.row();
        }
    }
}
