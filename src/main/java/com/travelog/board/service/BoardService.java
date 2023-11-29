package com.travelog.board.service;

import com.travelog.board.dto.*;
import com.travelog.board.entity.Board;
import com.travelog.board.entity.BoardHashtag;
import com.travelog.board.entity.Comment;
import com.travelog.board.entity.Hashtag;
import com.travelog.board.repository.BoardHashtagRepository;
import com.travelog.board.repository.BoardRepository;
import com.travelog.board.repository.HashtagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@AllArgsConstructor
@Service
public class BoardService {
    @Autowired
    private final BoardRepository boardRepository;
    @Autowired
    private final HashtagRepository hashtagRepository;
    @Autowired
    private final BoardHashtagRepository boardHashtagRepository;

    // 북마크 조회
    @Transactional(readOnly = true)
    public List<BookmarkListResDto> getBoards(List<Long> boardIds){
        return boardRepository.findByBoardIds(boardIds);
    }

    // 인기글 조회
    @Transactional(readOnly = true)
    public List<PopularListDto> getPopular(){
        return boardRepository.findTop10();
    }

    //블로그 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardListResDto> getBlogHome(String nickname){
        return boardRepository.findByNickname(nickname);
    }

    //지역별 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardListResDto> getLocalSearch(String local) {
        return boardRepository.findByLocal(local);
    }

    //해시태그 목록
    public List<BoardListResDto> getBoardsByTag(String hashtag){
        Hashtag tag = hashtagRepository.findByHashtag(hashtag)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해시태그입니다."));
        List<BoardListResDto> dtos = new ArrayList<>();
        for(BoardHashtag board: tag.getBoards()){
            dtos.add(new BoardListResDto(board.getBoard()));
        }
        return dtos;
    }

    //글 검색
    @Transactional(readOnly = true)
    public List<BoardListResDto> getSearch(String query){
        return boardRepository.findByTitleOrContentsContaining(query);
    }

    // 게시글 조회(조회수 증가)
    @Transactional
    public BoardResDto readBoard(long id, String nickname, List<Comment> comments, boolean bookmark){
        Board board = boardRepository.findByBoardIdAndNickname(id, nickname);
        System.out.println(board);
        board.updateViews(board.getViews()+1);
        // 해시태그 가져오기
//        List<String> hashtag = new ArrayList<>();
//        for(BoardHashtag boardHashtag:board.getHashtags()){
//            hashtag.add(boardHashtag.getHashtag().getHashtag());
//        }
//        List<Comment> comments = null;
//        try{
//            comments = commentServiceFeignClient.getComments(id);
//        } catch (FeignException e){
//            System.out.println(e.getMessage());
//        }
//        return new BoardResDto(board, comments);
        return new BoardResDto(board, comments, bookmark);
    }

    // 글 작성 (db 접근이 조금 많이 이루어지는 것 같아 간추려지면 더 좋을 것 같습니당)
    @Transactional
    public Board createBoard(BoardReqDto boardReqDto) {
        Board board = Board.builder()
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
    public Board upadateBoard(long id, Board boardA){
        Board boardB = boardRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("해당 게시글이 존재하지 않습니다."));
        boardB.updateBoard(boardA);

        return boardB;
    }
}
