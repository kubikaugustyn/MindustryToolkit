package MindustryToolkit.autofill;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Func;
import arc.struct.Seq;
import mindustry.gen.Building;

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

    FillableBlockCategory(String name, Building[] buildings, Boolf<Building> check, Func<Building, FillableBlock> toFillableBlock) {
        this.name(name);
        Seq<FillableBlock> filtered = new Seq<>();
        for (Building building : buildings) {
            if (!check.get(building)) continue;
            filtered.add(toFillableBlock.get(building));
        }
        this.blocks(filtered.items);
    }

    public FillableBlockCategory blocks(FillableBlock[] blocks) {
        this.blocks = blocks;
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
