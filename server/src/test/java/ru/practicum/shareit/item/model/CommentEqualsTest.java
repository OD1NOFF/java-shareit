package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentEqualsTest {

    @Test
    void equals_shouldReturnTrueForSameId() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertThat(comment1).isEqualTo(comment2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(2L);

        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void equals_shouldReturnFalseForNullId() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();

        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment).isEqualTo(comment);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment).isNotEqualTo("не комментарий");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment.hashCode()).isEqualTo(Comment.class.hashCode());
    }
}