package main.model.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("select p from Post p where is_active = 1 and moderation_status = 'ACCEPTED'")
    Page<Post> findAllByModerationStatusAndSort(@Param("offset") int offset, Pageable pageable);

    @Query(value = "SELECT * FROM Posts p WHERE is_active = 1 and moderation_status in ('ACCEPTED')" +
            "order by (select count(*) from post_comments where post_comments.post_id = p.id) DESC" +
            " limit :limit offset :offset", nativeQuery = true)
    List<Post> findAllByPopular(@Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT * FROM Posts p WHERE is_active = 1 and moderation_status in ('ACCEPTED')" +
            "order by (select sum(value) from post_votes where post_votes.post_id = p.id) DESC" +
            " limit :limit offset :offset", nativeQuery = true)
    List<Post> findAllByBest(@Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT count(p) FROM Post p WHERE is_active = 1 and moderation_status in ('ACCEPTED')")
    Optional<Integer> postsIsActive();

    @Query(value = "select * from Posts where text like %:query% and is_active = 1 and " +
            "moderation_status in ('ACCEPTED') order by time DESC limit :limit offset :offset",
            nativeQuery = true)
    List<Post> findAllByQuery(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("query") String query);

    @Query("select p from Post p where is_active = 1 and moderation_status in ('ACCEPTED') " +
            "and year(time) = :year")
    List<Post> findAllByYear(@Param("year") int year);

    @Query(value = "select * from Posts where is_active = 1 and moderation_status in ('ACCEPTED')" +
            " and date(time) = :time limit :limit offset :offset", nativeQuery = true)
    List<Post> findAllByDate(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("time") String time);

    @Query(value = "select p.* from Posts p " +
            "join tag2post ON tag2post.post_id = p.id " +
            "join tags ON tags.id = tag2post.tag_id " +
            "where is_active = 1 and moderation_status in ('ACCEPTED') " +
            "and tags.name = :tag limit :limit offset :offset",
            nativeQuery = true)
    List<Post> findAllByTag(@Param("offset") int offset, @Param("limit") int limit,
                            @Param("tag") String tag);

    @Query("select p from Post p where is_active = 1 and moderation_status in ('ACCEPTED') " +
            "and p.id = :id")
    Optional<Post> findById(@Param("id") int id);

    @Transactional
    @Modifying
    @Query("update Post p Set p.viewCount = p.viewCount + 1 where p.id = :id")
    void iterableViewCount(@Param("id") int id);

//    @Query("select p from Post p where moderation_status in ('NEW')")
//    List<Post> findByModerationPost();

    @Query("select p from Post p where user_id = :user_id and is_active = 0")
    List<Post> findMyInactivePost(@Param("user_id") int userId);

    @Query("select p from Post p where user_id = :user_id and is_active = 1 " +
            "and moderation_status = 'NEW'")
    List<Post> findMyPendingPost(@Param("user_id") int userId);

    @Query("select p from Post p where user_id = :user_id and is_active = 1 " +
            "and moderation_status = 'DECLINED'")
    List<Post> findMyDeclinedPost(@Param("user_id") int userId);

    @Query("select p from Post p where user_id = :user_id and is_active = 1 " +
            "and moderation_status = 'ACCEPTED'")
    List<Post> findMyPublishedPost(@Param("user_id") int userId);

    @Query("select p from Post p where is_active = 1 and moderation_status = 'NEW'")
    List<Post> findNewPost();

    @Query("select p from Post p where moderator_id = :moderator_id and is_active = 1 " +
            "and moderation_status = 'DECLINED'")
    List<Post> findModerationDeclinedPost(@Param("moderator_id") int moderatorId);

    @Query("select p from Post p where moderator_id = :moderator_id and is_active = 1 " +
            "and moderation_status = 'ACCEPTED'")
    List<Post> findModerationPublishedPost(@Param("moderator_id") int moderatorId);

    @Query("select min(p.time) from Post p where is_active = 1 and moderation_status = 'ACCEPTED' " +
            "and p.id = :id")
    Optional<Post> findFirstPublicationById(@Param("id") int id);

    @Query("select min(p.time) from Post p where is_active = 1 and moderation_status = 'ACCEPTED'")
    Optional<Post> findFirstPublication();
}
