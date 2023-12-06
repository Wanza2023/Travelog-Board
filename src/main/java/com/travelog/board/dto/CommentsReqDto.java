package com.travelog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentsReqDto {
    private Long boardId;
    private int commentSize;
}
