package pw.mihou.amelia.connections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.websocket.WsContext;
import org.json.JSONObject;
import pw.mihou.amelia.Amelia;
import pw.mihou.amelia.activities.Feeds;
import pw.mihou.amelia.activities.Trending;
import pw.mihou.amelia.io.ResetCalculator;
import pw.mihou.amelia.io.Scheduler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AmeliaServer {

    public static final Map<String, WsContext> connections = new ConcurrentHashMap<>();
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
    private static final int port = 3201;
    private static final String authorization = System.getenv("amelia_auth");

    private static void startHeartbeat() {
        Scheduler.schedule(() -> connections.values()
                .stream()
                .filter(wsContext -> wsContext.session.isOpen())
                .forEach(wsContext -> wsContext.send(new JSONObject()
                        .put("session", wsContext.getSessionId()).toString())), 20, 20, TimeUnit.SECONDS);
        Amelia.log.debug("Module [Heartbeat] is now running.");
    }

    private static void startFeeds() {
        Scheduler.schedule(Feeds::run, Amelia.determineNextTarget(), 10, TimeUnit.MINUTES);
        Amelia.log.debug("Module [RSS Feed] is delayed by {} minutes.", Amelia.determineNextTarget());
    }

    public static void sendPayload(Object payload, String type) {
        connections.values().stream().filter(wsContext -> wsContext.session.isOpen()).forEach(c -> c.send(new JSONObject()
                .put("payload", gson.toJson(payload)).put("payload_type", type).toString()));
    }

    private static void startTrending() {
        Scheduler.schedule(() -> CompletableFuture.runAsync(Trending::run), ResetCalculator.nextTrending(), ResetCalculator.defaultReset(), TimeUnit.SECONDS);
        Amelia.log.info("Module [Trending] is now delayed by {}.", secondsToDate());
    }

    private static String secondsToDate() {
        long uptime = ResetCalculator.nextTrending();
        return String.format("%d days, %d hours, %d minutes, %d seconds",
                TimeUnit.SECONDS.toDays(uptime),
                TimeUnit.SECONDS.toHours(uptime) - TimeUnit.DAYS.toHours(TimeUnit.SECONDS.toDays(uptime)),
                TimeUnit.SECONDS.toMinutes(uptime) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(uptime)),
                TimeUnit.SECONDS.toSeconds(uptime) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(uptime))
        );
    }

    public static void execute() {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.autogenerateEtags = true;
            config.ignoreTrailingSlashes = true;
            config.defaultContentType = "application/json";
            config.compressionStrategy(CompressionStrategy.GZIP);
            config.wsLogger(wsHandler -> {
                wsHandler.onConnect(wsConnectContext -> Amelia.log.debug("Received connection from {}", wsConnectContext.session.getRemoteAddress().toString()));
                wsHandler.onClose(wsCloseContext -> Amelia.log.debug("Received closed connection from {} for {}", wsCloseContext.session.getRemoteAddress().toString(), wsCloseContext.reason()));
            });
        });

        app.events(event -> {
            event.serverStarting(() -> Amelia.log.debug("The server is now starting at port: {}", port));
            event.serverStarted(() -> Amelia.log.debug("The server has successfully booted up."));
            event.serverStartFailed(() -> Amelia.log.error("The server failed to start, possibly another instance at the same port is running."));
            event.serverStopping(() -> Amelia.log.debug("The server is now shutting off."));
            event.serverStopped(() -> Amelia.log.debug("The server has successfully closed."));
        }).start(port);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        Amelia.log.debug("All events and handlers are now ready.");

        app.ws("/", ws -> {
            ws.onConnect(ctx -> {
                if (!Objects.isNull(ctx.header("Authorization")) && ctx.header("Authorization").equals(authorization)) {
                    connections.put(ctx.getSessionId(), ctx);
                    ctx.send("The handshake was accepted.");
                } else {
                    ctx.send("Your request to connect was denied: missing/invalid authorization header.");
                    ctx.session.close();
                }
            });
            ws.onClose(ctx -> connections.remove(ctx.getSessionId()));
        });

        startHeartbeat();
        startFeeds();
        startTrending();
    }

}
