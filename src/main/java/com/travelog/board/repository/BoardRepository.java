package com.travelog.board.repository;

import com.travelog.board.dto.BoardListDto;
import com.travelog.board.dto.BoardListResDto;
import com.travelog.board.dto.BookmarkListResDto;
import com.travelog.board.dto.PopularListDto;
import com.travelog.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BoardRepository  extends JpaRepository<Board, Long> {
    // 북마크 게시글 조회
    @Query("select b from Board b where b.boardId in :boardIds")
    List<BookmarkListResDto> findByBoardIds(List<Long> boardIds);

    //인기글 조회
    @Query("select b from Board b where b.status = true order by b.views desc limit 10"
    )
    List<PopularListDto> findTop10();

    // 개인 작성 글 목록
    @Query("select b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.nickname = :nickname order by b.createdAt desc"
    )
    List<BoardListDto> findByNickname(String nickname);

    // 지역별 게시글 목록
    @Query("select distinct b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.local = :local order by b.createdAt desc"
    )
    List<BoardListDto> findByLocal(String local);

    // 게시글 상세 조회
    @Query("select b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.boardId = :board_id and b.nickname = :nickname"
    )
    Board findByBoardIdAndNickname(Long board_id, String nickname);

    @Query("select b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.title LIKE %:query% OR b.contents LIKE %:query%"
    )
    List<BoardListDto> findByTitleOrContentsContaining(@Param("query") String query);
}
