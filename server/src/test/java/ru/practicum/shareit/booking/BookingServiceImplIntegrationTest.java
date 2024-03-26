package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoId;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    @Autowired
    private final BookingServiceImpl bookingService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private User user3;
    private Item item;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("user2@yandex.ru").build());
        user3 = userRepository.save(User.builder().name("user3").email("user3@yandex.ru").build());
        item = itemRepository.save(Item.builder()
                .name("item")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());
        booking1 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Booking.Status.APPROVED).build());
        booking2 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user3).status(Booking.Status.WAITING).build());
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addBooking_whenBookingValid_thenBookingSaved() {
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.addBooking(newBookingDto, user2.getId());
        expectedBookingDto.setId(actualBookingDto.getId());

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

//    @Test
//    void addBooking_whenItemIdNull_thenExceptionThrown() {
//        NewBookingDto newBookingDto = NewBookingDto.builder()
//                .start(LocalDateTime.now().plusDays(1))
//                .end(LocalDateTime.now().plusDays(2))
//                .build();
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.addBooking(newBookingDto, user2.getId()));
//    }

//    @Test
//    void addBooking_whenStartNull_thenExceptionThrown() {
//        NewBookingDto newBookingDto = NewBookingDto.builder()
//                .itemId(item.getId())
//                .end(LocalDateTime.now().plusDays(2))
//                .build();
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.addBooking(newBookingDto, user2.getId()));
//    }

//    @Test
//    void addBooking_whenStartInPast_thenExceptionThrown() {
//        NewBookingDto newBookingDto = NewBookingDto.builder()
//                .itemId(item.getId())
//                .start(LocalDateTime.now().minusDays(1))
//                .end(LocalDateTime.now().plusDays(2))
//                .build();
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.addBooking(newBookingDto, user2.getId()));
//    }

//    @Test
//    void addBooking_whenEndNull_thenExceptionThrown() {
//        NewBookingDto newBookingDto = NewBookingDto.builder()
//                .itemId(item.getId())
//                .start(LocalDateTime.now().plusDays(1))
//                .build();
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.addBooking(newBookingDto, user2.getId()));
//    }

//    @Test
//    void addBooking_whenEndInPast_thenExceptionThrown() {
//        NewBookingDto newBookingDto = NewBookingDto.builder()
//                .itemId(item.getId())
//                .start(LocalDateTime.now().minusDays(2))
//                .end(LocalDateTime.now().plusDays(1))
//                .build();
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.addBooking(newBookingDto, user2.getId()));
//    }

    @Test
    void approveOrRejectBooking_whenInputValid_thenBookingUpdated() {
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(Booking.Status.APPROVED)
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user3.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.approveOrRejectBooking(
                booking2.getId(), user1.getId(), true);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void getBookingById_whenInputValid_thenBookingReturn() {
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.getBookingById(booking1.getId(), user2.getId());

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void getAllBookingsByUser_whenStateAll_thenListOfDtoReturned() {
        String state = "ALL";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStatePast_thenListOfDtoReturned() {
        String state = "PAST";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStateFuture_thenListOfDtoReturned() {
        String state = "FUTURE";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user2).status(Booking.Status.WAITING).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStateCurrent_thenListOfDtoReturned() {
        String state = "CURRENT";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user2).status(Booking.Status.APPROVED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStateWaiting_thenListOfDtoReturned() {
        String state = "WAITING";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Booking.Status.WAITING).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStateRejected_thenListOfDtoReturned() {
        String state = "REJECTED";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Booking.Status.REJECTED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

//    @Test
//    void getAllBookingsByUser_whenFromNegative_thenExceptionThrown() {
//        String state = "ALL";
//        long from = -1;
//        long size = 10;
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.getAllBookingsByUser(user2.getId(), state, from, size));
//    }

//    @Test
//    void getAllBookingsByUser_whenSizeNegative_thenExceptionThrown() {
//        String state = "ALL";
//        long from = 0;
//        long size = -10;
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.getAllBookingsByUser(user2.getId(), state, from, size));
//    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateAll_thenListOfDtoReturned() {
        String state = "ALL";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto1 = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();
        BookingDto expectedBookingDto2 = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user3.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto2, expectedBookingDto1)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStatePast_thenListOfDtoReturned() {
        String state = "PAST";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateFuture_thenListOfDtoReturned() {
        String state = "FUTURE";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user3.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateCurrent_thenListOfDtoReturned() {
        String state = "CURRENT";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user2).status(Booking.Status.APPROVED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateWaiting_thenListOfDtoReturned() {
        String state = "WAITING";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user3.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateRejected_thenListOfDtoReturned() {
        String state = "REJECTED";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Booking.Status.REJECTED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

//    @Test
//    void getAllBookingsAllItemsByOwner_whenFromNegative_thenExceptionThrown() {
//        String state = "ALL";
//        long from = -1;
//        long size = 10;
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size));
//    }

//    @Test
//    void getAllBookingsAllItemsByOwner_whenSizeNegative_thenExceptionThrown() {
//        String state = "ALL";
//        long from = 0;
//        long size = -10;
//
//        assertThrows(ConstraintViolationException.class,
//                () -> bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size));
//    }

}
