package com.travelog.board.controller;

import com.travelog.board.dto.*;
import com.travelog.board.entity.Board;
import com.travelog.board.entity.Comment;
import com.travelog.board.service.BoardService;
import com.travelog.board.service.CommentServiceFeignClient;
import com.travelog.board.service.MemberServiceFeignClient;
import com.travelog.board.service.ScheduleService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private final BoardService boardService;
    @Autowired
    private final ScheduleService scheduleService;
    @Autowired
    private final CommentServiceFeignClient commentServiceFeignClient;
    @Autowired
    private final MemberServiceFeignClient memberServiceFeignClient;

    // 북마크 게시글 목록
    @PostMapping(value = "/bookmark")
    public List<BookmarkListResDto> getBoards(@RequestBody List<Long> boardIds){
        return boardService.getBoards(boardIds);
    }

    // 인기글 조회
    @GetMapping
    public ResponseEntity<?> getPopular(){
        List<PopularListDto> dtos = boardService.getPopular();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("인기글 10개 조회").body(dtos).build(),HttpStatus.OK);
    }

    //블로그 게시글 목록 조회 OK
    @Tag(name = "블로그 홈 조회")
    @Operation(summary = "개인 홈 조회", description = "nickname을 이용해 board 목록을 조회합니다.")
    @GetMapping(value = "/{nickname}")
    public ResponseEntity<?> getBlogHome(@PathVariable String nickname){
        List<BoardListResDto> dtos = boardService.getBlogHome(nickname);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("블로그 게시글 목록이 조회되었습니다.").body(dtos).build(), HttpStatus.OK);
    }

    //지역별 게시글 조회
    @GetMapping(value = "/local/{local}")
    public ResponseEntity<?> getLocalSearch(@PathVariable String local){
        List<BoardListResDto> dtos = boardService.getLocalSearch(local);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("지역별 검색 목록이 조회되었습니다.").body(dtos).build(), HttpStatus.OK);
    }

    // 해당 해시태그의 게시글 목록
    @GetMapping(value = "/tags/{hashtag}")
    public ResponseEntity<?> getBoardsByTag(@PathVariable String hashtag){
        List<BoardListResDto> dtos = boardService.getBoardsByTag(hashtag);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("해시태그 목록이 조회되었습니다.").body(dtos).build(), HttpStatus.OK);
    }

    // 글 검색
    @GetMapping(value = "/search/{query}")
    public ResponseEntity<?> getSearch(@PathVariable String query){
        List<BoardListResDto> dtos = boardService.getSearch(query);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("검색이 완료되었습니다.").body(dtos).build(), HttpStatus.OK);
    }

    // 글 조회 OK
    @GetMapping(value = "/{nickname}/{boardId}")
    public ResponseEntity<?> getBoard(@PathVariable String nickname, @PathVariable Long boardId, HttpServletRequest request){
        List<Comment> comments = null;
        try{
            comments = commentServiceFeignClient.getComments(boardId);
        } catch (FeignException e){
            System.out.println(e.getMessage());
        }

        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        BoardBookmarkDto dto = new BoardBookmarkDto(token, boardId);
        Boolean bookmarkStatus = false;
        try{
            bookmarkStatus = memberServiceFeignClient.isBookmark(dto);
        } catch (FeignException e){
            System.out.println(e.getMessage());
        }

        BoardResDto board =  boardService.readBoard(boardId, nickname, comments, bookmarkStatus);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("게시글이 조회되었습니다.").body(board).build(), HttpStatus.OK);
    }

    //글 생성 + 일정 생성 OK
    @PostMapping(value = "/write")
    public ResponseEntity<?> createBoard(@Valid @RequestBody BoardReqDto boardReqDto){
        Board res = boardService.createBoard(boardReqDto);
        scheduleService.connectSchedule(res);
        return new ResponseEntity<>(CMRespDto.builder().isSuccess(true).msg("게시물 저장완료")
                .body(res.getBoardId()).build(), HttpStatus.OK);
    }

    // 글 삭제 OK
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{nickname}/{boardId}")
    public String deleteBoard(HttpServletRequest request, @PathVariable String nickname, @PathVariable Long boardId){
        boardService.deleteBoard(boardId, nickname);
        String referer = request.getHeader("Referer");
        if(referer == null){
            referer = "/" + nickname;
        }
        return "redirect:" + referer;
    }

    // 글 수정
    @PutMapping(value = "/write/{boardId}")
    public Board updateBoard(@PathVariable Long boardId, @RequestBody Board board){
        return boardService.upadateBoard(boardId, board);
    }
}
