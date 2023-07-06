package MindustryToolkit.sectorized;

import MindustryToolkit.settings.SectorizedSettings;
import arc.Core;
import arc.Events;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.ctype.Content;
import mindustry.game.EventType;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.world.Block;
import mindustry.world.blocks.storage.StorageBlock;

import java.util.HashMap;
import java.util.Objects;


public class Sectorized {
    public static final Seq<SectorizedServer> sectorizedServers = Seq.with(new SectorizedServer("89.58.37.204", 6567), new SectorizedServer("sectorized.freeddns.org", 6567));
    public static final HashMap<StorageBlock, ItemStack[]> defaultVaultPrices = new HashMap<>();
    public static final HashMap<Planet, StorageBlock> planetToVault = new HashMap<>();
    public State state = State.Wait;
    public boolean appliedChanges = false;
    @Nullable
    private Planet currentPlanet;
    private int corePlacementCooldown = 10;
    private boolean corePlacementLocked = false;

    public void init() {
        defaultVaultPrices.put((StorageBlock) Blocks.vault, ItemStack.with(Items.titanium, 250, Items.thorium, 125));
        defaultVaultPrices.put((StorageBlock) Blocks.reinforcedVault, ItemStack.with(Items.tungsten, 125, Items.thorium, 70, Items.beryllium, 100));
        planetToVault.put(Planets.serpulo, (StorageBlock) Blocks.vault);
        planetToVault.put(Planets.erekir, (StorageBlock) Blocks.reinforcedVault);

        SectorizedConstants.init();
        SectorizedSettings.init();
        SectorizedCoreCost.init();
        Events.on(EventType.CoreChangeEvent.class, this::onCoreChange);
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
            Content cont = SectorizedCoreCost.getContent(text.charAt(0));
            if (!(cont instanceof Item item))
                break;
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
        /*SectorizedCoreCost.getUnicode(Items.coal);
        SectorizedCoreCost.getUnicode(Blocks.coreBastion);
        SectorizedCoreCost.getUnicode(Planets.serpulo);*/
        //Log.info("[cyan]Checked info popup: []" + text);
        StorageBlock vault = getCurrentVault();
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

    private void onCoreChange(EventType.CoreChangeEvent event) {
        // Log.info("Placed: " + event.core + " - " + (event.core.dead() ? "dead" : "alive"));
        if (event.core.dead() || event.core.team() != Vars.player.team()) return;
        this.onCorePlaced();
    }

    private void onCorePlaced() {
        setCorePlacementLocked(true);
        int seconds = Vars.player.team().cores().size <= 2 ? 30 : getCorePlacementCooldown();
        Timer.schedule(() -> setCorePlacementLocked(false), seconds);
    }

    private void setCorePlacementLocked(boolean locked) {
        if (corePlacementLocked == locked) return;
        corePlacementLocked = locked;
        Block vault = getCurrentVault();
        if (vault == null) return;
        Log.info((locked ? "Lock" : "Unlock") + " " + vault);
        if (locked && !Vars.state.rules.bannedBlocks.contains(vault)) Vars.state.rules.bannedBlocks.add(vault);
        else if (!locked && Vars.state.rules.bannedBlocks.contains(vault)) Vars.state.rules.bannedBlocks.remove(vault);
    }

    private void onServerConnect(EventType.ClientServerConnectEvent event) {
        if (!SectorizedSettings.enabled) return;
        Log.info("Connected to: " + event.ip + ":" + event.port);
        for (SectorizedServer server : Sectorized.sectorizedServers) {
            if (Objects.equals(server.ip, event.ip) && server.port == event.port) {
                state = State.ConnectingToSectorized;
                return;
            }
        }
        this.resetChanges();
        state = State.ConnectingToOther;
    }

    private void onWorldLoadBegin(EventType.WorldLoadBeginEvent event) {
        if (!SectorizedSettings.enabled) return;
        if (state == State.Wait) {
            this.resetChanges();
            return;
        }
        if (state == State.ConnectingToSectorized) {
            this.applyChanges();
            return;
        }
        if (state == State.ConnectingToOther) this.resetChanges();
    }

    /**
     * Do changes reset by resetChanges when joining different server than Sectorized
     */
    private void applyChanges() {
        state = State.Wait;
        if (appliedChanges) return;
        Log.info("[cyan]Apply changes!");
        // APPLY begin
        setCorePlacementLocked(false);
        // APPLY end
        appliedChanges = true;
    }

    /**
     * Reset changes made by applyChanges when joining Sectorized
     */
    private void resetChanges() {
        state = State.Wait;
        if (!appliedChanges) return;
        Log.info("[cyan]Reset changes!");
        // RESET begin
        setCorePlacementLocked(false);
        Sectorized.defaultVaultPrices.forEach((block, requirements) -> block.requirements = requirements);
        // RESET end
        appliedChanges = false;
    }

    @Nullable
    private StorageBlock getCurrentVault() {
        return planetToVault.get(this.getCurrentPlanet());
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

    private int getCorePlacementCooldown() {
        return corePlacementCooldown = getCurrentPlanet() == Planets.serpulo ? 10 : 15;
    }

    private enum State {
        Wait, ConnectingToSectorized, ConnectingToOther
    }
}
