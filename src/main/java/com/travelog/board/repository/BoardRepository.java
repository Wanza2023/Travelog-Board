package com.travelog.board.repository;

import com.travelog.board.dto.*;
import com.travelog.board.entity.Board;
import com.travelog.board.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BoardRepository  extends JpaRepository<Board, Long> {
    // 회원 별 전체 조회수 조회
    @Query("select " +
            "new com.travelog.board.dto.SumViewsDto(b.memberId, sum(b.views)) " +
            "from Board b group by b.memberId")
    List<SumViewsDto> findSumViewsDtoJPQL();

    // 북마크 게시글 조회
    @Query("select b from Board b where b.boardId in :boardIds")
    List<BookmarkListResDto> findByBoardIds(List<Long> boardIds);

    // 전체 최신순 게시글 조회
    @Query("select b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.status = true order by b.createdAt desc"
    )
    List<BoardListDto> findAllByStatusOrderByCreatedAt();

    // 전체 조회순 게시글 조회
    @Query("select b from Board b " +
            "left join fetch b.hashtags bh " +
            "left join fetch bh.hashtag " +
            "where b.status = true order by b.views desc"
    )
    List<BoardListDto> findAllByStatusOrderByViews();

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
            "where b.local = :local and b.status = true order by b.createdAt desc"
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
            "where (b.title LIKE %:query% OR b.contents LIKE %:query%) and b.status = true"
    )
    List<BoardListDto> findByTitleOrContentsContaining(@Param("query") String query);

    // 월별 작성 개수
    @Query("select function('DATE_FORMAT', b.createdAt, '%Y-%m') as date, count(*) as count " +
            "from Board b where b.memberId = :memberId group by date")
    List<Map<String, Object>> findPostPerMonth(Long memberId);

    //  개인별 조회수 높은 5개
    @Query("select b from Board b where b.memberId = :memberId Order by b.views desc limit 5")
    List<BoardListDto> findTop5ByMemberIdOrderByViewsDesc(Long memberId);
}
