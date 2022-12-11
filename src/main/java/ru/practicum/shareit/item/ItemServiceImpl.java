package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;


    @Override
    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            if (userRepository.findById(userId.get()).isEmpty()) {
                throw new NoSuchElementException("пользователь не существует");
            }
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(userRepository.findById(userId.get()).get());
            return ItemMapper.toItemDto(itemRepository.save(item), null, null, new ArrayList<>());
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }


    @Override
    public ItemDto updateItem(Optional<Long> userId, Long itemId, ItemDto itemDto) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            Item item = itemRepository.findById(itemId).get();
            log.info("вещь для редактирования:" + item);
            if (item.getOwner().getId() == userId.get()) {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
                itemRepository.save(item);

                Booking lastBooking = bookingLast(item);

                Booking nextBooking = bookingNext(item);

                List<CommentDto> commentsDto = commentDto(item);

                return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto);
            }
            throw new NoSuchElementException("нельзя редактировать чужие вещи!");
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public List<ItemDto> getItems(Optional<Long> userId) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            userRepository.findById(userId.get())
                    .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + userId + " не существует"));

            List<Item> items = itemRepository.findAllByOwnerId(userId.get());

            return getItemDto(items);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public ItemDto getItemOfId(Long userId, Long itemId) throws ValidationException {
        if (userId > 0 && itemId > 0) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NoSuchElementException("вещь c идентификатором " + itemId + " не существует"));

            Booking lastBooking = bookingRepository.findPastOwnerBookings(item.getId(), userId, LocalDateTime.now())
                    .stream()
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookingRepository.findFutureOwnerBookings(item.getId(), userId, LocalDateTime.now())
                    .stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            List<CommentDto> commentsDto = commentRepository.getAllByItemId(itemId).stream()
                    .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                    .collect(Collectors.toList());

            return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            if (text == null || text.length() == 0) return new ArrayList<>();
            List<Item> its = itemRepository.search(text);
            return getItemDto(its);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    private List<ItemDto> getItemDto(List<Item> its) {
        List<ItemDto> list = new ArrayList<>();
        for (Item item : its) {
            Booking lastBooking = bookingLast(item);

            Booking nextBooking = bookingNext(item);

            List<CommentDto> commentsDto = commentDto(item);

            list.add(ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto));
        }
        return list;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, long userId) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("отзыв не может быть пустым");
        }

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (bookingsCount == null || bookingsCount == 0) {
            throw new ValidationException("сначала надо взять эту вещь");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("вещь c идентификатором " + itemId + " не существует"));

        User user = UserMapper.INSTANCE.toUser(userService.findUserById(userId));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment), user);
    }

    public Booking bookingLast(Item item) {
        return bookingRepository.findAllByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    public Booking bookingNext(Item item) {
        return bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(item.getId(), LocalDateTime.now())
                .stream()
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    List<CommentDto> commentDto(Item item) {
        return commentRepository.getAllByItemId(item.getId())
                .stream()
                .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                .collect(Collectors.toList());
    }

}
