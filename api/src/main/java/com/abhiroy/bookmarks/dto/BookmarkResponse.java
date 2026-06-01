package com.abhiroy.bookmarks.dto;

import com.abhiroy.bookmarks.domain.Bookmark;
import java.time.Instant;
import java.util.UUID;

public record BookmarkResponse(
        UUID id,
        String url,
        String title,
        String description,
        Instant createdAt) {

    public static BookmarkResponse from(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getUrl(),
                bookmark.getTitle(),
                bookmark.getDescription(),
                bookmark.getCreatedAt());
    }
}
