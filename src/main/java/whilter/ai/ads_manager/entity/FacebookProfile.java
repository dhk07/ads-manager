package whilter.ai.ads_manager.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import whilter.ai.ads_manager.utility.AttributeEncryptor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "customer") // Prevent infinite recursion
@EqualsAndHashCode(exclude = "customer") // Prevent infinite recursion
@Entity
public class FacebookProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facebookId;

    private String emailId;

    @Convert(converter = AttributeEncryptor.class)
    @Column( length = 1000)
    private String accessToken;

    private Instant tokenExpiry;

    private boolean linkedStatus;
    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

}
