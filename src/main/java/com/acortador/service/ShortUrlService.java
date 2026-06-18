package com.acortador.service;

import com.acortador.dto.CreateShortUrlRequest;
import com.acortador.dto.ShortUrlResponse;
import com.acortador.exception.AliasAlreadyTakenException;
import com.acortador.exception.ShortUrlNotFoundException;
import com.acortador.model.ShortUrl;
import com.acortador.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    private final ShortUrlRepository repository;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public ShortUrlResponse create(CreateShortUrlRequest request) {
        String code;
        String alias = request.getAlias();

        if (alias != null && !alias.isBlank()) {
            if (repository.existsByCode(alias)) {
                throw new AliasAlreadyTakenException(alias);
            }
            code = alias;
        } else {
            code = generateUniqueCode();
        }

        ShortUrl saved = repository.save(
                ShortUrl.builder()
                        .originalUrl(request.getUrl())
                        .code(code)
                        .build()
        );
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ShortUrlResponse getStats(String code) {
        ShortUrl shortUrl = findOrThrow(code);
        return toResponse(shortUrl);
    }

    @Transactional
    public String resolveUrl(String code) {
        ShortUrl shortUrl = findOrThrow(code);
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        return shortUrl.getOriginalUrl();
    }

    private ShortUrl findOrThrow(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (repository.existsByCode(code));
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        return ShortUrlResponse.builder()
                .code(shortUrl.getCode())
                .originalUrl(shortUrl.getOriginalUrl())
                .shortUrl(baseUrl + "/" + shortUrl.getCode())
                .clickCount(shortUrl.getClickCount())
                .createdAt(shortUrl.getCreatedAt())
                .build();
    }
}
