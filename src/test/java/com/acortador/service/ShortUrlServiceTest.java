package com.acortador.service;

import com.acortador.dto.CreateShortUrlRequest;
import com.acortador.dto.ShortUrlResponse;
import com.acortador.exception.ShortUrlNotFoundException;
import com.acortador.model.ShortUrl;
import com.acortador.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository repository;

    @InjectMocks
    private ShortUrlService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnResponse() {
        var request = buildRequest("https://example.com");
        var saved = buildEntity("abc123", "https://example.com", 0L);

        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(saved);

        ShortUrlResponse response = service.create(request);

        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(response.getCode()).isEqualTo("abc123");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/abc123");
        assertThat(response.getClickCount()).isZero();
        verify(repository).save(any(ShortUrl.class));
    }

    @Test
    void create_shouldRetryCodeGenerationOnCollision() {
        var request = buildRequest("https://example.com");
        var saved = buildEntity("newcod", "https://example.com", 0L);

        when(repository.existsByCode(anyString()))
                .thenReturn(true)   // primera iteración: colisión
                .thenReturn(false); // segunda iteración: disponible
        when(repository.save(any())).thenReturn(saved);

        service.create(request);

        verify(repository, times(2)).existsByCode(anyString());
    }

    // --- getStats ---

    @Test
    void getStats_shouldReturnResponseWhenCodeExists() {
        when(repository.findByCode("abc123"))
                .thenReturn(Optional.of(buildEntity("abc123", "https://example.com", 5L)));

        ShortUrlResponse response = service.getStats("abc123");

        assertThat(response.getCode()).isEqualTo("abc123");
        assertThat(response.getClickCount()).isEqualTo(5L);
    }

    @Test
    void getStats_shouldThrowWhenCodeNotFound() {
        when(repository.findByCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStats("missing"))
                .isInstanceOf(ShortUrlNotFoundException.class)
                .hasMessageContaining("missing");
    }

    // --- resolveUrl ---

    @Test
    void resolveUrl_shouldReturnOriginalUrlAndIncrementClickCount() {
        var entity = buildEntity("abc123", "https://example.com", 3L);
        when(repository.findByCode("abc123")).thenReturn(Optional.of(entity));

        String result = service.resolveUrl("abc123");

        assertThat(result).isEqualTo("https://example.com");
        assertThat(entity.getClickCount()).isEqualTo(4L);
    }

    @Test
    void resolveUrl_shouldThrowWhenCodeNotFound() {
        when(repository.findByCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveUrl("missing"))
                .isInstanceOf(ShortUrlNotFoundException.class);
    }

    // --- helpers ---

    private CreateShortUrlRequest buildRequest(String url) {
        var req = new CreateShortUrlRequest();
        req.setUrl(url);
        return req;
    }

    private ShortUrl buildEntity(String code, String originalUrl, Long clicks) {
        return ShortUrl.builder()
                .id(1L)
                .code(code)
                .originalUrl(originalUrl)
                .clickCount(clicks)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
