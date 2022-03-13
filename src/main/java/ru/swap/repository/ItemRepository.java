package ru.swap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.swap.model.Item;
import ru.swap.model.Section;
import ru.swap.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllBySection(Section section);

    List<Item> findByUser(User user);

    @Query(value = "select * from item where match(item__index) against(:keywords in natural language mode)", nativeQuery = true)
    List<Item> search(@Param("keywords") String keywords);
}
