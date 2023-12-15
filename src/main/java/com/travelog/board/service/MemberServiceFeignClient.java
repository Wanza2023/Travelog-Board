package com.travelog.board.service;

import com.travelog.board.dto.BoardBookmarkDto;
import com.travelog.board.dto.MemberInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "members")
public interface MemberServiceFeignClient {

    // 게시글이 북마크된 게시글인지 확인
    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/isBookmark")
    Boolean isBookmark(@RequestBody BoardBookmarkDto dto);

    // 회원 프로필 받기
    @RequestMapping(method = RequestMethod.GET, value = "/members/briefInfo/{nickname}")
    MemberInfoDto getMemberInfo(@PathVariable String nickname);

    // 작성자 전체 프로필 받기
    @RequestMapping(method = RequestMethod.POST, value = "/members/briefInfo")
    List<MemberInfoDto> getMembersInfo(@RequestBody List<Long> memberIds);

    // 북마크 리스트 받아오기
    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/bookmarklist")
    List<Long> getBookmarkList(@RequestBody String token);
}
