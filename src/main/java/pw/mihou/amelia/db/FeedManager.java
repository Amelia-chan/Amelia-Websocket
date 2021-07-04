package pw.mihou.amelia.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import pw.mihou.amelia.io.Scheduler;
import pw.mihou.amelia.models.FeedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FeedManager {

    private static final MongoCollection<Document> db = MongoDB.collection("feeds", "amelia");

    public static CompletableFuture<Void> updateModel(long unique, String key, Object value) {
        return CompletableFuture.runAsync(() -> db.updateOne(Filters.eq("unique", unique),
                Updates.set(key, value)), Scheduler.getExecutorService());
    }

    public static CompletableFuture<Collection<FeedModel>> request() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<FeedModel> models = new ArrayList<>();
            db.find().forEach(doc -> models.add(new FeedModel(doc.getLong("unique"), doc.getInteger("id"),
                    doc.getString("url"), doc.getLong("channel"), doc.getLong("user"), doc.getString("name"), doc.getDate("date"),
                    doc.get("mentions", new ArrayList<>()))));
            return models;
        });
    }

    public static void remove(long unique){
        db.deleteOne(Filters.eq("unique", unique));
    }

}
