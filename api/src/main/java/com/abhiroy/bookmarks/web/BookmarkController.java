package com.abhiroy.bookmarks.web;

import com.abhiroy.bookmarks.dto.BookmarkResponse;
import com.abhiroy.bookmarks.dto.CreateBookmarkRequest;
import com.abhiroy.bookmarks.dto.UpdateBookmarkRequest;
import com.abhiroy.bookmarks.service.BookmarkService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService service;

    public BookmarkController(BookmarkService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookmarkResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookmarkResponse get(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookmarkResponse> create(
            @Valid @RequestBody CreateBookmarkRequest request,
            UriComponentsBuilder uriBuilder) {
        BookmarkResponse created = service.create(request);
        URI location = uriBuilder.path("/api/bookmarks/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public BookmarkResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookmarkRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
