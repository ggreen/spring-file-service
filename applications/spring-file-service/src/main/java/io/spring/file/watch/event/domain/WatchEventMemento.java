package io.spring.file.watch.event.domain;

public record WatchEventMemento(String absolutePath,
                                long lastModified,
                                String kindName){
}
