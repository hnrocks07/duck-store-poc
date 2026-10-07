package com.duckstore.duck;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/ducks")
public class DuckController {
    private final DuckService service;
    public DuckController(DuckService service) { this.service=service; }
    @GetMapping public List<DuckResponse> list() { return service.list(); }
    @PostMapping public ResponseEntity<DuckResponse> add(@Valid @RequestBody DuckRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request)); }
    @PutMapping("/{id}") public DuckResponse update(@PathVariable long id, @Valid @RequestBody DuckUpdateRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable long id) { service.delete(id); }
}
