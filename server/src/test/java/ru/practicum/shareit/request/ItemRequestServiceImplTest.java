package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.HasNotSavedException;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addItemRequest_whenItemRequestValid_thenItemRequestSaved() {
        long requesterId = 2L;
        ItemRequestDto newItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requester(requesterId)
                .build();
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(LocalDateTime.now()).build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requesterId)
                .items(List.of())
                .created(itemRequest.getCreated())
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto = itemRequestService.addItemRequest(requesterId, newItemRequestDto);

        assertThat(actualItemRequestDto, equalTo(expectedItemRequestDto));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void addItemRequest_whenDatabaseError_thenExceptionThrown() {
        long requesterId = 2L;
        ItemRequestDto newItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requester(requesterId)
                .build();
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(LocalDateTime.now()).build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requesterId)
                .items(List.of())
                .created(itemRequest.getCreated())
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenThrow(new DataIntegrityViolationException("Database error."));

        assertThrows(HasNotSavedException.class,
                () -> itemRequestService.addItemRequest(requesterId, newItemRequestDto));
    }

    @Test
    void addItemRequest_whenRequesterInvalid_thenExceptionThrown() {
        long requesterId = 3L;
        ItemRequestDto newItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requester(requesterId)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> itemRequestService.addItemRequest(requesterId, newItemRequestDto));
    }

    @Test
    void getItemRequestsByOwner_whenInputValid_thenListOfDtoReturn() {
        long ownerId = 1L;
        long requesterId = 2L;
        long requestId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(requesterId)
                .items(List.of(itemDto))
                .created(LocalDateTime.now())
                .build();
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();
        User owner = User.builder().id(ownerId).name("owner").email("owner@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("description")
                .requester(requester)
                .created(expectedItemRequestDto.getCreated())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));

        List<ItemRequestDto> actualList = itemRequestService.getItemRequestsByOwner(requesterId);

        assertThat(actualList, equalTo(List.of(expectedItemRequestDto)));
    }

    @Test
    void getItemRequestsByOwner_whenRequestorInvalid_thenExceptionThrown() {
        long requesterId = 3L;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> itemRequestService.getItemRequestsByOwner(requesterId));
    }

    @Test
    void getItemRequestsAllButOwner_whenInputValid_thenListOfDtoReturned() {
        long ownerId = 1L;
        long requesterId = 2L;
        long userId = 3L;
        long requestId = 2L;
        long itemId = 1L;
        long from = 0;
        long size = 10;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(userId)
                .items(List.of(itemDto))
                .created(LocalDateTime.now())
                .build();
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();
        User owner = User.builder().id(ownerId).name("owner").email("owner@yandex.ru").build();
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("description")
                .requester(user)
                .created(expectedItemRequestDto.getCreated())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdIsNot(eq(requesterId), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));

        List<ItemRequestDto> actualList = itemRequestService.getItemRequestsAllButOwner(requesterId, from, size);

        assertThat(actualList, equalTo(List.of(expectedItemRequestDto)));
    }

    @Test
    void getItemRequestsAllButOwner_whenRequesterInvalid_thenExceptionThrown() {
        long requesterId = 3L;
        long from = 0;
        long size = 10;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class,
                () -> itemRequestService.getItemRequestsAllButOwner(requesterId, from, size));
    }

    @Test
    void getItemRequestById_whenInputValid_thenDtoReturned() {
        long ownerId = 1L;
        long requesterId = 2L;
        long requestId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(requesterId)
                .items(List.of(itemDto))
                .created(LocalDateTime.now())
                .build();
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();
        User owner = User.builder().id(ownerId).name("owner").email("owner@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("description")
                .requester(requester)
                .created(expectedItemRequestDto.getCreated())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(requesterId, requestId);

        assertThat(actualItemRequestDto, equalTo(expectedItemRequestDto));
    }

    @Test
    void getItemRequestById_whenUserInvalid_thenExceptionThrown() {
        long requesterId = 3L;
        long requestId = 1L;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> itemRequestService.getItemRequestById(requesterId, requestId));
    }

    @Test
    void getItemRequestById_whenItemRequestInvalid_thenExceptionThrown() {
        long requesterId = 2L;
        long requestId = 1L;
        User requester = User.builder().id(requesterId).name("requester").email("requester@yandex.ru").build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class,
                () -> itemRequestService.getItemRequestById(requesterId, requestId));
    }
}
