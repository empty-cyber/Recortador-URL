package com.acortador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class CreateShortUrlRequest {

    @NotBlank(message = "La URL no puede estar vacía")
    @URL(message = "Debe ser una URL válida (incluye el protocolo, ej: https://ejemplo.com)")
    @Size(max = 2048, message = "La URL no puede superar los 2048 caracteres")
    private String url;

    @Size(min = 3, max = 30, message = "El alias debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$",
             message = "El alias solo puede contener letras, números, guiones (-) y guiones bajos (_)")
    private String alias;
}
