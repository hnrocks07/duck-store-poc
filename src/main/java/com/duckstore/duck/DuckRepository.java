package com.duckstore.duck;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.*;

public interface DuckRepository extends JpaRepository<Duck, Long> {
    List<Duck> findByDeletedFalseOrderByQuantityDescIdAsc();
    Optional<Duck> findFirstByColorAndSizeAndDeletedFalseOrderByPriceAscIdAsc(Color color, DuckSize size);
    boolean existsByColorAndSizeAndPriceAndIdNot(Color color, DuckSize size, BigDecimal price, Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Duck d where d.color=:color and d.size=:size and d.price=:price and d.deleted=:deleted")
    Optional<Duck> lockByIdentity(@Param("color") Color color, @Param("size") DuckSize size, @Param("price") BigDecimal price, @Param("deleted") boolean deleted);
}
