package MindustryToolkit.autofill;

import arc.Events;
import arc.util.Time;
import mindustry.game.EventType;

public class InteractTimer {
    public float timer = 0;

    public InteractTimer() {

    }

    public void increase() {
        timer = Time.time + Time.toSeconds * (250F / 1000F);
        timer += 0.01;// To prevent overflow
    }

    public boolean canInteract() {
        return Time.time >= timer;
    }
}
