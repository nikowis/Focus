package pl.nikowis.focus;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class MediaItem {
    private String title;
    private String description;

    public MediaItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
