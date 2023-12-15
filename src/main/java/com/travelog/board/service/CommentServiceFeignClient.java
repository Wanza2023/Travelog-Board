package com.travelog.board.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "comments")
public interface CommentServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = "/comments/commentSize/{boardId}", consumes = "application/json")
    int getCommentSize(@PathVariable Long boardId);

}
