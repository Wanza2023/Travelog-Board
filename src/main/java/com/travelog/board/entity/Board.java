package com.travelog.board.entity;

import com.travelog.board.dto.BoardReqDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String local;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private String summary;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<BoardHashtag> hashtags = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private boolean status;
    private int views;
    private int commentSize;

//    public void addSchedule(Schedule schedule) {
//        schedules.add(schedule);
//        schedule.setBoard(this);
//    }

//    public void addHashtag(BoardHashtag boardHashtag) {
//        boardHashtag.setBoard(this);
//        this.hashtags.add(boardHashtag);
//    }

    @Builder
    public Board(Long memberId, String nickname, String local, String title, String contents, String summary,
                 List<Schedule> schedules, boolean status) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.local = local;
        this.title = title;
        this.contents = contents;
        this.summary = summary;
        this.schedules = schedules;
        this.status = status;
    }
    public void updateBoard(BoardReqDto board){
        this.local = board.getLocal();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.summary = board.getSummary();
        this.updatedAt = LocalDateTime.now();
        this.status = board.isStatus();
    }

    public void updateViews(int views){
        this.views = views;
    }

    public void updateCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    //public void setComments(List<Comment> comments) {this.comments = comments;}
}
