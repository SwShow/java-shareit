package ru.practicum.shareit.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shareit.booking.model.Booking;
import ru.practicum.shareit.shareit.booking.BookingRepository;
import ru.practicum.shareit.shareit.exception.BadRequestException;
import ru.practicum.shareit.shareit.exception.NotFoundException;
import ru.practicum.shareit.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.shareit.item.comment.model.Comment;
import ru.practicum.shareit.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.shareit.item.model.Item;
import ru.practicum.shareit.shareit.request.model.ItemRequest;
import ru.practicum.shareit.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.shareit.user.UserRepository;
import ru.practicum.shareit.shareit.user.UserService;
import ru.practicum.shareit.shareit.user.dto.UserMapper;
import ru.practicum.shareit.shareit.user.model.User;

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
    public ItemDto createItem(ItemDto itemDto, long userId) {
        long requestId = itemDto.getRequestId();
        log.info("requestId:" + requestId);
        ItemRequest request = null;
        if (requestId != 0L) {
            request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("запрос c идентификатором " + requestId + " не существует"));
        }
        log.info("request:" + request);
            Item item = itemMapper.toItem(itemDto);
            log.info("item:" + item);
            item.setOwner(userRepository.findById(userId).get());
            item.setRequest(request);
            Item item1 = itemRepository.save(item);
            log.info("item:" + item1);
            return itemMapper.toItemDto(item1);

    }

    @Override
    public ItemDto updateItem(long userId, Long itemId, ItemDto itemDto) {
            Item item = itemRepository.findById(itemId).get();
            log.info("вещь для редактирования:" + item);
            if (item.getOwner().getId() == userId) {
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
            throw new NotFoundException("нельзя редактировать чужие вещи!");
    }

    @Override
    public List<ItemDto> getItems(long userId)  {
            userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + userId + " не существует"));

            List<Item> items = itemRepository.findAllByOwnerId(userId);

            return getItemDto(items);
    }

    @Override
    public ItemDto getItemOfId(Long userId, Long itemId) {

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

    @Override
    public List<ItemDto> getItemOfText(long userId, String text) {

            if (text == null || text.length() == 0) return new ArrayList<>();
            List<Item> its = itemRepository.search(text);
            return getItemDto(its);

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
    public CommentDto createComment(CommentDtoLittle commentDtoLittle, long itemId, long userId) {
        if (commentDtoLittle.getText() == null || commentDtoLittle.getText().equals("")) {
            throw new BadRequestException("отзыв не может быть пустым");
        }

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (bookingsCount == null || bookingsCount == 0) {
            throw new BadRequestException("сначала надо взять эту вещь");
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
