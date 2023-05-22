package MindustryToolkit.settings;

import MindustryToolkit.autofill.AutoFill;
import arc.struct.ObjectMap;
import mindustry.content.Blocks;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class AutoFillSettingsDefault implements FeatureSettingsDefault {
    public static boolean enabled = false; // AutoFill enabled, false until I get it working
    public static boolean allowHomingAmmo = false; // (Homing) Ammo like silicon in duo can be used
    public static boolean allowFireAmmo = false; // (Burning)  Ammo like coal in scorch can be used
    public static int minTurretCoreItems = 25;
    public static ObjectMap<ItemTurret, Item[]> turretAmmo = new ObjectMap<>();

    public static void init() {
        // Why does it crash?
        /*
        duo = new ItemTurret("duo")
        scatter = new ItemTurret("scatter")
        scorch = new ItemTurret("scorch")
        hail = new ItemTurret("hail")
        swarmer = new ItemTurret("swarmer")
        salvo = new ItemTurret("salvo")
        fuse = new ItemTurret("fuse")
        ripple = new ItemTurret("ripple")
        cyclone = new ItemTurret("cyclone")
        foreshadow = new ItemTurret("foreshadow")
        spectre = new ItemTurret("spectre")
        breach = new ItemTurret("breach")
        diffuse = new ItemTurret("diffuse")
        titan = new ItemTurret("titan")
        disperse = new ItemTurret("disperse")
        scathe = new ItemTurret("scathe")
        smite = new ItemTurret("smite")
        */
        turretAmmo.put((ItemTurret) Blocks.duo, AutoFill.getBestAmmoList((ItemTurret) Blocks.duo));
        turretAmmo.put((ItemTurret) Blocks.scatter, AutoFill.getBestAmmoList((ItemTurret) Blocks.scatter));
        turretAmmo.put((ItemTurret) Blocks.scorch, AutoFill.getBestAmmoList((ItemTurret) Blocks.scorch));
        turretAmmo.put((ItemTurret) Blocks.hail, AutoFill.getBestAmmoList((ItemTurret) Blocks.hail));
        turretAmmo.put((ItemTurret) Blocks.swarmer, AutoFill.getBestAmmoList((ItemTurret) Blocks.swarmer));
        turretAmmo.put((ItemTurret) Blocks.salvo, AutoFill.getBestAmmoList((ItemTurret) Blocks.salvo));
        turretAmmo.put((ItemTurret) Blocks.fuse, AutoFill.getBestAmmoList((ItemTurret) Blocks.fuse));
        turretAmmo.put((ItemTurret) Blocks.ripple, AutoFill.getBestAmmoList((ItemTurret) Blocks.ripple));
        turretAmmo.put((ItemTurret) Blocks.cyclone, AutoFill.getBestAmmoList((ItemTurret) Blocks.cyclone));
        turretAmmo.put((ItemTurret) Blocks.foreshadow, AutoFill.getBestAmmoList((ItemTurret) Blocks.foreshadow));
        turretAmmo.put((ItemTurret) Blocks.spectre, AutoFill.getBestAmmoList((ItemTurret) Blocks.spectre));
        turretAmmo.put((ItemTurret) Blocks.breach, AutoFill.getBestAmmoList((ItemTurret) Blocks.breach));
        turretAmmo.put((ItemTurret) Blocks.diffuse, AutoFill.getBestAmmoList((ItemTurret) Blocks.diffuse));
        turretAmmo.put((ItemTurret) Blocks.titan, AutoFill.getBestAmmoList((ItemTurret) Blocks.titan));
        turretAmmo.put((ItemTurret) Blocks.disperse, AutoFill.getBestAmmoList((ItemTurret) Blocks.disperse));
        turretAmmo.put((ItemTurret) Blocks.scathe, AutoFill.getBestAmmoList((ItemTurret) Blocks.scathe));
        turretAmmo.put((ItemTurret) Blocks.smite, AutoFill.getBestAmmoList((ItemTurret) Blocks.smite));
    }
}
