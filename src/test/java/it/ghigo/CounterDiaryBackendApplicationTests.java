package it.ghigo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import it.ghigo.dto.CounterDTO;
import it.ghigo.dto.CounterTypeDTO;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class CounterDiaryBackendApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Value(value = "${server.servlet.context-path}")
	private String contextPath;

	private String getBasePath() {
		return "http://localhost:" + port + "/" + contextPath;
	}

	@Test
	@Order(1)
	void testPostCounterType() throws Exception {
		{
			CounterTypeDTO ctDTO = new CounterTypeDTO(0, 1, "TestName", "TestDescription");
			ResponseEntity<String> response = restTemplate.postForEntity(getBasePath() + "/counterType/post", ctDTO,
					String.class);
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		}
		{
			CounterTypeDTO ctDTO = new CounterTypeDTO(0, 2, "TestName2", "TestDescription2");
			ResponseEntity<String> response = restTemplate.postForEntity(getBasePath() + "/counterType/post", ctDTO,
					String.class);
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		}
	}

	@Test
	@Order(2)
	void testGetCounterType() throws Exception {
		ResponseEntity<List<CounterTypeDTO>> response = restTemplate.exchange("/counterType/get", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterTypeDTO>>() {
				});
		List<CounterTypeDTO> counterTypeList = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, counterTypeList.size());
		assertEquals("TestName", counterTypeList.get(0).name());
		assertEquals("TestDescription", counterTypeList.get(0).description());
		assertEquals(1, counterTypeList.get(0).seq());
		assertEquals("TestName2", counterTypeList.get(1).name());
		assertEquals("TestDescription2", counterTypeList.get(1).description());
		assertEquals(2, counterTypeList.get(1).seq());
	}

	@Test
	@Order(3)
	void testGetCounter() throws Exception {
		ResponseEntity<List<CounterDTO>> response = restTemplate.exchange(
				"/counter/get/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterDTO>>() {
				});
		List<CounterDTO> counterList = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, counterList.size());
		assertEquals("TestName", counterList.get(0).counterTypeDTO().name());
		assertEquals("TestDescription", counterList.get(0).counterTypeDTO().description());
		assertEquals(1, counterList.get(0).counterTypeDTO().seq());
		assertEquals(0, counterList.get(0).counter());
		assertEquals("TestName2", counterList.get(1).counterTypeDTO().name());
		assertEquals("TestDescription2", counterList.get(1).counterTypeDTO().description());
		assertEquals(2, counterList.get(1).counterTypeDTO().seq());
		assertEquals(0, counterList.get(1).counter());
	}

	@Test
	@Order(4)
	void testPostCounter() throws Exception {
		ResponseEntity<List<CounterDTO>> response = restTemplate.exchange(
				"/counter/get/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterDTO>>() {
				});
		List<CounterDTO> counterList = response.getBody();
		CounterDTO cDTO = counterList.get(0);
		CounterDTO cNewDTO = new CounterDTO(cDTO.id(), cDTO.counterTypeDTO(), cDTO.eventDate(), cDTO.counter() + 1);
		ResponseEntity<String> response2 = restTemplate.postForEntity(getBasePath() + "/counter/post", cNewDTO,
				String.class);
		assertEquals(HttpStatus.OK.value(), response2.getStatusCode().value());
	}

	@Test
	@Order(5)
	void testGetCounter2() throws Exception {
		ResponseEntity<List<CounterDTO>> response = restTemplate.exchange(
				"/counter/get/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterDTO>>() {
				});
		List<CounterDTO> counterList = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, counterList.size());
		assertEquals("TestName", counterList.get(0).counterTypeDTO().name());
		assertEquals("TestDescription", counterList.get(0).counterTypeDTO().description());
		assertEquals(1, counterList.get(0).counterTypeDTO().seq());
		assertEquals(1, counterList.get(0).counter());
		assertEquals("TestName2", counterList.get(1).counterTypeDTO().name());
		assertEquals("TestDescription2", counterList.get(1).counterTypeDTO().description());
		assertEquals(2, counterList.get(1).counterTypeDTO().seq());
		assertEquals(0, counterList.get(1).counter());
	}

	@Test
	@Order(6)
	void testDeleteCounterType() throws Exception {
		CounterTypeDTO ctDTO = new CounterTypeDTO(0, 100, "TEST_DELETE", "");
		ResponseEntity<String> response = restTemplate.postForEntity(getBasePath() + "/counterType/post", ctDTO,
				String.class);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		//
		ResponseEntity<List<CounterTypeDTO>> response2 = restTemplate.exchange("/counterType/get", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterTypeDTO>>() {
				});
		List<CounterTypeDTO> counterTypeList = response2.getBody();
		assertEquals(3, counterTypeList.size());
		CounterTypeDTO ctDeleteDTO = counterTypeList.stream().filter(x -> x.name().equals(ctDTO.name())).findFirst()
				.get();
		//
		ResponseEntity<Void> respDel = restTemplate.exchange(getBasePath() + "/counterType/delete/" + ctDeleteDTO.id(),
				HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
		assertEquals(HttpStatus.OK, respDel.getStatusCode());
		//
		response2 = restTemplate.exchange("/counterType/get", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CounterTypeDTO>>() {
				});
		counterTypeList = response2.getBody();
		assertEquals(false,
				counterTypeList.stream().filter(ctDTOx -> ctDTOx.id() == ctDeleteDTO.id()).findAny().isPresent());
	}
}