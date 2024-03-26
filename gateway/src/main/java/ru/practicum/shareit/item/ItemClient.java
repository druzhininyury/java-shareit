package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Service
@Validated
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(@Valid ItemDto itemDto, long userId) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> updateItemData(ItemDto itemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByUserId(long userId, @PositiveOrZero long from, @Positive long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllItemsWithText(String text, @PositiveOrZero long from, @Positive long size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        return get("/search?text={text}&from={from}&size={size}", 0L, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, @Valid CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}
