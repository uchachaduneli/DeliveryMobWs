package ge.bestline.delivery.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Token {
    public LocalDateTime created;
    public String token;

    public Token(LocalDateTime created, String token) {
        this.created = created;
        this.token = token;
    }
}

