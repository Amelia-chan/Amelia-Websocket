package pw.mihou.amelia.models;

import org.bson.Document;
import pw.mihou.amelia.Amelia;
import pw.mihou.amelia.db.FeedManager;

import java.util.ArrayList;
import java.util.Date;

public class FeedModel {

    private final int id;
    private final int unique;
    private final String feedURL;
    private final long channel;
    private final String name;
    private final long user;
    private final ArrayList<Long> mentions = new ArrayList<>();
    private Date date;

    public FeedModel(int unique, int id, String feedURL, long channel, long user, String name, Date date, ArrayList<Long> mentions) {
        this.unique = unique;
        this.id = id;
        this.feedURL = feedURL;
        this.channel = channel;
        this.user = user;
        this.name = name;
        this.date = date;
        this.mentions.addAll(mentions);
    }

    /**
     * Sets the published date of this feed model.
     *
     * @param date The new date of this feed model.
     * @param update Whether to update this field onto the database?
     * @return A new feed model.
     */
    public FeedModel setPublishedDate(Date date, boolean update) {

        if (update) {
            FeedManager.updateModel(unique, "date", date);
            Amelia.log.debug("Updated published date for {}. [prevDate={}, newDate={}]", feedURL, this.date.toString(), date.toString());
        }

        this.date = date;
        return this;
    }

    /**
     * Gets the unique ID of this Feed Model.
     *
     * @return The unique ID of the model.
     */
    public int getUnique() {
        return unique;
    }

    /**
     * Retrieves the current date of this feed model.
     * 
     * @return The date of this feed model.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the URL of this feed model.
     *
     * @return The URL of this feed model.
     */
    public String getFeedURL() {
        return feedURL;
    }

    /**
     * Transforms a MongoDB-document into a Feed Model which the websocket
     * client can understand.
     *
     * @param doc The document to transform.
     * @return A new Feed Model.
     */
    public static FeedModel from(Document doc) {
        return new FeedModel(doc.getInteger("unique"),
                doc.getInteger("id"),
                doc.getString("url"),
                doc.getLong("channel"),
                doc.getLong("user"),
                doc.getString("name"),
                doc.getDate("date"),
                doc.get("mentions", new ArrayList<>()));
    }

}
