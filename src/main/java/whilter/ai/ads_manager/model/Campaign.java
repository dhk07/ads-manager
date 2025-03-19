package whilter.ai.ads_manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Campaign {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private String status;
    @ManyToMany
    private Set<Audience> targetAudiences = new HashSet<>();
}
