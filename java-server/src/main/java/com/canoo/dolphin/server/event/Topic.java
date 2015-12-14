package com.canoo.dolphin.server.event;

import java.util.UUID;

public class Topic<T> {

    private String name;

    public Topic() {
        this(UUID.randomUUID().toString());
    }

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static <T> Topic<T> create(String uniqueName) {
        return new Topic<>(uniqueName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic<?> topic = (Topic<?>) o;

        return !(name != null ? !name.equals(topic.name) : topic.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
