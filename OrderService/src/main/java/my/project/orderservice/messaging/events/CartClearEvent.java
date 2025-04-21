package my.project.orderservice.messaging.events;

import java.io.Serializable;

public record CartClearEvent(
        Long customerId
) implements Serializable {}
