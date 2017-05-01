package pl.nikowis.focus.ui.instagram;

import java.util.Date;

import pl.nikowis.focus.rest.instagram.InstaFeedDataResponse;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramPost {
    private String title;
    private String description;
    private Date date;
    private String link;
    private InstaFeedDataResponse.InstaSinglePostResponse.Images.Thumbnail thumbnail;

    public InstagramPost(String title, String description, Date date, String link
            , InstaFeedDataResponse.InstaSinglePostResponse.Images.Thumbnail thumbnail) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.thumbnail = thumbnail;
    }

    public InstagramPost(String title, Date date, String link, InstaFeedDataResponse.InstaSinglePostResponse.Images.Thumbnail thumbnail) {
        this.title = title;
        this.description = "";
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

    public InstaFeedDataResponse.InstaSinglePostResponse.Images.Thumbnail getThumbnail() {
        return thumbnail;
    }
}
