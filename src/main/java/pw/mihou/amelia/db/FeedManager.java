package pw.mihou.amelia.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import pw.mihou.amelia.io.Scheduler;
import pw.mihou.amelia.models.FeedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FeedManager {

    private static final MongoCollection<Document> db = MongoDB.collection("feeds", "amelia");

    public static void updateModel(long unique, String key, Object value) {
        CompletableFuture.runAsync(() -> db.updateOne(Filters.eq("unique", unique), Updates.set(key, value)),
                Scheduler.getExecutorService());
    }

    public static CompletableFuture<Collection<FeedModel>> request() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<FeedModel> models = new ArrayList<>();

            db.find().forEach(doc -> models.add(FeedModel.from(doc)));
            return models;
        }, Scheduler.getExecutorService());
    }

    public static CompletableFuture<FeedModel> request(long unique) {
        return CompletableFuture.supplyAsync(() -> FeedModel.from(Objects.requireNonNull(db.find(Filters.eq("unique", unique)).first())));
    }

    public static void remove(long unique){
        CompletableFuture.runAsync(() -> db.deleteOne(Filters.eq("unique", unique)), Scheduler.getExecutorService());
    }

}
