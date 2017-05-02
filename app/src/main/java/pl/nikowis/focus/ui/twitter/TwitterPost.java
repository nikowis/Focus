package pl.nikowis.focus.ui.twitter;

import java.util.Date;

import pl.nikowis.focus.rest.twitter.TwitterFeedDataResponse;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterPost {
    private String title;
    private String description;
    private Date date;
    private String link;
    private TwitterFeedDataResponse.TwitterSinglePostResponse.Images.Thumbnail thumbnail;

    public TwitterPost(String title, String description, Date date, String link
            , TwitterFeedDataResponse.TwitterSinglePostResponse.Images.Thumbnail thumbnail) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.thumbnail = thumbnail;
    }

    public TwitterPost(String title, Date date, String link, TwitterFeedDataResponse.TwitterSinglePostResponse.Images.Thumbnail thumbnail) {
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

    public TwitterFeedDataResponse.TwitterSinglePostResponse.Images.Thumbnail getThumbnail() {
        return thumbnail;
    }
}
