package MindustryToolkit.identity;

import MindustryToolkit.Updater;
import MindustryToolkit.dialogs.IdentityServerDialog;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.ui.*;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.scene.utils.Elem;
import arc.struct.SnapshotSeq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.net.Net;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.JoinDialog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

import static mindustry.Vars.*;

public class Identity {
    private static IdentityServerDialog identityServerDialog;

    /*private Table local,//Local Servers
            remote,// Remote Servers
            global,// Community Servers
            hosts;// <container for all of the above>*/
    private Table cont;

    public void init() {
        Identity.identityServerDialog = new IdentityServerDialog();
        IdentitySettings.init();

        Core.scene.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, KeyCode keyCode) {
                //Log.info("Key event: " + keyCode.value);
                if (!state.isMenu() &&
                        !ui.chatfrag.shown() &&
                        !ui.schematics.isShown() &&
                        !ui.database.isShown() &&
                        !ui.consolefrag.shown() &&
                        !ui.content.isShown() &&
                        !ui.logic.isShown() &&
                        !ui.join.isShown() &&
                        Vars.net.active()) {
                    if (IdentitySettings.enabled && IdentitySettings.quickKey.equalsIgnoreCase(keyCode.value)) {
                        identityServerDialog.show(false);
                    }
                }/* else if (ui.join.isShown()) {
                    // Just to test which one is which
                    switch (keyCode.value) {
                        case "A" -> local.clear();
                        case "B" -> remote.clear();
                        case "C" -> global.clear();
                        case "D" -> hosts.clear();
                    }
                }*/

                return super.keyDown(event, keyCode);
            }
        });

        ui.join.shown(this::changeJoinMenu);

        try {
            extractJoinTables();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Vars.ui.showOkText(Settings.getText("title"), Settings.getText("identity.failed-servers"), () -> {
            });
        }
    }

    void extractJoinTables() throws NoSuchFieldException, IllegalAccessException {
        /*local = extractJoinTable("local");
        remote = extractJoinTable("remote");
        global = extractJoinTable("global");
        hosts = extractJoinTable("hosts");*/
        cont = extractContTable();
    }

    void changeJoinMenu() {
        /*processChildren(global, "");
        processChildren(local, "");
        processChildren(remote, "");*/
        addIdentityButton();
    }

    void addIdentityButton() {
        Log.info(cont);
        if (!(cont.getChildren().get(0) instanceof Table nameTable)) return;
        nameTable.button(Settings.getText("identity.change-server-settings"), () -> {
            identityServerDialog.show(true);
        }).padLeft(10.0F).left().growX().height(35F);
    }

    /*void processChildren(Group thing, String off) {
        for (Element el : thing.getChildren()) {
            boolean isServer = false;
            if (el instanceof Button button) {
                isServer = processPossibleServer(button, off);
            }
            if (!isServer) {
                Log.info(off + "Child: " + el.getClass().getSimpleName());
                if (el instanceof Group groupEl) {
                    processChildren(groupEl, off + "   ");
                }
            }
            if (el instanceof Group groupEl) hookChildren(groupEl);
        }
    }

    boolean processPossibleServer(Button button, String off) {
        // mindustry.ui.dialogs.JoinDialog
        // void buildServer(Host host, Table content)
        // Adds 2 tables - header and content
        // We want to inject another one before them

        Log.info(off + "Server:");
        processChildren(button, off + "   ");

        if (button.getChildren().size != 2) return false;
        Element el = button.getChildren().get(0);
        Element el2 = button.getChildren().get(1);
        if (!(el instanceof Table header)) return false;
        if (!(el2 instanceof Table content)) return false;

        button.clearChildren();
        button.clear();

        button.table(Tex.whiteui, (t) -> {
            t.left();
            t.setColor(Pal.gray);
            t.button(Settings.getText("identity.change-server-settings"), () -> {
                identityServerDialog.show(true);
            }).padLeft(10.0F).left().growX().height(35F);
        }).growX().height(36.0F).row();
        button.add(header).growX().height(36.0F).row();
        button.add(content).growX().left().bottom();

        return true;
    }

    Table extractJoinTable(String name) throws NoSuchFieldException, IllegalAccessException {
        Field tableField = JoinDialog.class.getDeclaredField(name);
        tableField.setAccessible(true);
        return (Table) tableField.get(Vars.ui.join);
    }

    void hookChildren(Group group) {
        try {
            Field childrenField = Group.class.getDeclaredField("children");
            childrenField.setAccessible(true);

            SnapshotSeqProxy<Element> proxy = new SnapshotSeqProxy<>(true, 4, Element.class);
            Object childrenObj = childrenField.get(group);
            if (!(childrenObj instanceof SnapshotSeq<?> children)) return;
            for (Object el : children) proxy.addNoVerbose((Element) el);

            childrenField.set(group, proxy);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            Log.err("Mindustry toolkit - Identity - void hookChildren(Group group)", exception);
        }
    }*/

    Table extractContTable() throws NoSuchFieldException, IllegalAccessException {
        Field tableField = Dialog.class.getDeclaredField("cont");
        tableField.setAccessible(true);
        return (Table) tableField.get(Vars.ui.join);
    }

    public String getCurrentServerIp() {
        // TODO Extract it and be able to provide it when a server button is clicked
        throw new NullPointerException();
    }
}
