package com.travelog.board.dto;

import lombok.Data;

@Data
public class CommentsReqDto {
    private Long boardId;
    private int commentSize;
}
