package com.travelog.board.repository;

import com.travelog.board.entity.BoardHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHashtagRepository extends JpaRepository<BoardHashtag, Long> {

}
