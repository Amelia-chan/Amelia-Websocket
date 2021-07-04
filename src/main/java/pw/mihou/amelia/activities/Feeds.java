package pw.mihou.amelia.activities;

import pw.mihou.amelia.Amelia;
import pw.mihou.amelia.connections.AmeliaServer;
import pw.mihou.amelia.db.FeedManager;
import pw.mihou.amelia.fault.FaultTolerance;
import pw.mihou.amelia.io.ReadRSS;
import pw.mihou.amelia.io.Scheduler;
import pw.mihou.amelia.payloads.AmeliaPayload;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Feeds {

    public static void run() {
        if (AmeliaServer.connections.isEmpty()) {
            Amelia.log.warn("No connections are found, feed scheduler will be delayed for until a connection is established...");
            return;
        } else {
            AtomicInteger bucket = new AtomicInteger(0);
            FeedManager.request().thenAccept(feedModels -> feedModels.forEach(feedModel ->
                    Scheduler.schedule(() -> ReadRSS.getLatest(feedModel.getFeedURL()).ifPresentOrElse(item ->
                            item.getPubDate().ifPresent(date -> {
                                if (date.after(feedModel.getDate())) {
                                    AmeliaServer.sendPayload(new AmeliaPayload(item, feedModel.setPublishedDate(date)), "feed");
                                    Amelia.log.info("All {} nodes were notified for feed [{}].", AmeliaServer.connections.size(), feedModel.getUnique());
                                }
                            }), () -> {
                        FaultTolerance.addFault(feedModel.getUnique());
                        Amelia.log.error("We couldn't find any results for {} from {}.", feedModel.getName(), feedModel.getFeedURL());
                    }), bucket.addAndGet(2), TimeUnit.SECONDS))).exceptionally(throwable -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return null;
            });
        }
    }

}
