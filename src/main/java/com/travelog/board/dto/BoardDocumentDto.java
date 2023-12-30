package com.travelog.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class BoardDocumentDto {
    private Long boardId;
    private Long memberId;
    private String nickname;
    private String local;
    private String title;
    private String contents;
    private String summary;
    private String createdAt;
    private int views;
    private int commentSize;
}
