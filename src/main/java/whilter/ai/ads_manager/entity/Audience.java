package whilter.ai.ads_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Audience {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String criteria; // JSON string with targeting criteria
    private int estimatedReach;
}
