package com.travelog.board.repository;

import com.travelog.board.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByHashtag(String hashtag);

    @Query(value = "select h.hashtag from hashtag h join board_hashtag bh on h.hashtag_id = bh.hashtag_id " +
            "group by h.hashtag order by count(*) desc limit 5", nativeQuery = true)
    List<String> findByHashtagTop5();
}
