package pw.mihou.amelia.payloads;

import com.google.gson.annotations.SerializedName;
import pw.mihou.amelia.models.FeedModel;
import pw.mihou.amelia.wrappers.ItemWrapper;

public class AmeliaPayload {

    @SerializedName("wrapper")
    public final ItemWrapper wrapper;
    @SerializedName("model")
    public final FeedModel model;

    public AmeliaPayload(ItemWrapper wrapper, FeedModel model) {
        this.wrapper = wrapper;
        this.model = model;
    }

}
