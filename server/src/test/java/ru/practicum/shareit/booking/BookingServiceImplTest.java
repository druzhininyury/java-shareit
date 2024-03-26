package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.item.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.item.exception.NoSuchItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoId;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addBooking_whenBookingValid_thenBookingSaved() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.addBooking(newBookingDto, bookerId);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_whenDatabaseError_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException("Database error."));

        assertThrows(BookingHasNotSavedException.class,
                () -> bookingService.addBooking(newBookingDto, bookerId));
    }

    @Test
    void addBooking_whenBookingDatesInvalid_thenExceptionThrown() {
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(InvalidStartEndDatesException.class, () -> bookingService.addBooking(newBookingDto,  bookerId));
    }

    @Test
    void addBooking_whenBookerIsInvalid_thenExceptionThrown() {
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> bookingService.addBooking(newBookingDto,  bookerId));
    }

    @Test
    void addBooking_whenItemIsInvalid_thenExceptionThrown() {
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NoSuchItemException.class, () -> bookingService.addBooking(newBookingDto,  bookerId));
    }

    @Test
    void addBooking_whenBookerIsOwner_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 1L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotBookingRelationException.class, () -> bookingService.addBooking(newBookingDto,  bookerId));
    }

    @Test
    void addBooking_whenItemUnavailable_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(false)
                .owner(owner).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemIsNotAvailableException.class, () -> bookingService.addBooking(newBookingDto,  bookerId));
    }

    @Test
    void approveOrRejectBooking_whenInputValid_thenBookingUpdated() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        Booking bookingUpdated = Booking.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.Status.APPROVED).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Booking.Status.APPROVED)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingUpdated);

        BookingDto actualBookingDto = bookingService.approveOrRejectBooking(bookingId, ownerId, true);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveOrRejectBooking_whenDataBaseError_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        Booking bookingUpdated = Booking.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.Status.APPROVED).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Booking.Status.APPROVED)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException("Database error."));

        assertThrows(BookingHasNotSavedException.class,
                () -> bookingService.approveOrRejectBooking(bookingId, ownerId, true));
    }

    @Test
    void approveOrRejectBooking_whenBookingInvalid_thenExceptionThrown() {
        long ownerId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NoSuchBookingException.class,
                () -> bookingService.approveOrRejectBooking(bookingId, ownerId, true));
    }

    @Test
    void approveOrRejectBooking_whenNotOwner_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotBookingRelationException.class,
                () -> bookingService.approveOrRejectBooking(bookingId, bookerId, true));
    }

    @Test
    void approveOrRejectBooking_whenStatusNotWaiting_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.CANCELED).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NoWaitingStatusException.class,
                () -> bookingService.approveOrRejectBooking(bookingId, ownerId, true));
    }

    @Test
    void getBookingById_whenInputValid_thenBookingReturn() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = bookingService.getBookingById(bookingId, ownerId);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void getBookingById_whenBookingInvalid_thenExceptionThrown() {
        long bookingId = 2L;
        long ownerId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NoSuchBookingException.class, () -> bookingService.getBookingById(bookingId, ownerId));
    }

    @Test
    void getBookingById_whenNotOwnerNorBooker_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        long userId = 3L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotBookingRelationException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getAllBookingsByUser_whenInputValid_thenReturnedListOfDto() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 10;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(eq(bookerId), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(bookerId, state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUser_whenStateInvalid_thenExceptionThrown() {
        long bookerId = 2L;
        String state = "SOME";
        long from = 0;
        long size = 10;

        assertThrows(InvalidStateException.class,
                () -> bookingService.getAllBookingsByUser(bookerId, state, from, size));
    }

    @Test
    void getAllBookingsByUser_whenBookerInvalid_thenExceptionThrown() {
        long bookerId = 3L;
        String state = "ALL";
        long from = 0;
        long size = 10;

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class,
                () -> bookingService.getAllBookingsByUser(bookerId, state, from, size));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInputValid_thenReturnedListOfDto() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 10;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerId(eq(ownerId), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenStateInvalid_thenExceptionThrown() {
        long ownerId = 1L;
        String state = "SOME";
        long from = 0;
        long size = 10;

        assertThrows(InvalidStateException.class,
                () -> bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenBookerInvalid_thenExceptionThrown() {
        long ownerId = 3L;
        String state = "ALL";
        long from = 0;
        long size = 10;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class,
                () -> bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size));
    }

}
