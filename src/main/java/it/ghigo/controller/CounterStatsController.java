package it.ghigo.controller;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.ghigo.dto.DatasetCounterStatsDTO;
import it.ghigo.dto.STATS_TYPE;
import it.ghigo.service.CounterStatsService;

@RestController
@RequestMapping("/counterStats")
public class CounterStatsController {

	@Autowired
	private CounterStatsService counterStatsService;

	@GetMapping("/statsTypeList")
	public ResponseEntity<List<String>> getStatsTypeList() throws Exception {
		return ResponseEntity.ok(Stream.of(STATS_TYPE.values()).map(st -> st.toString()).toList());
	}

	@GetMapping("/stats/{counterTypeId}/{type}/{period}")
	public ResponseEntity<DatasetCounterStatsDTO> getStats(@PathVariable Long counterTypeId, @PathVariable String type,
			@PathVariable String period) throws Exception {

		DatasetCounterStatsDTO dto = counterStatsService.findByCounterTypeIdAndTypeAndPeriod(counterTypeId,
				STATS_TYPE.valueOf(type), period);
		return ResponseEntity.ok(dto);
	}
}