package pl.nikowis.focus.ui.instagram;

import java.util.Date;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramPost {
    private String title;
    private String description;
    private Date date;
    private String id;

    public InstagramPost(String title, String description) {
        this.title = title;
        this.description = description;
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
