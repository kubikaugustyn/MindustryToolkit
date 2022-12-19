package MindustryToolkit.autofill;

import MindustryToolkit.settings.AutoFillSettings;
import arc.Events;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.type.Item;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AutoFill {
    private final InteractTimer interactTimer = new InteractTimer();
    private final AutoFillSettings settings = new AutoFillSettings();
    private static final String[] ignoredBlocks = {// To prevent random conveyor filling etc.
            // Storage
            "core-shard",
            "core-foundation",
            "core-nucleus",
            "core-bastion",
            "core-citadel",
            "core-acropolis",
            "container",
            "reinforced-container",
            "vault",
            "reinforced-vault",
            // Conveyor
            "conveyor",
            "titanium-conveyor",
            "plastanium-conveyor",
            "armored-conveyor",
            "junction", // I don't care that junction doesn't accept items :-) Pointifix
            "bridge-conveyor",
            "phase-conveyor",
            "sorter", // Same
            "inverted-sorter",
            "router",
            "distributor",
            "overflow-gate", // Shut up!
            "underflow-gate",
            "mass-driver",
            // Now ducts aaaaaa
            "duct",
            "armored-duct",
            "duct-router",
            "overflow-duct",
            "underflow-duct",
            "duct-bridge",
            "duct-unloader",
            "surge-conveyor",
            "surge-router",
            "unit-cargo-loader",
            // Now some else random stuff
            "force-projector", // Pls don't eat phase fabric
            "mender",
            "mend-projector",
            "incinerator", // No item waste
            "slag-incinerator"
            // Contact me to add more blocks - Discord: HackerKuba2009#9722
    };

    public void init() {
        settings.init();
        Events.run(EventType.Trigger.update, this::onUpdate);
    }

    private void onUpdate() {
        if (!interactTimer.canInteract()) return;
        Player player = Vars.player;
        ItemStack stack = player.unit().stack;
        Team team = player.team();
        CoreBlock.CoreBuild core = player.closestCore();
        boolean isCoreAvailable = core != null;

        AtomicBoolean transfered = new AtomicBoolean(false);
        AtomicReference<Item> request = new AtomicReference<>(null);
        // Search Blocks
        Vars.indexer.eachBlock(team, player.x, player.y, Vars.buildingRange, (Building building) -> !isBlockIgnored(building.block()), b -> {
            if (!interactTimer.canInteract()) return;

            // Blocks Declaration
            Block block = b.tile.block();
            String blockname = block.getDisplayName(b.tile);
            // String blockId = getBlockId(block);

            //Check If block eats items
            //if (!block.consumesItem)// In v6
            if (!block.hasConsumers) {// In v7, Kuba's sketchy way
                // Vars.player.sendMessage("Can't Eat : " + blockname);
                return;
            }
            // Filtering Core and Container extender and some useless Buildings
            // No longer needed, it's in the pred function in Vars.indexer.eachBlock call
            // if (isBlockIgnored(block)) return;

            // Transfer item?
            // Vars.player.sendMessage("Detected " + blockname);
            if (b.acceptStack(stack.item, stack.amount, player.unit()) >= 1) {
                Call.transferInventory(player, b);
                Vars.player.sendMessage("Transferred To " + blockname);
                interactTimer.update();
                transfered.set(true);
            }

            if (!isCoreAvailable || request.get() != null) return;

            if (block instanceof ItemTurret) { // Fill item turrets
                // if (!b.ammo.isEmpty()) return;// v6
                if (!((ItemTurret) block).ammoTypes.isEmpty()) return;

                request.set(getBestAmmo((ItemTurret) block, core));
            } else if (block instanceof UnitFactory) { // Fill unit factory
                request.set(getUnitFactoryRequest((UnitFactory.UnitFactoryBuild) b, (UnitFactory) block, core));
            } else if (block instanceof Reconstructor) { // Fill unit reconstructor
                request.set(getReconstructorRequest((Reconstructor.ReconstructorBuild) b, (Reconstructor) block, core));
            } else if (block instanceof GenericCrafter) {
                request.set(findRequiredItem(getItemStacks(getItemConsumers(block)), b, core));
            } /*else if (b.items) {
                request.set(getItemRequest(b, block, core));
            }*/
        });
        if (!isCoreAvailable || transfered.get() || request.get() == null || !player.within(core, Vars.buildingRange)) {
            return;
        }

        if (stack.amount > 0) {
            Call.transferInventory(player, core);
            if (stack.amount > 0) { // Throw out items, that core doesn't accept
                Call.dropItem(0F);
            }
        } else {
            Call.requestItem(player, core, request.get(), 999);
        }
        interactTimer.update();
    }

    private String getBlockId(Block block) {
        return block.name;
    }

    private boolean isBlockIgnored(Block block) {
        String blockId = getBlockId(block);
        for (String id : ignoredBlocks) {
            if (Objects.equals(blockId, id)) { // Not my idea, just Intellij IDEA
                // Vars.player.sendMessage("Core Detected : " + blockname);
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getItemStacks(ConsumeItems[] consumedItems) {
        int len = 0;
        for (ConsumeItems items : consumedItems) len += items.items.length;
        int i = 0;
        ItemStack[] itemStacks = new ItemStack[len];
        for (ConsumeItems items : consumedItems) {
            for (ItemStack itemStack : items.items) {
                itemStacks[i++] = itemStack;
            }
        }
        return itemStacks;
    }

    public ConsumeItems[] getItemConsumers(Block block) {
        return Arrays.stream(block.consumers).filter(c -> c instanceof ConsumeItems).toArray(ConsumeItems[]::new);
    }

    public Item getBestAmmo(ItemTurret turret, CoreBlock.CoreBuild core) {
        AtomicReference<Item> best = null;
        AtomicReference<Float> bestDamage = new AtomicReference<>((float) 0);
        turret.ammoTypes.each((Item item, BulletType ammo) -> {
            float totalDamage = ammo.damage + ammo.splashDamage;
            if (!ammo.makeFire && totalDamage > bestDamage.get() && core.items.get(item) >= 20) {
                best.set(item);
                bestDamage.set(totalDamage);
            }
        });
        return best.get();
    }

    public Item getUnitFactoryRequest(UnitFactory.UnitFactoryBuild build, UnitFactory block, CoreBlock.CoreBuild core) {
        if (build.currentPlan == -1) return null;

        ItemStack[] stacks = block.plans.get(build.currentPlan).requirements;

        return findRequiredItem(stacks, build, core);
    }

    public Item getReconstructorRequest(Reconstructor.ReconstructorBuild build, Reconstructor block, CoreBlock.CoreBuild core) {
        ItemStack[] stacks = block.requirements;

        return findRequiredItem(stacks, build, core);
    }

    // getItemRequest

    // getFilterRequest

    public Item findRequiredItem(ItemStack[] stacks, Building build, CoreBlock.CoreBuild core) {
        for (ItemStack itemStack : stacks) {
            Item item = itemStack.item;
            if (core.items.get(item) >= 20 && build.acceptStack(item, 20, Vars.player.unit()) >= 1) {
                return item;
            }
        }
        return null;
    }
}
