package it.ghigo.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.ghigo.dto.CounterDTO;
import it.ghigo.service.CounterService;

@RestController
@RequestMapping("/counter")
public class CounterController {

	@Autowired
	private CounterService counterService;

	private SimpleDateFormat sdf;

	public CounterController() {
		this.sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	@GetMapping("/get/{date}")
	public ResponseEntity<List<CounterDTO>> getByDate(@PathVariable String date) throws Exception {
		List<CounterDTO> cDTOList = counterService.findByEventDate(sdf.parse(date));
		return ResponseEntity.ok(cDTOList);
	}

	@PostMapping("/post")
	public ResponseEntity<Void> updateCounter(@RequestBody CounterDTO cDTO) throws Exception {
		counterService.updateCounter(cDTO);
		return ResponseEntity.ok().build();
	}
}