package it.ghigo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.ghigo.dto.CounterTypeDTO;
import it.ghigo.service.CounterTypeService;

@RestController
@RequestMapping("/counterType")
public class CounterTypeController {

	@Autowired
	private CounterTypeService counterTypeService;

	@GetMapping("/get")
	public ResponseEntity<List<CounterTypeDTO>> get() throws Exception {
		List<CounterTypeDTO> ctDTOList = counterTypeService.findDTOOrderBySequence();
		return ResponseEntity.ok(ctDTOList);
	}

	@PostMapping("/post")
	public ResponseEntity<Void> saveCounter(@RequestBody CounterTypeDTO ctDTO) throws Exception {
		counterTypeService.save(ctDTO);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteCounter(@PathVariable Long id) throws Exception {
		counterTypeService.delete(id);
		return ResponseEntity.ok().build();
	}
}