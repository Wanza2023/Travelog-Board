package com.travelog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumViewsDto {
    private Long memberId;
    private Long totalViews;
}
