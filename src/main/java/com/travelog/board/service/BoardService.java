package com.travelog.board.service;

import com.travelog.board.dto.*;
import com.travelog.board.entity.Board;
import com.travelog.board.entity.BoardHashtag;
import com.travelog.board.entity.Comment;
import com.travelog.board.entity.Hashtag;
import com.travelog.board.repository.BoardHashtagRepository;
import com.travelog.board.repository.BoardRepository;
import com.travelog.board.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final HashtagRepository hashtagRepository;
    private final BoardHashtagRepository boardHashtagRepository;

    // 회원별 전체 조회수
    @Transactional(readOnly = true)
    public List<SumViewsDto> getSumViews(){
        return boardRepository.findSumViewsDtoJPQL();
    }

    // 북마크 조회
    @Transactional(readOnly = true)
    public List<BookmarkListResDto> getBookmarkBoards(List<Long> boardIds){
        return boardRepository.findByBoardIds(boardIds);
    }

    // 전체 게시글 최신순 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getAllBoardOrderByCreatedAt() {
        return boardRepository.findAllByStatusOrderByCreatedAt();
    }

    // 인기글 조회
    @Transactional(readOnly = true)
    public List<PopularListDto> getPopular(){
        return boardRepository.findTop10();
    }

    //블로그 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getBlogHome(String nickname){
        return boardRepository.findByNickname(nickname);
    }

    //지역별 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getLocalSearch(String local) {
        return boardRepository.findByLocal(local);
    }

    //해시태그 목록
    public List<BoardListDto> getBoardsByTag(String hashtag){
        Optional<Hashtag> tag = hashtagRepository.findByHashtag(hashtag);
        List<BoardListDto> dtos = new ArrayList<>();
        if(tag.isPresent()){
            for(BoardHashtag board: tag.get().getBoards()){
                dtos.add(new BoardListDto(board.getBoard()));
            }
        }
        return dtos;
    }

    //글 검색
    @Transactional(readOnly = true)
    public List<BoardListDto> getSearch(String query){
        return boardRepository.findByTitleOrContentsContaining(query);
    }

    // 게시글 조회(조회수 증가)
    @Transactional
    public BoardResDto readBoard(long id, String nickname, boolean bookmark){
        Board board = boardRepository.findByBoardIdAndNickname(id, nickname);
        board.updateViews(board.getViews()+1);
        return new BoardResDto(board, bookmark);
    }

    // 게시글 댓글 개수 수정
    @Transactional
    public int updateCommentSize(CommentsReqDto commentsReqDto){
        Board board = boardRepository.findById(commentsReqDto.getBoardId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 게시글"));
        board.updateCommentSize(commentsReqDto.getCommentSize());
        return board.getCommentSize();
    }

    // 글 작성 (db 접근이 조금 많이 이루어지는 것 같아 간추려지면 더 좋을 것 같습니당)
    @Transactional
    public Board createBoard(BoardReqDto boardReqDto) {
        Board board = Board.builder()
                .memberId(boardReqDto.getMemberId())
                .nickname(boardReqDto.getNickname())
                .local(boardReqDto.getLocal())
                .title(boardReqDto.getTitle())
                .contents(boardReqDto.getContents())
                .summary(boardReqDto.getSummary())
                .schedules(boardReqDto.getSchedules())
                .status(boardReqDto.isStatus())
                .build();
        Board board1 = boardRepository.save(board);

        // hashtag 테이블에 검색 후 없으면 저장
        // board_hashtag 테이블에 보드 아이디, 해시태그 아이디 저장
        for (String hashtag : boardReqDto.getHashtags()) {
            Hashtag hashtag1 = hashtagRepository.findByHashtag(hashtag)
                    .orElseGet(() -> hashtagRepository.save(Hashtag.builder()
                            .hashtag(hashtag)
                            .build()));

            BoardHashtag boardHashtag = new BoardHashtag(board1, hashtag1);
            board1.getHashtags().add(boardHashtag);
            hashtag1.getBoards().add(boardHashtag);

            boardHashtagRepository.save(boardHashtag);
        }

        return board1;
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(long id, String nickname){
        Board board = boardRepository.findByBoardIdAndNickname(id, nickname);

        //hashtag의 boards에서 해당 게시글 삭제
        for(BoardHashtag boardHashtag : board.getHashtags()){
            Optional<Hashtag> hashtag = hashtagRepository.findByHashtag(boardHashtag.getHashtag().getHashtag());
            hashtag.ifPresent(value -> value.getBoards().remove(boardHashtag));
        }
        boardRepository.delete(board);
    }

    // 글 수정( 수정 필요 )
    @Transactional
    public Board updateBoard(long id, BoardReqDto boardReqDto){
        Board boardB = boardRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("해당 게시글이 존재하지 않습니다."));
        boardB.updateBoard(boardReqDto);

        return boardB;
    }

    //월별 작성 게시글
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPostPerMonth(Long memberId){
         return boardRepository.findPostPerMonth(memberId);
    }

    // 조회수 높은 게시글 5개
    @Transactional(readOnly = true)
    public List<BoardListDto> getTop5(Long memberId){
        return boardRepository.findTop5ByMemberIdOrderByViewsDesc(memberId);
    }
}
