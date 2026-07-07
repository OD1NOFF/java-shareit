package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = getUserOrThrow(ownerId);
        Item created = itemRepository.save(ItemMapper.toItem(itemDto, owner));
        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        getUserOrThrow(ownerId);
        Item existing = getItemOrThrow(itemId);

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException(
                    "Пользователь с id=" + ownerId + " не является владельцем вещи с id=" + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existing.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existing.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existing));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = getItemOrThrow(itemId);
        ItemDto dto = ItemMapper.toItemDto(item);

        // даты бронирования показываем только владельцу
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> lastList = bookingRepository.findLastBookings(itemId, now);
            List<Booking> nextList = bookingRepository.findNextBookings(itemId, now);
            dto.setLastBooking(lastList.isEmpty() ? null : BookingMapper.toBookingShortDto(lastList.get(0)));
            dto.setNextBooking(nextList.isEmpty() ? null : BookingMapper.toBookingShortDto(nextList.get(0)));
        }

        dto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Collection<ItemDto> getAllByOwner(Long ownerId) {
        getUserOrThrow(ownerId);

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookings = bookingRepository.findLastBookingsForItems(itemIds, now).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));
        Map<Long, Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds, now).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findAllByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    dto.setLastBooking(BookingMapper.toBookingShortDto(lastBookings.get(item.getId())));
                    dto.setNextBooking(BookingMapper.toBookingShortDto(nextBookings.get(item.getId())));
                    dto.setComments(commentsByItem.getOrDefault(item.getId(), List.of()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        boolean hasBooked = bookingRepository
                .findFirstByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .isPresent();

        if (!hasBooked) {
            throw new ValidationException(
                    "Пользователь с id=" + userId + " не брал вещь с id=" + itemId + " в аренду");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }
}