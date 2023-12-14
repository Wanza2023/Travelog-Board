package com.travelog.board.controller;

import com.travelog.board.dto.*;
import com.travelog.board.entity.Board;
import com.travelog.board.entity.Comment;
import com.travelog.board.service.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final ScheduleService scheduleService;
    private final HashtagService hashtagService;
    private final CommentServiceFeignClient commentServiceFeignClient;
    private final MemberServiceFeignClient memberServiceFeignClient;

    @Operation(summary = "회원별 전체 조회수")
    @GetMapping(value = "/views")
    public List<SumViewsDto> getSumViews(){
        return boardService.getSumViews();
    }

    // 북마크 게시글 목록
    @Operation(summary = "북마크 게시글 목록 불러오기")
    @PostMapping(value = "/bookmark")
    public List<BookmarkListResDto> getBookmarkBoards(@RequestBody List<Long> boardIds){
        return boardService.getBookmarkBoards(boardIds);
    }

    // 메인화면 조회
    @Operation(summary = "메인 화면 조회")
    @GetMapping
    public ResponseEntity<?> getPopular(){
        List<PopularListDto> popularList = boardService.getPopular();
        List<String> hashtags = hashtagService.getHashtag5();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("인기 해시태그 5개, 인기글 10개")
                .body(new MainDto(hashtags, popularList)).build(),HttpStatus.OK);
    }

    // 전체 게시글 조회
    @Operation(summary = "전체 게시글 목록 조회")
    @GetMapping(value = "/boards")
    public ResponseEntity<?> getAllBoard(HttpServletRequest request){
        String reqHeader = request.getHeader("Authorization");

        List<BoardListDto> boards = boardService.getAllBoardOrderByCreatedAt();
        List<BoardListResDto> res = refactorToResDto(boards, reqHeader);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).body(res).msg("전체 게시글이 조회되었습니다.").build(), HttpStatus.OK);
    }

    //블로그 게시글 목록 조회 OK
    @Operation(summary = "블로그 개인 홈 조회", description = "nickname을 이용해 board 목록을 조회합니다.")
    @GetMapping(value = "/{nickname}")
    public ResponseEntity<?> getBlogHome(HttpServletRequest request, @PathVariable String nickname){
        String reqHeader = request.getHeader("Authorization");

        List<BoardListDto> boards = boardService.getBlogHome(nickname);
        List<BoardListResDto> res = refactorToResDto(boards, reqHeader);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("블로그 게시글 목록이 조회되었습니다.").body(res).build(), HttpStatus.OK);
    }

    //지역별 게시글 조회
    @Operation(summary = "지역별 게시글 목록 조회")
    @GetMapping(value = "/local/{local}")
    public ResponseEntity<?> getLocalSearch(HttpServletRequest request, @PathVariable String local){
        String reqHeader = request.getHeader("Authorization");
        List<BoardListDto> boards = boardService.getLocalSearch(local);
        List<BoardListResDto> res = refactorToResDto(boards, reqHeader);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("지역별 검색 목록이 조회되었습니다.").body(res).build(), HttpStatus.OK);
    }

    // 해당 해시태그의 게시글 목록
    @Operation(summary = "해시태그별 게시글 목록 조회")
    @GetMapping(value = "/tags/{hashtag}")
    public ResponseEntity<?> getBoardsByTag(HttpServletRequest request, @PathVariable String hashtag){
        String reqHeader = request.getHeader("Authorization");
        List<BoardListDto> boards = boardService.getBoardsByTag(hashtag);
        List<BoardListResDto> res = refactorToResDto(boards, reqHeader);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("해시태그 목록이 조회되었습니다.").body(res).build(), HttpStatus.OK);
    }

    // 글 검색
    @Operation(summary = "제목 또는 내용으로 게시글 검색")
    @GetMapping(value = "/search/{query}")
    public ResponseEntity<?> getSearch(HttpServletRequest request, @PathVariable String query){
        String reqHeader = request.getHeader("Authorization");
        List<BoardListDto> boards = boardService.getSearch(query);
        List<BoardListResDto> res = refactorToResDto(boards, reqHeader);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("검색이 완료되었습니다.").body(res).build(), HttpStatus.OK);
    }

    // 글 조회 OK
    @Operation(summary = "게시글 상세 조회")
    @GetMapping(value = "/{nickname}/{boardId}")
    public ResponseEntity<?> getBoard(@PathVariable String nickname, @PathVariable Long boardId, HttpServletRequest request){

        List<Comment> comments = null;
        Boolean bookmarkStatus = false;
        String reqHeader = request.getHeader("Authorization");

        try{
            comments = commentServiceFeignClient.getComments(boardId);
            int size = comments.size();
            boardService.updateCommentSize(new CommentsReqDto(boardId, size));
        } catch (FeignException e){
            log.error("error={}", e.getMessage());
        }

        if (reqHeader != null && reqHeader.startsWith("Bearer")) {
            String token = reqHeader.split(" ")[1];
            BoardBookmarkDto dto = new BoardBookmarkDto(token, boardId);
            try {
                bookmarkStatus = memberServiceFeignClient.isBookmark(dto);
            } catch (FeignException e) {
                log.error("error={}", e.getMessage());
            }
        }

        BoardResDto respDto =  boardService.readBoard(boardId, nickname, comments, bookmarkStatus);

        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("게시글이 조회되었습니다.").body(respDto).build(), HttpStatus.OK);
    }

    @Operation(summary = "댓글 개수 수정")
    // 댓글 개수 수정
    @PostMapping(value = "/comments")
    public int updateCommentSize(@Valid @RequestBody CommentsReqDto commentsReqDto){
        return boardService.updateCommentSize(commentsReqDto);
    }

    @Operation(summary = "게시글 작성")
    //글 생성 + 일정 생성 OK
    @PostMapping(value = "/write")
    public ResponseEntity<?> createBoard(@Valid @RequestBody BoardReqDto boardReqDto){
        Board res = boardService.createBoard(boardReqDto);
        scheduleService.connectSchedule(res);
        return new ResponseEntity<>(CMRespDto.builder().isSuccess(true).msg("게시물 저장완료")
                .body(res.getBoardId()).build(), HttpStatus.OK);
    }

    @Operation(summary = "게시글 삭제")
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

    @Operation(summary = "게시글 수정")
    // 글 수정
    @PutMapping(value = "/write/{boardId}")
    public Board updateBoard(@PathVariable Long boardId, @RequestBody Board board){
        return boardService.updateBoard(boardId, board);
    }

    private List<BoardListResDto> refactorToResDto(List<BoardListDto> boards, String reqHeader){
        List<Long> memberIds = boards.stream().map(BoardListDto::getMemberId).toList();
        boolean bookmarkStatus = false;
        List<MemberInfoDto> members = new ArrayList<>();
        List<Long> bookmarkList = new ArrayList<>();
        MemberInfoDto member;
        List<BoardListResDto> res = new ArrayList<>();

        try {
            // 모든 작성자 정보 받아오기
            members = memberServiceFeignClient.getMemberInfo(memberIds);

            // 로그인한 경우
            if (reqHeader != null && reqHeader.startsWith("Bearer")) {
                String token = reqHeader.split(" ")[1];
                // 사용자의 북마크 리스트 받아오기
                bookmarkList = memberServiceFeignClient.getBookmarkList(token);
            }
        } catch (FeignException e) {
                log.error("error={}", e.getMessage());
        }

        // 작성자 정보 받아왔을 경우
        if (!members.isEmpty()){
            for(BoardListDto board: boards){
                // 작성자들 정보에서 해당 게시글의 작성자와 일치 하는 멤버 찾기
                member = members.stream()
                        .filter(o->o.getMemberId().equals(board.getMemberId()))
                        .findFirst()
                        .orElseThrow(()->new IllegalArgumentException("존재하지 않은 멤버"));
                // 북마크 게시글이 있으면
                if(!bookmarkList.isEmpty()){
                    // 북마크 게시글인지 확인
                    bookmarkStatus = bookmarkList.stream().anyMatch(o->o.equals(board.getBoardId()));
                }
                res.add(new BoardListResDto(board, member, bookmarkStatus));
            }
        }
        else {
            for(BoardListDto board: boards){
                member = new MemberInfoDto(board.getMemberId(), board.getNickname(), null);
                res.add(new BoardListResDto(board, member, bookmarkStatus));
            }
        }

        return res;
    }
}
