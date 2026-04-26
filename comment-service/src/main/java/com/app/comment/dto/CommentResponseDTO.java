package com.app.comment.dto;

import com.app.comment.entity.Comment.CommentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDTO {
    private Long id;
    private Long incidentId;
    private String content;
    private String authorId;
    private String authorName;
    private CommentType commentType;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
