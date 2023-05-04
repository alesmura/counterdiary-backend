package it.ghigo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.ghigo.dto.CounterTypeDTO;
import it.ghigo.model.CounterType;
import it.ghigo.repository.CounterRepository;
import it.ghigo.repository.CounterTypeRepository;

@Component
public class CounterTypeService {

	@Autowired
	private CounterTypeRepository counterTypeRepository;

	@Autowired
	private CounterRepository counterRepository;

	public List<CounterTypeDTO> findDTOOrderBySequence() {
		return findOrderBySequence().stream().map(ct -> getDTO(ct)).toList();
	}

	public List<CounterType> findOrderBySequence() {
		return counterTypeRepository.findAllByOrderBySeqAsc();
	}

	public void save(CounterTypeDTO ctDTO) {
		Optional<CounterType> ctOpt = counterTypeRepository.findById(ctDTO.id());
		CounterType ct = ctOpt.orElse(new CounterType());
		ct.setName(ctDTO.name());
		ct.setDescription(ctDTO.description());
		ct.setSeq(ctDTO.seq());
		counterTypeRepository.save(ct);
	}

	public CounterTypeDTO getDTO(CounterType ct) {
		return new CounterTypeDTO(ct.getId(), ct.getSeq(), ct.getName(), ct.getDescription());
	}

	public void delete(Long id) throws Exception {
		Optional<CounterType> ctOpt = counterTypeRepository.findById(id);
		if (!ctOpt.isPresent())
			throw new Exception("Counter type ID: " + id + " not found");
		if (counterRepository.countByCounterTypeId(id) > 0)
			throw new Exception("Counter type ID: " + id + " used");
		counterTypeRepository.delete(ctOpt.get());
	}
}