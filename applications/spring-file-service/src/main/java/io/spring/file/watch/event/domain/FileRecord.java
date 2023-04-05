package io.spring.file.watch.event.domain;

public record FileRecord(String absolutePath, long lastModified) {
}
