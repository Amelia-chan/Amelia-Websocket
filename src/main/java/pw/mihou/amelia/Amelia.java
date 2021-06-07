package pw.mihou.amelia;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pw.mihou.amelia.connections.AmeliaServer;
import pw.mihou.amelia.misc.ColorPalette;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class Amelia {

    public static final SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss");
    private static final String version = "1.0.0";
    private static final String build = "BETA";
    public static Logger log = (Logger) LoggerFactory.getLogger("Amelia Websocket");

    static {
        log.setLevel(Level.DEBUG);
    }

    public static void main(String[] args) {
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
        ((Logger) LoggerFactory.getLogger("io.javalin.Javalin")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.eclipse.jetty.util.log")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.eclipse.jetty")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("o.e.jetty.io")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("o.e.j.w.common")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(Level.ERROR);

        System.out.println(banner().replaceAll("b", ColorPalette.ANSI_BLUE).replaceAll("re", ColorPalette.ANSI_RESET));
        System.out.printf("Version: %s, Creator: Shindou Mihou, Build: %s\n", version, build);
        AmeliaServer.execute();
    }

    private static String banner() {
        return "b         _                  _   _         _            _              _          _          \n" +
                "b        / /\\               /\\_\\/\\_\\ _    /\\ \\         _\\ \\           /\\ \\       / /\\        \n" +
                "b       / /  \\             / / / / //\\_\\ /  \\ \\       /\\__ \\          \\ \\ \\     / /  \\       \n" +
                "b      / / /\\ \\           /\\ \\/ \\ \\/ / // /\\ \\ \\     / /_ \\_\\         /\\ \\_\\   / / /\\ \\      \n" +
                "b     / / /\\ \\ \\         /  \\____\\__/ // / /\\ \\_\\   / / /\\/_/        / /\\/_/  / / /\\ \\ \\     \n" +
                "b    / / /  \\ \\ \\       / /\\/________// /_/_ \\/_/  / / /            / / /    / / /  \\ \\ \\    \n" +
                "b   / / /___/ /\\ \\     / / /\\/_// / // /____/\\    / / /            / / /    / / /___/ /\\ \\   \n" +
                "b  / / /_____/ /\\ \\   / / /    / / // /\\____\\/   / / / ____       / / /    / / /_____/ /\\ \\  \n" +
                "b / /_________/\\ \\ \\ / / /    / / // / /______  / /_/_/ ___/\\ ___/ / /__  / /_________/\\ \\ \\ \n" +
                "b/ / /_       __\\ \\_\\\\/_/    / / // / /_______\\/_______/\\__\\//\\__\\/_/___\\/ / /_       __\\ \\_\\\n" +
                "b\\_\\___\\     /____/_/        \\/_/ \\/__________/\\_______\\/    \\/_________/\\_\\___\\     /____/_/re";
    }

    public static int determineNextTarget() {
        return LocalDateTime.now().getMinute() % 10 != 0 ? (LocalDateTime.now().getMinute() + (10 - LocalDateTime.now().getMinute() % 10)) - LocalDateTime.now().getMinute() : 0;
    }

}
