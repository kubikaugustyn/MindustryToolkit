package MindustryToolkit.autofill;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Func;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.gen.Building;

import java.util.Iterator;

public class FillableBlockCategory {
    private FillableBlock[] blocks;
    private String name;

    FillableBlockCategory(String name) {
        this.name(name);
    }

    FillableBlockCategory(String name, FillableBlock[] blocks) {
        this.name(name);
        this.blocks(blocks);
    }

    FillableBlockCategory(String name, Seq<Building> buildings, Boolf<Building> check, Func<Building, FillableBlock> toFillableBlock) {
        this.name(name);
        Seq<FillableBlock> filtered = new Seq<>();
        int debugNullBuildingsCount = 0;
        for (Building building : buildings) {
            if (building == null) {
                debugNullBuildingsCount++;
                continue;
            }
            if (!check.get(building)) continue;
            FillableBlock block = toFillableBlock.get(building);
            if (block == null) {
                // Log.info("We got null block.");
                continue;
            }
            filtered.add(block);
        }
        if (debugNullBuildingsCount > 0)
            Log.info("Something fucked up, we got " + debugNullBuildingsCount + " null buildings");
        this.blocks(filtered);
    }

    public FillableBlockCategory blocks(FillableBlock[] blocks) {
        this.blocks = blocks;
        return this;
    }

    public FillableBlockCategory blocks(Seq<FillableBlock> blocks) {
        FillableBlock[] fillableBlocks = new FillableBlock[blocks.size];
        int i = 0;
        for (FillableBlock bl : blocks) fillableBlocks[i++] = bl;
        this.blocks(fillableBlocks);
        return this;
    }

    public FillableBlockCategory name(String name) {
        this.name = name;
        return this;
    }

    public FillableBlock[] blocks() {
        return this.blocks;
    }

    public String name() {
        return this.name;
    }
}
