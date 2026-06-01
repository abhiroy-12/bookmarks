package com.abhiroy.bookmarks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateBookmarkRequest(
        @NotBlank @URL @Size(max = 2048) String url,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 4096) String description) {
}
