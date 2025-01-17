package my.project.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductAvailabilityDTO (Long id, Integer quantity) {}
