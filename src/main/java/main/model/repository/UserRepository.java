package main.model.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @Query(value = "select max(u.id) from Users u",
            nativeQuery = true)
    Integer findByLastId();

//    @Query(value = "select count(id) from Users u where email = :email",
//    nativeQuery = true)
//    Integer findByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);
}
