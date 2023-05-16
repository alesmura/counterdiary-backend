package it.ghigo.dto;

import java.util.List;

public record DatasetCounterStatsDTO(String label, CounterTypeDTO ctDTO, List<CounterStatsDTO> csDTOList) {

}
