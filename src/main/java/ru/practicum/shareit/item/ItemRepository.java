package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId);

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (upper(item.name) like upper(concat('%', :text, '%')) " +
            "or upper(item.description) like upper(concat('%', :text, '%')))")
    List<Item> findAllContainingText(@Param("text") String text);

    List<Item> findAllByRequestIdIn(List<Long> itemRequestIds);

    List<Item> findAllByRequestId(long requestId);

}
