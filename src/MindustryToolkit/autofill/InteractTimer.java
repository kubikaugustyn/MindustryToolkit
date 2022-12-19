package MindustryToolkit.autofill;

import arc.util.Time;

public class InteractTimer {
    public float timer = 0;

    public InteractTimer() {

    }

    public void update() {
        timer = Time.time + Time.toSeconds * (250F / 1000F);
        timer += 0.01;// To prevent overflow
    }

    public boolean canInteract() {
        return Time.time >= timer;
    }
}
