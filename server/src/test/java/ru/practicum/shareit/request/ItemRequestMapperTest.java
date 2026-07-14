package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_shouldSetRequestorAndCreatedDate() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequestDto dto = ItemRequestDto.builder().description("Нужна дрель").build();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto, requestor);

        assertThat(itemRequest.getDescription()).isEqualTo("Нужна дрель");
        assertThat(itemRequest.getRequestor()).isEqualTo(requestor);
        assertThat(itemRequest.getCreated()).isNotNull();
    }

    @Test
    void toResponseDto_shouldMapWithEmptyAnswers() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponseDto dto = ItemRequestMapper.toResponseDto(itemRequest, null);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toResponseDto_shouldMapAnswersWithOwnerInfo() {
        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwner(owner);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponseDto dto = ItemRequestMapper.toResponseDto(itemRequest, List.of(item));

        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(2L);
    }
}