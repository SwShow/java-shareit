package ru.practicum.shareit.item.comment.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.model.Comment;

@Mapper
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDtoLittle commentDto);
}




