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

    @Query(value = "select u from User u where u.email = :email and u.id != :id")
    Optional<User> findByEmailExcludId(@Param("email") String email, @Param("id") Integer id);

    Optional<User> findByEmail(String email);

//    @Query("select u from User u where u.code = :code")
    Optional<User> findByCode(@Param("code") String code);


}
