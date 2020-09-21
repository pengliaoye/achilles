package com.getxinfo.content.dataaccess;

import java.time.LocalDateTime;

public class NewsBuilder {

    private Long id;
    private String title = "title";
    private String content = "content";
    private LocalDateTime createdAt = LocalDateTime.parse("2007-12-03T10:15:30");
    private LocalDateTime updatedAt = LocalDateTime.parse("2007-12-03T10:15:30");
    private boolean deleted = true;

    public static NewsBuilder news() {
        return new NewsBuilder();
    }

    public NewsBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public NewsBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public NewsBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public NewsBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NewsBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public NewsBuilder withDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public News build() {
        return new News(id, title, content, createdAt, updatedAt, deleted);
    }

}
