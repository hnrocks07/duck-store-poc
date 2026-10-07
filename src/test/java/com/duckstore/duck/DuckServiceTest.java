package com.duckstore.duck;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DuckServiceTest {
    @Autowired DuckService service;
    @Autowired DuckRepository repository;
    @BeforeEach void clean() { repository.deleteAll(); }
    private DuckRequest red(int quantity) { return new DuckRequest(Color.Red, DuckSize.Large, new BigDecimal("12.50"), quantity); }
    @Test void same_identity_merges_quantity() {
        service.add(red(3)); service.add(red(4));
        var ducks = service.list(); assertEquals(1, ducks.size()); assertEquals(7, ducks.getFirst().quantity());
    }
    @RepeatedTest(10) void concurrent_matching_adds_preserve_single_row_and_every_unit() throws Exception {
        var start = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(10);
        try {
            var futures = java.util.stream.IntStream.rangeClosed(1, 10)
                .mapToObj(quantity -> pool.submit(() -> { start.await(); return service.add(red(quantity)); }))
                .toList();
            start.countDown();
            for (var future : futures) future.get();
        } finally { pool.shutdownNow(); }
        var ducks=service.list(); assertEquals(1, ducks.size()); assertEquals(55, ducks.getFirst().quantity());
    }
    @Test void logical_delete_hides_duck_and_same_add_restores_it() {
        var duck=service.add(red(2)); service.delete(duck.id()); assertTrue(service.list().isEmpty());
        var restored=service.add(red(1)); assertEquals(duck.id(), restored.id()); assertEquals(3, restored.quantity());
    }
    @Test void update_cannot_change_identity_because_its_request_has_no_identity_fields() {
        var duck=service.add(red(2)); var changed=service.update(duck.id(), new DuckUpdateRequest(new BigDecimal("20.00"), 5));
        assertEquals(Color.Red, changed.color()); assertEquals(DuckSize.Large, changed.size()); assertEquals(new BigDecimal("20.00"), changed.price());
    }
}
