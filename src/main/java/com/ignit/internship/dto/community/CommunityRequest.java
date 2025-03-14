package com.ignit.internship.dto.community;

import java.util.List;

public class CommunityRequest {

    private String title;

    private String content;

    private String url;

    private List<String> tags;

    public CommunityRequest(String title, String content, String url, List<String> tags) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getTags() {
        return tags;
    }
}
