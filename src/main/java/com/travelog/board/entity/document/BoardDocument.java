package com.travelog.board.entity.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelog.board.entity.BoardHashtag;
import com.travelog.board.entity.Schedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "sourcedb2.board.board")
@Mapping(mappingPath = "elastic/comment-mapping.json")
@JsonIgnoreProperties(ignoreUnknown=true)
public class BoardDocument {
    @Id
    @Field(name = "board_id", type = FieldType.Long)
    private Long board_id;
    @Field(name = "nickname", type = FieldType.Text)
    private String nickname;
    @Field(name = "member_id", type = FieldType.Long)
    private Long member_id;
    @Field(name = "local", type = FieldType.Text)
    private String local;
    @Field(name = "title", type = FieldType.Text)
    private String title;
    @Field(name = "contents", type = FieldType.Text)
    private String contents;
    @Field(name = "summary", type = FieldType.Text)
    private String summary;

    @Field(name = "created_at", type = FieldType.Long)
    private long created_at;
    @Field(name = "updated_at", type = FieldType.Long)
    private long updated_at;
    @Field(name = "status", type = FieldType.Boolean)
    private boolean status;
    @Field(name = "views", type = FieldType.Integer)
    private int views;
    @Field(name = "comment_size", type = FieldType.Integer)
    private int comment_size;
}