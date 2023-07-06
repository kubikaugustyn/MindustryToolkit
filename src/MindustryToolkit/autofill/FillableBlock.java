package MindustryToolkit.autofill;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class FillableBlock {
    private Block block;
    private Building building;
    private Item[] itemsIn; // Sorted (needed for turrets), first item is the best ammo etc.

    FillableBlock() {

    }

    FillableBlock(Block block, Item[] itemsIn) {
        this.block(block);
        this.itemsIn(itemsIn);
    }

    FillableBlock(Block block, ItemStack[] itemsIn) {
        this.block(block);
        this.itemsIn(itemsIn);
    }

    public FillableBlock block(Block block) {
        this.block = block;
        return this;
    }

    public FillableBlock building(Building building) {
        this.building = building;
        return this;
    }

    public FillableBlock itemsIn(Item[] itemsIn) {
        this.itemsIn = itemsIn;
        return this;
    }

    public FillableBlock itemsIn(ItemStack[] itemsIn) {
        Item[] items = new Item[itemsIn.length];
        int i = 0;
        for (ItemStack stack : itemsIn) items[i++] = stack.item;
        this.itemsIn(items);
        return this;
    }

    public Block block() {
        return this.block;
    }

    public Building building() {
        return this.building;
    }

    public Item[] itemsIn() {
        return this.itemsIn;
    }
}
