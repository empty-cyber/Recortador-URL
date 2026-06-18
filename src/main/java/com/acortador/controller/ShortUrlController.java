package com.acortador.controller;

import com.acortador.dto.CreateShortUrlRequest;
import com.acortador.dto.ErrorResponse;
import com.acortador.dto.ShortUrlResponse;
import com.acortador.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
@Tag(name = "Links", description = "Creación y consulta de estadísticas de enlaces acortados")
public class ShortUrlController {

    private final ShortUrlService service;

    @Operation(summary = "Crear un enlace corto",
               description = "Genera un código único de 6 caracteres en Base62 para la URL proporcionada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Enlace creado correctamente"),
            @ApiResponse(responseCode = "400", description = "URL inválida o cuerpo malformado",
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Consultar estadísticas de un enlace",
               description = "Devuelve la URL original, el número de clics y la fecha de creación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estadísticas del enlace"),
            @ApiResponse(responseCode = "404", description = "Código no encontrado",
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<ShortUrlResponse> getStats(@PathVariable String code) {
        return ResponseEntity.ok(service.getStats(code));
    }
}
