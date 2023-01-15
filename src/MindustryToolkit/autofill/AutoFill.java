package MindustryToolkit.autofill;

import MindustryToolkit.settings.AutoFillSettings;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.type.Item;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AutoFill {
    private final static double Infinity = 1.7E308;
    private final InteractTimer interactTimer = new InteractTimer();
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
        AutoFillSettings.init();
        Events.run(EventType.Trigger.update, this::onUpdate);
    }

    private void onUpdate() {
        if (Vars.state.isPaused() || Vars.state.isMenu()) return;
        if (Vars.player.unit().dead() || !Vars.player.unit().canBuild()) return;
        if (!AutoFillSettings.enabled || !interactTimer.canInteract() || Vars.state.isPaused()) return;
        Player player = Vars.player;
        ItemStack stack = player.unit().stack;
        Team team = player.team();
        CoreBlock.CoreBuild core = player.closestCore();
        boolean isCoreAvailable = core != null;

        AtomicBoolean transferred = new AtomicBoolean(false);
        AtomicReference<Item> request = new AtomicReference<>(null);
        // Search Blocks
        Seq<Building> buildingsInRange = new Seq<>(new Building[]{});
        Vars.indexer.eachBlock(team, player.x, player.y, Vars.buildingRange, (Building building) -> !isBlockIgnored(building.block()) && building.block.hasConsumers, buildingsInRange::add);
        if (buildingsInRange.any() && isCoreAvailable) {
            FillableBlockCategory[] categories = this.getBlocksToFill(buildingsInRange);
            for (FillableBlockCategory category : categories) {
                boolean isTurret = Objects.equals(category.name(), "turret");
                for (FillableBlock block : category.blocks()) {
                    if (isTurret) {
                        // if (!b.ammo.isEmpty()) return;// v6
                        if (((ItemTurret) block.block()).ammoTypes.isEmpty()) return;
                        if (block.building().items.any()) return;
                        // Log.info("[cyan]Fill turret!");
                        // Item bestAmmo = getBestAmmo((ItemTurret) block, core); Old way
                        Item[] bestAmmoList = AutoFillSettings.turretAmmo.get((ItemTurret) block.block());
                        Item bestAmmo = null;
                        for (Item ammo : bestAmmoList) {
                            if (core.items.get(ammo) >= AutoFillSettings.minTurretCoreItems) {
                                bestAmmo = ammo;
                                break;
                            }
                        }
                        if (bestAmmo == null) return;
                        Vars.player.sendMessage("Chose " + bestAmmo.localizedName + " to fill " + block.block().localizedName + " at " + block.building().tile().x + " " + block.building().tile().y + " with " + block.building().items.total() + " items inside out of " + block.building().getMaximumAccepted(null));
                        request.set(bestAmmo);
                    } else if (block.building() != null) {
                        request.set(findRequiredItem(block.itemsIn(), block.building(), core));
                    } else {
                        request.set(block.itemsIn()[0]);
                    }
                }
            }
        }

        String a = """
                    Vars.indexer.eachBlock(team, player.x, player.y, Vars.buildingRange, (Building building) -> !isBlockIgnored(building.block()) && building.block.hasConsumers, b -> {
                    if (!interactTimer.canInteract()) return;

                    // Blocks Declaration
                    Block block = b.tile.block();
                    String blockname = block.getDisplayName(b.tile);
                    // String blockId = getBlockId(block);

                    //Check If block eats items
                    //if (!block.consumesItem)// In v6
                    /*if (!block.hasConsumers) {// In v7, Kuba's sketchy way
                        No longer needed
                        // Vars.player.sendMessage("Can't Eat : " + blockname);
                        return;
                    }*/
                    // Filtering Core and Container extender and some useless Buildings
                    // No longer needed, it's in the pred function in Vars.indexer.eachBlock call
                    // if (isBlockIgnored(block)) return;

                    // Transfer item?
                    // Vars.player.sendMessage("Detected " + blockname);
                    if (b.acceptStack(stack.item, stack.amount, player.unit()) >= 1) {
                        Call.transferInventory(player, b);
                        Vars.player.sendMessage("Transferred to " + blockname);
                        interactTimer.update();
                        transferred.set(true);
                    }

                    if (!isCoreAvailable || request.get() != null) return;

                    if (block instanceof ItemTurret) { // Fill item turrets
                        // if (!b.ammo.isEmpty()) return;// v6
                        if (((ItemTurret) block).ammoTypes.isEmpty()) return;
                        if (b.items.any()) return;
                        // Log.info("[cyan]Fill turret!");
                        // Item bestAmmo = getBestAmmo((ItemTurret) block, core); Old way
                        Item[] bestAmmoList = AutoFillSettings.turretAmmo.get((ItemTurret) block);
                        Item bestAmmo = null;
                        for (Item ammo : bestAmmoList)
                            if (core.items.get(ammo) >= AutoFillSettings.minTurretCoreItems) {
                                bestAmmo = ammo;
                                break;
                            }
                        if (bestAmmo == null) return;
                        Vars.player.sendMessage("Chose " + bestAmmo.localizedName + " to fill " + block.localizedName + " at " + b.tile().x + " " + b.tile().y + " with " + b.items.total() + " items inside out of " + b.getMaximumAccepted(null));
                        request.set(bestAmmo);
                    } else if (block instanceof UnitFactory) { // Fill unit factory
                        request.set(getUnitFactoryRequest((UnitFactory.UnitFactoryBuild) b, (UnitFactory) block, core));
                    } else if (block instanceof Reconstructor) { // Fill unit reconstructor
                        request.set(getReconstructorRequest((Reconstructor.ReconstructorBuild) b, (Reconstructor) block, core));
                    } else if (block instanceof GenericCrafter) {
                        request.set(findRequiredItem(getItemStacks(getItemConsumers(block)), b, core));
                    }/*else if (b.items) {
                        request.set(getItemRequest(b, block, core));
                    }*/
                });""";
        if (!isCoreAvailable || transferred.get() || request.get() == null || !player.within(core, Vars.buildingRange)) {
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

    private FillableBlockCategory[] getBlocksToFill(Seq<Building> buildings) {
        return new FillableBlockCategory[]{
                new FillableBlockCategory("turret", buildings, b -> b.block() instanceof ItemTurret, building ->
                        new FillableBlock().block(building.block()).itemsIn(getBestAmmoList((ItemTurret) building.block())).building(building)
                ),
                new FillableBlockCategory("unit-factory", buildings, b -> b.block() instanceof UnitFactory && ((UnitFactory.UnitFactoryBuild) b).currentPlan > -1, building ->
                        new FillableBlock().block(building.block()).itemsIn(((UnitFactory) building.block()).plans.get(((UnitFactory.UnitFactoryBuild) building).currentPlan).requirements).building(building)
                ),
                new FillableBlockCategory("unit-reconstructor", buildings, b -> b.block() instanceof Reconstructor, building ->
                        new FillableBlock().block(building.block()).itemsIn(building.block().requirements).building(building)
                ),
                new FillableBlockCategory("crafter", buildings, b -> b.block() instanceof GenericCrafter, building ->
                        new FillableBlock().block(building.block()).itemsIn(getItemStacks(getItemConsumers(building.block()))).building(building)
                )
        };
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
            if ((!AutoFillSettings.allowHomingAmmo || ammo.homingPower <= 0f) && (!AutoFillSettings.allowFireAmmo || !ammo.makeFire) && totalDamage > bestDamage.get() && core.items.get(item) >= AutoFillSettings.minTurretCoreItems) {
                best.set(item);
                bestDamage.set(totalDamage);
            }
        });
        return best.get();
    }

    public static Item[] getBestAmmoList(ItemTurret turret) {
        // Log.info(turret == null ? "Has turret!" : "No turret...");
        ItemDamage[] itemDamages = new ItemDamage[turret.ammoTypes.size];
        int i = 0;
        for (Item item : turret.ammoTypes.keys()) {
            BulletType ammo = turret.ammoTypes.get(item);
            float totalDamage = ammo.damage + ammo.splashDamage;
            ItemDamage itemDamage = new ItemDamage(item, totalDamage, true);
            itemDamages[i] = itemDamage;
            Log.info("Best ammo list for " + turret.localizedName + " at " + i + " is " + (itemDamages[i] == null ? "null" : "defined: " + itemDamages[i].ammo.localizedName));
            if (!(!AutoFillSettings.allowHomingAmmo || ammo.homingPower <= 0f))
                itemDamage.allowed = false; // Allow homing ammo setting
            if (!(!AutoFillSettings.allowFireAmmo || !ammo.makeFire))
                itemDamage.allowed = false; // Allow fire ammo setting
            i++;
        }
        Arrays.sort(itemDamages);
        int finalLength = 0;
        for (ItemDamage dmg : itemDamages) if (dmg.allowed) finalLength++;
        Item[] ammoListSorted = new Item[finalLength];
        int index = 0;
        for (ItemDamage itemDamage : itemDamages) if (itemDamage.allowed) ammoListSorted[index++] = itemDamage.ammo;
        return ammoListSorted;
    }

    public Item getUnitFactoryRequest(UnitFactory.UnitFactoryBuild build, UnitFactory block, CoreBlock.CoreBuild
            core) {
        if (build.currentPlan == -1) return null;

        ItemStack[] stacks = block.plans.get(build.currentPlan).requirements;

        return findRequiredItem(stacks, build, core);
    }

    public Item getReconstructorRequest(Reconstructor.ReconstructorBuild build, Reconstructor
            block, CoreBlock.CoreBuild core) {
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

    public Item findRequiredItem(Item[] items, Building build, CoreBlock.CoreBuild core) {
        for (Item item : items) {
            if (item == null) {
                Log.info("Something fucked up, we have null item in findRequiredItem.");
                continue;
            }
            if (core.items.get(item) >= 20 && build.acceptStack(item, 999, Vars.player.unit()) >= 1) {
                return item;
            }
        }
        return null;
    }

    private static class ItemDamage implements Comparable<ItemDamage> {
        public Item ammo;
        public float damage;
        public boolean allowed;

        ItemDamage() {

        }

        ItemDamage(Item ammo, float damage) {
            this(ammo, damage, true);
        }

        ItemDamage(Item ammo, float damage, boolean allowed) {
            this.ammo = ammo;
            this.damage = damage;
            this.allowed = allowed;
        }

        @Override
        public int compareTo(ItemDamage itemDamage) {
            /*
            -1 this > other
             0 this = other
             1 this < other
            */
            // this.damage
            // itemDamage.damage
            // return Float.compare(, ); // Again Intellij IDEA
            if (itemDamage == null) return 0;
            if (!this.allowed) return 1; // Not allowed ammo will automatically be at the end
            if (this.damage > itemDamage.damage) return 1;
            else if (this.damage == itemDamage.damage) return 0;
            else return -1;
        }
    }
}
