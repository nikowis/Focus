package pl.nikowis.focus.ui.gmail;

import java.util.Date;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class GmailMessage {
    private String title;
    private String description;
    private Date date;
    private String id;

    public GmailMessage(String title, String description, Long date, String id) {
        this.title = title;
        this.description = description;
        this.date = new Date(date);
        this.id = id;
    }

    public GmailMessage(String title, String description, String id) {
        this.title = title;
        this.description = description;
        this.date = new Date();
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
