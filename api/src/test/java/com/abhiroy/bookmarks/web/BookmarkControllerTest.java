package com.abhiroy.bookmarks.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abhiroy.bookmarks.dto.BookmarkResponse;
import com.abhiroy.bookmarks.dto.CreateBookmarkRequest;
import com.abhiroy.bookmarks.dto.UpdateBookmarkRequest;
import com.abhiroy.bookmarks.service.BookmarkNotFoundException;
import com.abhiroy.bookmarks.service.BookmarkService;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookmarkService service;

    private BookmarkResponse sample(UUID id) {
        return new BookmarkResponse(id, "https://example.com", "Example", "A site", Instant.now());
    }

    @Test
    void listReturnsAll() throws Exception {
        given(service.findAll()).willReturn(List.of(sample(UUID.randomUUID())));

        mockMvc.perform(get("/api/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].url").value("https://example.com"));
    }

    @Test
    void getByIdReturnsBookmark() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.findById(id)).willReturn(sample(id));

        mockMvc.perform(get("/api/bookmarks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Example"));
    }

    @Test
    void getByIdReturnsProblemDetailWhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.findById(id)).willThrow(new BookmarkNotFoundException(id));

        mockMvc.perform(get("/api/bookmarks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bookmark not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.create(any(CreateBookmarkRequest.class))).willReturn(sample(id));
        var body = new CreateBookmarkRequest("https://example.com", "Example", "A site");

        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/bookmarks/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void createWithBlankUrlReturns400() throws Exception {
        var body = new CreateBookmarkRequest("", "Example", "A site");

        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation error"))
                .andExpect(jsonPath("$.errors.url").exists());

        verify(service, never()).create(any());
    }

    @Test
    void createWithMalformedUrlReturns400() throws Exception {
        var body = new CreateBookmarkRequest("not-a-url", "Example", "A site");

        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.url").exists());

        verify(service, never()).create(any());
    }

    @Test
    void updateReturnsUpdatedBookmark() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.update(eq(id), any(UpdateBookmarkRequest.class))).willReturn(sample(id));
        var body = new UpdateBookmarkRequest("https://example.com", "Example", "A site");

        mockMvc.perform(put("/api/bookmarks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/api/bookmarks/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new BookmarkNotFoundException(id)).when(service).delete(id);

        mockMvc.perform(delete("/api/bookmarks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Bookmark not found"));
    }
}
