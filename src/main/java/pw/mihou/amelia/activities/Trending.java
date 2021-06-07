package pw.mihou.amelia.activities;

import pw.mihou.amelia.Amelia;
import pw.mihou.amelia.connections.AmeliaServer;
import pw.mihou.amelia.db.UserDB;
import pw.mihou.amelia.payloads.AmeliaTrendingPayload;
import pw.mihou.amelia.wrappers.AmatsukiWrapper;
import tk.mihou.amatsuki.entities.story.lower.StoryResults;

import java.util.List;
import java.util.stream.Collectors;

public class Trending {

    public static void run() {
        if (AmeliaServer.connections.isEmpty())
            return;

        AmatsukiWrapper.getConnector().getTrending().thenAccept(storyResults -> {
            List<StoryResults> trending = storyResults.stream().limit(9).collect(Collectors.toList());

            UserDB.load().thenAccept(userModels -> userModels.forEach(userModel -> userModel.getAccounts()
                    .forEach(shUser -> shUser.asUser().thenAccept(user -> trending.stream()
                            .filter(r -> matches(r.getCreator(), user.getName()))
                            .forEachOrdered(results -> AmeliaServer
                                    .sendPayload(new AmeliaTrendingPayload(userModel.getUser(), results, user.getName()),
                                            "trending"))))));
            Amelia.log.info("All {} nodes were notified for today's trending.", AmeliaServer.connections.size());
        });
    }

    private static boolean matches(String creator, String user) {
        return creator.equals(user);
    }

}
