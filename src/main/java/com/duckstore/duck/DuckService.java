package com.duckstore.duck;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DuckService {
    private final DuckRepository ducks;
    public DuckService(DuckRepository ducks) { this.ducks = ducks; }

    public List<DuckResponse> list() { return ducks.findByDeletedFalseOrderByQuantityDescIdAsc().stream().map(DuckResponse::from).toList(); }

    /** Synchronized is intentionally sufficient for this single-instance POC; the DB unique key is a second guard. */
    @Transactional
    public synchronized DuckResponse add(DuckRequest request) {
        var active = ducks.lockByIdentity(request.color(), request.size(), request.price(), false);
        if (active.isPresent()) { active.get().addQuantity(request.quantity()); return DuckResponse.from(active.get()); }
        // Chosen ambiguity: an identical deleted duck is restored rather than inserting a second historical row.
        var deleted = ducks.lockByIdentity(request.color(), request.size(), request.price(), true);
        if (deleted.isPresent()) { deleted.get().restore(request.quantity()); return DuckResponse.from(deleted.get()); }
        return DuckResponse.from(ducks.save(new Duck(request.color(), request.size(), request.price(), request.quantity())));
    }
    @Transactional
    public DuckResponse update(long id, DuckUpdateRequest request) {
        Duck duck = active(id);
        duck.update(request.price(), request.quantity());
        return DuckResponse.from(duck);
    }
    @Transactional public void delete(long id) { active(id).delete(); }
    public Duck cheapestActive(Color color, DuckSize size) {
        return ducks.findFirstByColorAndSizeAndDeletedFalseOrderByPriceAscIdAsc(color, size)
            .orElseThrow(() -> new NotFoundException("No active duck exists for " + color + " / " + size + "."));
    }
    private Duck active(long id) { return ducks.findById(id).filter(d -> !d.isDeleted()).orElseThrow(() -> new NotFoundException("Active duck " + id + " was not found.")); }
}
