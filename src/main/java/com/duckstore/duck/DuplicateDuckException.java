package com.duckstore.duck;

public class DuplicateDuckException extends RuntimeException {
    public DuplicateDuckException() {
        super("A duck with this color, size and price already exists.");
    }
}
