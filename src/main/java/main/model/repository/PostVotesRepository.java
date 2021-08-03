package main.model.repository;

import main.model.PostVotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer> {
}
