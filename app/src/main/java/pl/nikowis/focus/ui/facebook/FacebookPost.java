package pl.nikowis.focus.ui.facebook;

import java.util.Date;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookPost {
    private String pageName;
    private String description;
    private Date date;
    private String id;

    public FacebookPost(String pageName, String id, String description, Date date) {
        this.pageName = pageName;
        this.description = description;
        this.date = date;
        this.id = id;
    }

    public String getPageName() {
        return pageName;
    }

    public String getDescription() {
        return description;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }
}
