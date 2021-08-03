package main.model.repository;

import main.model.CaptchaCodes;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {
    @Query(value = "select MAX(id) from captcha_codes p",
            nativeQuery = true)
    Integer findLastId();

    @Modifying
    @Transactional
    @Query(value = "delete from captcha_codes p where p.time < :time",
            nativeQuery = true)
    void deleteByOldRecord(@Param("time") LocalDateTime time);

//    @Query(value = "select secret_code from captcha_codes p where p.secret_code = :secret_code",
//            nativeQuery = true)
//    String findBySecretCode(@Param("secret_code") String secretCode);
}
