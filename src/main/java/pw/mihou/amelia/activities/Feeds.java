package pw.mihou.amelia.activities;

import com.google.gson.Gson;
import pw.mihou.amelia.Amelia;
import pw.mihou.amelia.connections.AmeliaServer;
import pw.mihou.amelia.db.FeedManager;
import pw.mihou.amelia.fault.FaultTolerance;
import pw.mihou.amelia.io.ReadRSS;
import pw.mihou.amelia.io.Scheduler;
import pw.mihou.amelia.payloads.AmeliaPayload;
import pw.mihou.amelia.wrappers.ItemWrapper;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Feeds {

    public static void run() {
        if (AmeliaServer.connections.isEmpty()) {
            Amelia.log.warn("No connections are found, feed scheduler will be delayed for until a connection is established...");
            return;
        }

        AtomicInteger bucket = new AtomicInteger(0);
        FeedManager.request().thenAccept(feedModels -> feedModels.forEach(feedModel ->
                Scheduler.schedule(() -> {
                    List<ItemWrapper> feeds = ReadRSS.getLatest(feedModel.getFeedURL())
                            .stream()
                            .filter(itemWrapper -> itemWrapper.getPubDate().after(feedModel.getDate()))
                            .collect(Collectors.toUnmodifiableList());

                    feeds.forEach(itemWrapper -> AmeliaServer.sendPayload(new AmeliaPayload(itemWrapper,
                            feedModel.setPublishedDate(itemWrapper.getPubDate(), false)),
                            "feed"));

                    // Update only on the first result (which is guaranteed to be the last update).
                    feeds.stream().findFirst().ifPresent(itemWrapper -> feedModel.setPublishedDate(itemWrapper.getPubDate(), true));

                    if (feeds.size() > 0) {
                        Amelia.log.info("Nodes were notified of a total of {} updates. [feed={}, modelId={}]", feeds.size(), feedModel.getFeedURL(), feedModel.getUnique());
                    }
                }, bucket.addAndGet(2), TimeUnit.SECONDS))).exceptionally(throwable -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
            return null;
        });
    }

}
