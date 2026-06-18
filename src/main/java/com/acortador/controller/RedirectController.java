package com.acortador.controller;

import com.acortador.dto.ErrorResponse;
import com.acortador.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@Tag(name = "Redirect", description = "Redirección al enlace original e incremento de clics")
public class RedirectController {

    private final ShortUrlService service;

    @Operation(summary = "Redirigir al enlace original",
               description = "Incrementa el contador de clics y redirige con HTTP 302 al destino")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirección al enlace original"),
            @ApiResponse(responseCode = "404", description = "Código no encontrado",
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{code:[a-zA-Z0-9][a-zA-Z0-9_-]{2,29}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.resolveUrl(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
