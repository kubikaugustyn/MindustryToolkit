package MindustryToolkit.sectorized;

import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.SectorizedSettings;
import arc.Core;
import arc.Events;
import arc.scene.Action;
import arc.scene.Element;
import arc.scene.actions.DelayAction;
import arc.scene.actions.RemoveActorAction;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.ui.Menus;
import mindustry.ui.dialogs.JoinDialog;
import mindustry.ui.fragments.PlacementFragment;
import mindustry.world.Block;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;


public class Sectorized {
    public static final Seq<SectorizedServer> sectorizedServers = Seq.with(new SectorizedServer("89.58.37.204", 6567), new SectorizedServer("sectorized.freeddns.org", 6567));
    public static final HashMap<StorageBlock, ItemStack[]> defaultVaultPrices = new HashMap<>();
    public static final HashMap<Planet, StorageBlock> planetToVault = new HashMap<>();
    public static final byte StateWait = 0;
    public static final byte StateConnectingToSectorized = 1;
    public static final byte StateConnectingToOther = 2;
    public byte state = Sectorized.StateWait;
    public boolean appliedChanges = false;
    @Nullable
    private Planet currentPlanet;

    public void init() {
        defaultVaultPrices.put((StorageBlock) Blocks.vault, ItemStack.with(Items.titanium, 250, Items.thorium, 125));
        defaultVaultPrices.put((StorageBlock) Blocks.reinforcedVault, ItemStack.with(Items.tungsten, 125, Items.thorium, 70, Items.beryllium, 100));
        planetToVault.put(Planets.serpulo, (StorageBlock) Blocks.vault);
        planetToVault.put(Planets.erekir, (StorageBlock) Blocks.reinforcedVault);

        SectorizedConstants.init();
        SectorizedSettings.init();
        Events.on(EventType.ClientServerConnectEvent.class, this::onServerConnect);
        Events.on(EventType.WorldLoadBeginEvent.class, this::onWorldLoadBegin);
        Events.run(EventType.Trigger.update, this::onUpdate);
        //Events.on(EventType.ClientServerConnectEvent.class, ev -> Log.info("ClientServerConnectEvent"));
        //Events.on(EventType.WorldLoadBeginEvent.class, ev -> Log.info("WorldLoadBeginEvent"));
        /*Events.on(EventType.WorldLoadEndEvent.class, ev -> Log.info("WorldLoadEndEvent"));
        Events.on(EventType.WorldLoadEvent.class, ev -> Log.info("WorldLoadEvent"));*/

        // Vars.ui.hudfrag.setHudText("Lmao");
        // Vars.ui.hudfrag.showToast("LMAO");
        // Menus.infoPopup("XD", 5.1f, 0, 50, 50, 0, 0);


        /*Vars.content.blocks().each(block -> block instanceof LogicDisplay, block -> block.buildType = () -> ((LogicDisplay) block).new LogicDisplayBuild() {
            @Override
            public void draw() {
                super.draw();
                Draw.draw(Draw.z(), () -> {
                    Draw.rect(Draw.wrap(buffer.getTexture()), x, y, block.region.width * Draw.scl, -block.region.height * Draw.scl);
                });
            }
        });*/
    }

    private void onUpdate() {
        // Vars.ui.showInfoPopup("XD", 5000.1, Align.topLeft, 90, 5, 0, 0);
        /*Core.scene.root.getChildren().each(element -> (element instanceof Table
                && element.touchable == Touchable.disabled
                && element.hasActions()), elem -> Log.info(elem.getActions().get(0).getActor()));*/

        if (!appliedChanges) return;
        if (Vars.state.isPaused() || Vars.state.isMenu() || !SectorizedSettings.enabled) return;
        /*ItemSeq requirements = SectorizedCoreCost.getRequirements(Vars.player.team());
        if (this.getCurrentPlanet() != null) Log.info("Planet: " + this.getCurrentPlanet().localizedName);
        StorageBlock vault = planetToVault.get(this.getCurrentPlanet());
        if (vault != null) vault.requirements = requirements.toArray();*/
        Core.scene.root.getChildren().each(element -> (element instanceof Table
                && element.touchable == Touchable.disabled
                && element.hasActions()
                && element.getActions().size == 1), this::checkInfoPopup);
    }

    private void checkInfoPopup(Element element) {
        //Log.info("[cyan]Checking info popup...");
        // Hopefully only runs with Table made by UI --> public void showInfoPopup(String info, float duration, int align, int top, int left, int bottom, int right)
        if (!(element instanceof Table rootTable)) return;
        if (!rootTable.hasChildren()) return;
        Element infoElem = rootTable.getChildren().get(0);
        if (!(infoElem instanceof Table infoTable)) return;
        Element textElem = infoTable.getChildren().get(0);
        if (!(textElem instanceof Label textLabel)) return;
        StringBuilder text = new StringBuilder(textLabel.getText());
        // Log.info("[cyan]Checked info popup: []" + text);
        if (!this.checkString(SectorizedMessageUtils.cInfo + "Costs for next[white] ", text))
            return;
        currentPlanet = text.charAt(0) == '\uF869' ? Planets.serpulo : Planets.erekir;
        text.delete(0, 2);
        ItemSeq requirements = new ItemSeq();
        ItemSeq available = new ItemSeq();
        while (text.length() > 0) {
            if (!SectorizedCoreCost.unicodeItems.containsKey(String.valueOf(text.charAt(0))))
                break;
            Item item = SectorizedCoreCost.unicodeItems.get(String.valueOf(text.charAt(0)));
            ItemStack itemStack = new ItemStack(item, 0);
            ItemStack itemStackAvailable = new ItemStack(item, 0);
            text.deleteCharAt(0);
            if (text.substring(0, SectorizedMessageUtils.cDanger.length()).equals(SectorizedMessageUtils.cDanger)) {
                text.delete(0, SectorizedMessageUtils.cDanger.length());
                int i = text.indexOf("[white]/");
                itemStackAvailable.amount = Integer.parseInt(text.substring(0, i));
                text.delete(0, i + "[white]/".length());
                i = text.indexOf("\n");
                itemStack.amount = Integer.parseInt(text.substring(0, i));
                text.delete(0, i + 1);
            } else {
                String postfix = SectorizedMessageUtils.cHighlight2 + "\uE800[white]\n";
                int i = text.indexOf(postfix);
                itemStack.amount = Integer.parseInt(text.substring(0, i));
                text.delete(0, i + postfix.length());
            }
            requirements.add(itemStack);
            available.add(itemStackAvailable);
        }
        //Log.info("[cyan]Checked info popup: []" + text);
        StorageBlock vault = planetToVault.get(this.getCurrentPlanet());
        if (vault != null) vault.requirements = requirements.toArray();
    }

    private boolean checkString(String str, StringBuilder inside) {
        return this.checkString(str, inside, true);
    }

    private boolean checkString(String str, StringBuilder inside, boolean delete) {
        if (inside.length() < str.length()) return false;
        if (!Objects.equals(inside.substring(0, str.length()), str)) return false;
        if (delete) inside.delete(0, str.length());
        return true;
    }

    private void onServerConnect(EventType.ClientServerConnectEvent event) {
        if (!SectorizedSettings.enabled) return;
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
        if (!SectorizedSettings.enabled) return;
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
        // APPLY begin
        // APPLY end
        appliedChanges = true;
    }

    /**
     * Reset changes made by applyChanges when joining Sectorized
     */
    private void resetChanges() {
        state = Sectorized.StateWait;
        if (!appliedChanges) return;
        Log.info("[cyan]Reset changes!");
        // RESET begin
        Sectorized.defaultVaultPrices.forEach((block, requirements) -> block.requirements = requirements);
        // RESET end
        appliedChanges = false;
    }

    @Nullable
    private Planet getCurrentPlanet() {
        /*try {
            Method method = PlacementFragment.class.getDeclaredMethod("unlocked", Block.class);
            method.setAccessible(true);
            boolean vaultVisible = (boolean) method.invoke(Vars.ui.hudfrag.blockfrag, Blocks.vault) && Blocks.vault.isVisible();
            boolean reinfVaultVisible = (boolean) method.invoke(Vars.ui.hudfrag.blockfrag, Blocks.reinforcedVault) && Blocks.reinforcedVault.isVisible();
            if (!vaultVisible && !reinfVaultVisible) return null;
            // if (vaultVisible && reinfVaultVisible) return null;
            return vaultVisible ? Planets.serpulo : Planets.erekir;
        } catch (Exception ignored) {
            return null;
        }*/
        return currentPlanet;
    }
}
