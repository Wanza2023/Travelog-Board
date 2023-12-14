package com.travelog.board.dto;

import com.travelog.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResDto {
    private Long boardId;
    private String nickname;
    private String pfp;
    private String local;
    private String title;
    private String contents;
    private String summary;
    private List<ScheduleDto> schedules;
    private List<String> hashtags;
    private LocalDateTime createdAt;
    private int views;
    private int commentSize;
    private boolean bookmarkStatus;

    public BoardListResDto(BoardListDto board, MemberInfoDto member, boolean bookmarkStatus){
        this.boardId = board.getBoardId();
        this.nickname = member.getNickName();
        this.pfp = member.getPfp();
        this.local = board.getLocal();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.summary = board.getSummary();
        this.schedules = board.getSchedules();
        this.hashtags = board.getHashtags();
        this.createdAt = board.getCreatedAt();
        this.views = board.getViews();
        this.commentSize =  board.getCommentSize();
        this.bookmarkStatus = bookmarkStatus;
    }

}
