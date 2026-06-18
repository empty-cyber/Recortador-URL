package com.acortador.controller;

import com.acortador.dto.ShortUrlResponse;
import com.acortador.exception.ShortUrlNotFoundException;
import com.acortador.service.ShortUrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShortUrlController.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShortUrlService service;

    // --- POST /api/links ---

    @Test
    void create_shouldReturn201WithBody() throws Exception {
        when(service.create(any())).thenReturn(buildResponse("abc123", "https://example.com", 0L));

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("url", "https://example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc123"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc123"))
                .andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    void create_shouldReturn400WhenUrlIsBlank() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("url", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void create_shouldReturn400WhenUrlIsInvalid() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("url", "not-a-valid-url"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_shouldReturn400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // --- GET /api/links/{code} ---

    @Test
    void getStats_shouldReturn200WithBody() throws Exception {
        when(service.getStats("abc123")).thenReturn(buildResponse("abc123", "https://example.com", 7L));

        mockMvc.perform(get("/api/links/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(7));
    }

    @Test
    void getStats_shouldReturn404WhenCodeNotFound() throws Exception {
        when(service.getStats("missing")).thenThrow(new ShortUrlNotFoundException("missing"));

        mockMvc.perform(get("/api/links/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No existe un enlace con el código: missing"));
    }

    // --- helper ---

    private ShortUrlResponse buildResponse(String code, String originalUrl, Long clicks) {
        return ShortUrlResponse.builder()
                .code(code)
                .originalUrl(originalUrl)
                .shortUrl("http://localhost:8080/" + code)
                .clickCount(clicks)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
