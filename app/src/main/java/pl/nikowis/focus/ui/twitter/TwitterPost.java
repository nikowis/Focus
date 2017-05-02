package pl.nikowis.focus.ui.twitter;

import java.util.Date;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterPost {
    private String title;
    private String description;
    private Date date;
    private String id;

    public TwitterPost(String title, String description, Date date, String id) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }
}
