package whilter.ai.ads_manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Entity
@Table(name = "oauth_token")
public class OAuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String provider;
    @Column(nullable = false, length = 500)
    private String accessToken;
    @Column(nullable = false)
    private Instant expiresAt;

    private Instant updatedOn;
}

