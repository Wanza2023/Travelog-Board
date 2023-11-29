package com.travelog.board.service;

import com.travelog.board.repository.HashtagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class HashtagService {
    @Autowired
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public List<String> getHashtag5(){
        return hashtagRepository.findByHashtagTop5();
    }
}
