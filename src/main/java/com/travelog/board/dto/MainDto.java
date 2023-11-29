package com.travelog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainDto {
    private List<String> hashtags;
    private List<PopularListDto> popularList;
}
