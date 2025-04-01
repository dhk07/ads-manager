package whilter.ai.ads_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whilter.ai.ads_manager.entity.InstagramProfile;

import java.util.Optional;

public interface InstagramProfileRepository extends JpaRepository<InstagramProfile, Long> {
    Optional<InstagramProfile> findByCustomerId(Long userId);
    Optional<InstagramProfile> findByInstagramId(String instagramId);
}
