package de.chefexperte.grandtheftminecraft.guns;

import java.util.ArrayList;

public class RecoilPatterns {

    public static RecoilPattern AK47 = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(1f, 0.0f));
        add(new RecoilStep(1f, 0.4f));
        add(new RecoilStep(0.5f, -0.8f));
        add(new RecoilStep(0, -1.2f));
        add(new RecoilStep(0.5f, 0.4f));
    }});

    public static RecoilPattern DESERT_EAGLE = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(0.4f, 0.02f));
        add(new RecoilStep(0.2f, -0.05f));
        add(new RecoilStep(0.1f, 0.1f));
        add(new RecoilStep(0.3f, -1.5f));
        add(new RecoilStep(0.1f, 0.1f));
    }});

    public static RecoilPattern ROCKET_LAUNCHER = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(2.5f, 0));
    }});


    public static class RecoilPattern {
        public ArrayList<RecoilStep> steps;

        public RecoilPattern(ArrayList<RecoilStep> steps) {
            this.steps = steps;
        }
    }

    public record RecoilStep(float pitch, float yaw) {
    }

}
