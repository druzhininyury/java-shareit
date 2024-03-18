package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    @Autowired
    private final ItemServiceImpl itemService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;
    private Comment comment;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("user2@yandex.ru").build());
        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("description")
                .requester(user2)
                .created(LocalDateTime.of(2024, 01, 01, 12, 0))
                .build());
        item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());
        item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(user1)
                .build());
        comment = commentRepository.save(Comment.builder().text("comment").item(item1).author(user2)
                .created(LocalDateTime.of(2024, 03, 01, 12, 0)).build());
        lastBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.of(2024, 02, 01, 12, 0))
                .end(LocalDateTime.of(2024, 02, 02, 12, 0))
                .item(item1).booker(user2).status(Booking.Status.APPROVED).build());
        nextBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1).booker(user2).status(Booking.Status.APPROVED).build());
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addItem_whenItemValid_thenItemSaved() {
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        ItemDto actualItemDto = itemService.addItem(newItemDto, user1.getId());
        expectedItemDto.setId(actualItemDto.getId());

        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void addItem_whenNameIsEmpty_thenExceptionThrown() {
        ItemDto newItemDto = ItemDto.builder()
                .name("")
                .description("description")
                .available(true)
                .build();

        assertThrows(ConstraintViolationException.class, () -> itemService.addItem(newItemDto, user1.getId()));
    }

    @Test
    void addItem_whenDescriptionIsEmpty_thenExceptionThrown() {
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("")
                .available(true)
                .build();

        assertThrows(ConstraintViolationException.class, () -> itemService.addItem(newItemDto, user1.getId()));
    }

    @Test
    void addItem_whenAvailableIsNull_thenExceptionThrown() {
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .build();

        assertThrows(ConstraintViolationException.class, () -> itemService.addItem(newItemDto, user1.getId()));
    }

    @Test
    void updateItemData_whenItemValid_thenItemUpdated() {
        ItemDto toUpdateItemDto = ItemDto.builder()
                .name("item1-updated")
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .name("item1-updated")
                .description("description1")
                .available(true)
                .build();

        ItemDto actualItemDto = itemService.updateItemData(toUpdateItemDto, item1.getId(), user1.getId());
        expectedItemDto.setId(actualItemDto.getId());

        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void getItemById_whenInputValid_thenReturnItemDto() {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(item1.getId())
                .name("item1")
                .description("description1")
                .available(true)
                .comments(List.of(CommentDto.builder()
                        .id(comment.getId())
                        .text("comment")
                        .authorName("user2")
                        .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                        .build()))
                .lastBooking(BookingDtoItem.builder()
                        .id(lastBooking.getId())
                        .bookerId(user2.getId())
                        .start(lastBooking.getStart())
                        .end(lastBooking.getEnd())
                        .build())
                .nextBooking(BookingDtoItem.builder()
                        .id(nextBooking.getId())
                        .bookerId(user2.getId())
                        .start(nextBooking.getStart())
                        .end(nextBooking.getEnd())
                        .build())
                .build();

        ItemDto actualItemDto = itemService.getItemById(user1.getId(), item1.getId());

        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void getAllItemsByUserId_whenInputValid_thenReturnListOfDto() {
        long from = 0;
        long size = 10;
        ItemDto expectedItemDto1 = ItemDto.builder()
                .id(item1.getId())
                .name("item1")
                .description("description1")
                .available(true)
                .comments(List.of(CommentDto.builder()
                        .id(comment.getId())
                        .text("comment")
                        .authorName("user2")
                        .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                        .build()))
                .lastBooking(BookingDtoItem.builder()
                        .id(lastBooking.getId())
                        .bookerId(user2.getId())
                        .start(lastBooking.getStart())
                        .end(lastBooking.getEnd())
                        .build())
                .nextBooking(BookingDtoItem.builder()
                        .id(nextBooking.getId())
                        .bookerId(user2.getId())
                        .start(nextBooking.getStart())
                        .end(nextBooking.getEnd())
                        .build())
                .build();
        ItemDto expectedItemDto2 = ItemDto.builder()
                .id(item2.getId())
                .name("item2")
                .description("description2")
                .available(true)
                .comments(List.of())
                .build();

        List<ItemDto> actualList = itemService.getAllItemsByUserId(user1.getId(), from, size);

        assertThat(actualList, equalTo(List.of(expectedItemDto1, expectedItemDto2)));
    }

    @Test
    void getAllItemsByUserId_whenFromIsNegative_thenExceptionThrown() {
        long from = -1;
        long size = 10;

        assertThrows(ConstraintViolationException.class,
                () -> itemService.getAllItemsByUserId(user1.getId(), from, size));
    }

    @Test
    void getAllItemsByUserId_whenSizeIsZero_thenExceptionThrown() {
        long from = 0;
        long size = 0;

        assertThrows(ConstraintViolationException.class,
                () -> itemService.getAllItemsByUserId(user1.getId(), from, size));
    }

    @Test
    void getAllItemsWithText_whenInputValid_thenReturnListOfDto() {
        long from = 0;
        long size = 10;
        String searchString = "TION2";
        ItemDto expectedItemDto = ItemDto.builder()
                .id(item2.getId())
                .name("item2")
                .description("description2")
                .available(true)
                .build();

        List<ItemDto> actualList = itemService.getAllItemsWithText(searchString, from, size);

        assertThat(actualList, equalTo(List.of(expectedItemDto)));
    }

    @Test
    void getAllItemsWithText_whenFromIsNegative_thenExceptionThrown() {
        long from = -1;
        long size = 10;
        String searchString = "TION2";

        assertThrows(ConstraintViolationException.class,
                () -> itemService.getAllItemsWithText(searchString, from, size));
    }

    @Test
    void getAllItemsWithText_whenSizeIsZero_thenExceptionThrown() {
        long from = 0;
        long size = 0;
        String searchString = "TION2";

        assertThrows(ConstraintViolationException.class,
                () -> itemService.getAllItemsWithText(searchString, from, size));
    }

    @Test
    void addComment_whenCommentValid_thenCommentSaved() {
        CommentDto newCommentDto = CommentDto.builder().text("new comment").build();
        CommentDto expectedCommentDto = CommentDto.builder()
                .text("new comment").authorName("user2").build();

        CommentDto actualCommentDto = itemService.addComment(user2.getId(), item1.getId(), newCommentDto);
        expectedCommentDto.setId(actualCommentDto.getId());
        expectedCommentDto.setCreated(actualCommentDto.getCreated());

        assertThat(actualCommentDto, equalTo(expectedCommentDto));
    }

    @Test
    void addComment_whenTextIsBlank_thenExceptionThrown() {
        CommentDto newCommentDto = CommentDto.builder().text("").build();

        assertThrows(ConstraintViolationException.class,
                () -> itemService.addComment(user2.getId(), item1.getId(), newCommentDto));
    }
}
