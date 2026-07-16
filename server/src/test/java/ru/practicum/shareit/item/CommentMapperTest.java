package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toCommentDto_shouldMapAllFields() {
        User author = new User();
        author.setId(1L);
        author.setName("Иван");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отлично!");
        comment.setAuthor(author);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отлично!");
        assertThat(dto.getAuthorName()).isEqualTo("Иван");
    }

    @Test
    void toComment_shouldSetItemAuthorAndCreatedDate() {
        Item item = new Item();
        item.setId(1L);
        User author = new User();
        author.setId(1L);

        CommentDto dto = CommentDto.builder().text("Отлично!").build();

        Comment comment = CommentMapper.toComment(dto, item, author);

        assertThat(comment.getText()).isEqualTo("Отлично!");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
    }
}