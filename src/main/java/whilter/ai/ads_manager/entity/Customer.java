package whilter.ai.ads_manager.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"facebookProfile", "instagramProfile"}) // Prevent recursion
@EqualsAndHashCode(exclude = {"facebookProfile", "instagramProfile"}) // Prevent recursion
@Entity
@Table(name = "cutomers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String emailId;

    @Column(nullable = false)
    private String phoneNumber;

    private String password;

    private String providerName;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private FacebookProfile facebookProfile;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private InstagramProfile instagramProfile;
}
