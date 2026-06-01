package com.abhiroy.bookmarks.repository;

import com.abhiroy.bookmarks.domain.Bookmark;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
}
