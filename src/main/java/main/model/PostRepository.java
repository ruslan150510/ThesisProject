package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("select p from Post p where is_active = 1 and moderation_status in ('ACCEPTED')" +
            " order by time DESC")
    Iterable<Post> findAllByModerationStatusAndSortDesc();

    @Query("select p from Post p where is_active = 1 and moderation_status in ('ACCEPTED')" +
            " order by time")
    Iterable<Post> findAllByModerationStatusAndSortAbs();

    @Query("SELECT p FROM Post p WHERE is_active = 1 and moderation_status in ('ACCEPTED')")
    Iterable<Post> findAllByAccepted();
}
