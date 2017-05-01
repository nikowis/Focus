package pl.nikowis.focus.ui.instagram;

import java.util.Date;

import pl.nikowis.focus.rest.instagram.InstaPostsDataResponse;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramPost {
    private String title;
    private String description;
    private Date date;
    private String link;
    private InstaPostsDataResponse.InstaSinglePostResponse.Images.Thumbnail thumbnail;

    public InstagramPost(String title, String description, Date date, String link
            , InstaPostsDataResponse.InstaSinglePostResponse.Images.Thumbnail thumbnail) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.thumbnail = thumbnail;
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

    public String getLink() {
        return link;
    }
}
