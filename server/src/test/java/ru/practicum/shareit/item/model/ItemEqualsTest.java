package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemEqualsTest {

    @Test
    void equals_shouldReturnTrueForSameId() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void equals_shouldReturnFalseForNullId() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        Item item = new Item();
        item.setId(1L);

        assertThat(item).isEqualTo(item);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        Item item = new Item();
        item.setId(1L);

        assertThat(item).isNotEqualTo("не вещь");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Item item = new Item();
        item.setId(1L);

        assertThat(item.hashCode()).isEqualTo(Item.class.hashCode());
    }
}