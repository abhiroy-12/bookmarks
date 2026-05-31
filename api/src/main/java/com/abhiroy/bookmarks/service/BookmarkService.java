package com.abhiroy.bookmarks.service;

import com.abhiroy.bookmarks.domain.Bookmark;
import com.abhiroy.bookmarks.dto.BookmarkResponse;
import com.abhiroy.bookmarks.dto.CreateBookmarkRequest;
import com.abhiroy.bookmarks.dto.UpdateBookmarkRequest;
import com.abhiroy.bookmarks.repository.BookmarkRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkService {

    private final BookmarkRepository repository;

    public BookmarkService(BookmarkRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> findAll() {
        return repository.findAll().stream()
                .map(BookmarkResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookmarkResponse findById(UUID id) {
        return repository.findById(id)
                .map(BookmarkResponse::from)
                .orElseThrow(() -> new BookmarkNotFoundException(id));
    }

    public BookmarkResponse create(CreateBookmarkRequest request) {
        Bookmark bookmark = new Bookmark(request.url(), request.title(), request.description());
        return BookmarkResponse.from(repository.save(bookmark));
    }

    public BookmarkResponse update(UUID id, UpdateBookmarkRequest request) {
        Bookmark bookmark = repository.findById(id)
                .orElseThrow(() -> new BookmarkNotFoundException(id));
        bookmark.setUrl(request.url());
        bookmark.setTitle(request.title());
        bookmark.setDescription(request.description());
        return BookmarkResponse.from(repository.save(bookmark));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new BookmarkNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
