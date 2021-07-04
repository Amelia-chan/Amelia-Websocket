package pw.mihou.amelia.fault;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import pw.mihou.amelia.db.FeedManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FaultTolerance {

    /**
     * Fault Tolerance concept.
     * - Amelia's fault tolerance concept is based on the fact that
     * - a feed can receive more than 50 errors within 550 minutes (or 9 hours)
     * - 550 minutes is because 55 connect attempts to the feed.
     * - Most of the time that 55 connects would be logged as non-IOException errors.
     * - and the feed would therefore be killed.
     */

    private static final LoadingCache<Long, AtomicInteger> faults = Caffeine.newBuilder()
            .expireAfterWrite(700, TimeUnit.MINUTES).build(key -> new AtomicInteger(0));

    public static void addFault(long feed){
        if(faults.get(feed).addAndGet(1) > 50)
            FeedManager.remove(feed);
    }

}
