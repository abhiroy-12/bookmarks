package com.abhiroy.bookmarks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.abhiroy.bookmarks.domain.Bookmark;
import com.abhiroy.bookmarks.dto.BookmarkResponse;
import com.abhiroy.bookmarks.dto.CreateBookmarkRequest;
import com.abhiroy.bookmarks.dto.UpdateBookmarkRequest;
import com.abhiroy.bookmarks.repository.BookmarkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository repository;

    @InjectMocks
    private BookmarkService service;

    private Bookmark persisted(UUID id) {
        Bookmark bookmark = new Bookmark("https://example.com", "Example", "A site");
        ReflectionTestUtils.setField(bookmark, "id", id);
        ReflectionTestUtils.setField(bookmark, "createdAt", Instant.now());
        return bookmark;
    }

    @Test
    void findAllMapsToResponses() {
        given(repository.findAll()).willReturn(List.of(persisted(UUID.randomUUID())));

        List<BookmarkResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).url()).isEqualTo("https://example.com");
    }

    @Test
    void findByIdReturnsResponse() {
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.of(persisted(id)));

        BookmarkResponse result = service.findById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("Example");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(BookmarkNotFoundException.class);
    }

    @Test
    void createSavesAndReturnsResponse() {
        var request = new CreateBookmarkRequest("https://example.com", "Example", "A site");
        given(repository.save(any(Bookmark.class))).willAnswer(invocation -> invocation.getArgument(0));

        BookmarkResponse result = service.create(request);

        assertThat(result.url()).isEqualTo("https://example.com");
        assertThat(result.title()).isEqualTo("Example");
        assertThat(result.description()).isEqualTo("A site");
    }

    @Test
    void updateMutatesExistingAndSaves() {
        UUID id = UUID.randomUUID();
        Bookmark existing = persisted(id);
        given(repository.findById(id)).willReturn(Optional.of(existing));
        given(repository.save(any(Bookmark.class))).willAnswer(invocation -> invocation.getArgument(0));
        var request = new UpdateBookmarkRequest("https://updated.com", "Updated", "New desc");

        BookmarkResponse result = service.update(id, request);

        assertThat(result.url()).isEqualTo("https://updated.com");
        assertThat(result.title()).isEqualTo("Updated");
        assertThat(result.description()).isEqualTo("New desc");
        assertThat(existing.getUrl()).isEqualTo("https://updated.com");
    }

    @Test
    void updateThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new UpdateBookmarkRequest("https://x.com", "X", null)))
                .isInstanceOf(BookmarkNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteRemovesWhenExists() {
        UUID id = UUID.randomUUID();
        given(repository.existsById(id)).willReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void deleteThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        given(repository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(BookmarkNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
