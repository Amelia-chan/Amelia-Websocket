package pw.mihou.amelia.wrappers;

import tk.mihou.amatsuki.api.Amatsuki;
import tk.mihou.amatsuki.entities.story.Story;

import java.util.concurrent.TimeUnit;

public class AmatsukiWrapper {

    private static final Amatsuki connector = new Amatsuki()
            .setLifespan(24, TimeUnit.HOURS)
            .setUserAgent("Amatsuki-library/1.0.7 (Language=Java/1.8)")
            .setCache(true);

    public static Amatsuki getConnector() {
        return connector;
    }

}
