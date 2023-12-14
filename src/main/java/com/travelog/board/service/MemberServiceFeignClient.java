package com.travelog.board.service;

import com.travelog.board.dto.BoardBookmarkDto;
import com.travelog.board.dto.MemberInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "members")
public interface MemberServiceFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/isBookmark")
    Boolean isBookmark(@RequestBody BoardBookmarkDto dto);

    @RequestMapping(method = RequestMethod.POST, value = "/members/briefInfo")
    List<MemberInfoDto> getMemberInfo(@RequestBody List<Long> memberIds);

    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/bookmarklist")
    List<Long> getBookmarkList(@RequestBody String token);
}
