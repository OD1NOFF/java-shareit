package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long itemId);

    Collection<ItemDto> getAllByOwner(Long ownerId);

    Collection<ItemDto> search(String text);
}