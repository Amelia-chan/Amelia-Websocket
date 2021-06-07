package pw.mihou.amelia.db;

import com.mongodb.client.MongoDatabase;
import pw.mihou.amelia.models.SHUser;
import pw.mihou.amelia.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserDB {

    private static final MongoDatabase database = MongoDB.database("notifications");

    public static CompletableFuture<List<UserModel>> load() {
        return CompletableFuture.supplyAsync(() -> {
            List<UserModel> users = new ArrayList<>();
            database.listCollectionNames().forEach(s -> {
                List<SHUser> c = new ArrayList<>();
                database.getCollection(s).find().forEach(document -> c.add(new SHUser(document.getString("url"),
                        document.getInteger("unique"), document.getString("name"))));
                UserModel model = new UserModel(Long.parseLong(s), c);
                users.add(model);
            });

            return users;
        });
    }

}
