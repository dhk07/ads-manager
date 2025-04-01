package whilter.ai.ads_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whilter.ai.ads_manager.entity.FacebookProfile;

import java.util.Optional;

public interface FacebookProfileRepository extends JpaRepository<FacebookProfile, Long> {
    Optional<FacebookProfile> findByCustomerId(Long userId);
    Optional<FacebookProfile> findByFacebookId(String facebookId);
}
