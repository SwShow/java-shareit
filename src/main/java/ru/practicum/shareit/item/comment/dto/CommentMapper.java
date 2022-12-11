package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getText()
        );
    }

    public static CommentDto toCommentDto(Comment comment, User user) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated()
        );
    }

}
