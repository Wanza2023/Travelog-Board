package com.travelog.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Table
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String local;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private String summary;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private boolean status;
    private int views;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BoardHashtag> hashtags = new HashSet<BoardHashtag>();

    public void addHashtag(BoardHashtag boardHashtag) {
        this.hashtags.add(boardHashtag);
        boardHashtag.setBoard(this);
    }

    @Builder
    public Board(String nickname, String local, String title, String contents, String summary, boolean status){
        this.nickname = nickname;
        this.local = local;
        this.title = title;
        this.contents = contents;
        this.summary = summary;
        this.status = status;
    }

    public void updateBoard(Board board){
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
}
