package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEqualsTest {

    @Test
    void equals_shouldReturnTrueForSameId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equals_shouldReturnFalseForNullId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        User user = new User();
        user.setId(1L);

        assertThat(user).isEqualTo(user);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        User user = new User();
        user.setId(1L);

        assertThat(user).isNotEqualTo("не пользователь");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        User user = new User();
        user.setId(1L);

        assertThat(user.hashCode()).isEqualTo(User.class.hashCode());
    }
}