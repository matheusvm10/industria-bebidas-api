package br.com.senai.bebidas.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;

public record ErroResponse(
        int status,
        String mensagem,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime timestamp
) {
    public static ErroResponse of(int status, String mensagem) {
        return new ErroResponse(status, mensagem, OffsetDateTime.now());
    }
}
