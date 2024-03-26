package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllContainingTextTest() {
        User user = User.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        userRepository.save(user);
        Item item1 = Item.builder()
                .name("item1")
                .description("description item1")
                .available(true)
                .owner(user)
                .build();
        Item item2 = Item.builder()
                .name("item2")
                .description("description item12")
                .available(true)
                .owner(user)
                .build();
        Item item3 = Item.builder()
                .name("item3")
                .description("description item3")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> items = itemRepository.findAllContainingText("TEM1", PageRequest.of(0, 10));
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), equalTo("item1"));
        assertThat(items.get(1).getName(), equalTo("item2"));
    }

}
