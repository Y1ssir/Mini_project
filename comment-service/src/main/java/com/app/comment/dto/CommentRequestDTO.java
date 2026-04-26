package com.app.comment.dto;

import com.app.comment.entity.Comment.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {

    @NotNull
    private Long incidentId;

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;

    private String attachmentUrl;

    // Le type sera déduit du rôle JWT si absent
    private CommentType commentType;
}
