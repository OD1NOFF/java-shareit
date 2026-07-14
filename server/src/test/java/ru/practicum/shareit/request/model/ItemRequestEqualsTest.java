package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestEqualsTest {

    @Test
    void equals_shouldReturnTrueForSameId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void equals_shouldReturnFalseForNullId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request).isEqualTo(request);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request).isNotEqualTo("не запрос");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request.hashCode()).isEqualTo(ItemRequest.class.hashCode());
    }
}