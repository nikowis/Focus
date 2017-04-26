package pl.nikowis.focus.ui.facebook;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookPost {
    private String pageName;
    private String description;

    public FacebookPost(String pageName, String description) {
        this.pageName = pageName;
        this.description = description;
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
}
