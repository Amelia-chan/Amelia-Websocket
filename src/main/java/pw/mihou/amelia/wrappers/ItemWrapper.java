package pw.mihou.amelia.wrappers;

import com.apptastic.rssreader.Item;
import pw.mihou.amelia.Amelia;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

public class ItemWrapper {

    private final String title;
    private final Date date;
    private final String author;
    private final String link;
    private final String description;

    public ItemWrapper(Item item) {
        this.title = item.getTitle().orElse("");
        this.description = item.getDescription().orElse("");
        this.author = item.getAuthor().orElse("");
        this.link = item.getLink().orElse("");
        this.date = item.getPubDate().map(source -> {
            try {
                return Amelia.formatter.parse(source);
            } catch (ParseException e) {
                Amelia.log.error("Amelia wasn't able to parse the date {}, exception: {}", source, e.getMessage());
                return null;
            }
        }).orElse(null);
    }

    public Date getPubDate() {
        return date;
    }

    public boolean valid() {
        return link != null && title != null && !link.isEmpty() && !link.isBlank() && !title.isEmpty() && !title.isBlank();
    }


}
