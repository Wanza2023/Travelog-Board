package com.travelog.board.repository;

import com.travelog.board.entity.BoardHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHashtagRepository extends JpaRepository<BoardHashtag, Long> {
    @Modifying
    @Query("delete from BoardHashtag bh where bh.id in :ids")
    void deleteAllByIds(List<Long> ids);
}
