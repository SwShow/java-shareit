package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
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
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) throws ValidationException {
        Optional<Long> requestId = Optional.ofNullable(itemDto.getRequestId());
        log.info("requestId:" + requestId);
        ItemRequest request = null;

        if (requestId.isPresent() && requestId.get() > 0L) {
            request = itemRequestRepository.findById(requestId.get())
                    .orElseThrow(() -> new NoSuchElementException("запрос c идентификатором " + requestId + " не существует"));
        }

        log.info("request:" + request);
        if (userId.isPresent() && userId.get() > 0) {
            if (userRepository.findById(userId.get()).isEmpty()) {
                throw new NoSuchElementException("пользователь не существует");
            }
            Item item = itemMapper.toItem(itemDto);
            log.info("item:" + item);
            item.setOwner(userRepository.findById(userId.get()).get());
            item.setRequest(request);
            Item item1 = itemRepository.save(item);
            log.info("item:" + item1);
            return itemMapper.toItemDto(item1);
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
                item.setLastBooking(lastBooking);
                item.setNextBooking(nextBooking);
                item.setComments(commentsDto);
                return itemMapper.toItemDto(item);
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

            List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId.get());

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

            List<CommentDto> commentsDto = commentDto(item);
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
            item.setComments(commentsDto);
            return itemMapper.toItemDto(item);
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
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
            item.setComments(commentsDto);
            list.add(itemMapper.toItemDto(item));
        }
        return list;
    }

    @Override
    public CommentDto createComment(CommentDtoLittle commentDtoLittle, Long itemId, long userId) {
        if (commentDtoLittle.getText() == null || commentDtoLittle.getText().equals("")) {
            throw new ValidationException("отзыв не может быть пустым");
        }

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (bookingsCount == null || bookingsCount == 0) {
            throw new ValidationException("сначала надо взять эту вещь");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("вещь c идентификатором " + itemId + " не существует"));
        User user = UserMapper.INSTANCE.toUser(userService.findUserById(userId));
        Comment comment = commentMapper.toComment(commentDtoLittle);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
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

    public List<CommentDto> commentDto(Item item) {
        return commentRepository.getAllByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

}
