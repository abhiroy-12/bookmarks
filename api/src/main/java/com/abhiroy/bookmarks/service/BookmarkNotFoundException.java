package com.abhiroy.bookmarks.service;

import java.util.UUID;

public class BookmarkNotFoundException extends RuntimeException {

    public BookmarkNotFoundException(UUID id) {
        super("Bookmark not found: " + id);
    }
}
