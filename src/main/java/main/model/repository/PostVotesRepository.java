package main.model.repository;

import main.model.PostVotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer> {
    @Query("select p from PostVotes p where user_id = :user_id and post_id = :post_id")
    Optional<PostVotes> findByUserIdAndPostId(@Param("user_id") int userId, @Param("post_id") int postId);
}
