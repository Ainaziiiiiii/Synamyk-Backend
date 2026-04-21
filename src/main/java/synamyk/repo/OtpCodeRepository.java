package synamyk.repo;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synamyk.entities.OTPCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OTPCode, Long> {

    @Modifying
    @Query("""
            UPDATE OTPCode o
            SET o.verified = true,
                o.used = true,
                o.usedAt = :now
            WHERE o.phone = :phone
            AND o.type = :type
            AND o.verified = false
            """)
    int deactivateOldCodes(
            @Param("phone") String phone,
            @Param("type") OTPCode.OtpType type,
            @Param("now") LocalDateTime now
    );

    Optional<OTPCode> findFirstByPhoneAndTypeAndVerifiedFalseOrderByCreatedAtDesc(String phone, OTPCode.@NotNull OtpType type);

    List<OTPCode> findByPhoneAndTypeAndVerifiedFalseOrderByCreatedAtDesc(String phone, OTPCode.@NotNull OtpType type);
}