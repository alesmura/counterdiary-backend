package it.ghigo.dto;

import java.util.Date;

public record CounterDTO(long id, CounterTypeDTO counterTypeDTO, Date eventDate, long counter) {
}