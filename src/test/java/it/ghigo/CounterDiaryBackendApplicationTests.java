package it.ghigo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import it.ghigo.dto.CounterStatsDTO;
import it.ghigo.dto.CounterTypeDTO;
import it.ghigo.dto.DatasetCounterStatsDTO;
import it.ghigo.dto.STATS_TYPE;

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

	@Test
	@Order(7)
	void testGetStatsTypeList() throws Exception {
		ResponseEntity<List<String>> response = restTemplate.exchange("/counterStats/statsTypeList", HttpMethod.GET,
				null, new ParameterizedTypeReference<List<String>>() {
				});
		List<String> statsTypeList = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(3, statsTypeList.size());
		assertEquals(STATS_TYPE.WEEKLY.toString(), statsTypeList.get(0));
		assertEquals(STATS_TYPE.MONTHLY.toString(), statsTypeList.get(1));
		assertEquals(STATS_TYPE.YEARLY.toString(), statsTypeList.get(2));
	}

	@Test
	@Order(8)
	void testStats() throws Exception {
		final String COUNTER_TYPE_NAME = "TEST_STATS";
		{
			CounterTypeDTO ctDTOx = new CounterTypeDTO(0, 1, COUNTER_TYPE_NAME, "");
			ResponseEntity<String> response = restTemplate.postForEntity(getBasePath() + "/counterType/post", ctDTOx,
					String.class);
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		}
		CounterTypeDTO ctDTO;
		{
			ResponseEntity<List<CounterTypeDTO>> response = restTemplate.exchange("/counterType/get", HttpMethod.GET,
					null, new ParameterizedTypeReference<List<CounterTypeDTO>>() {
					});
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
			List<CounterTypeDTO> counterTypeList = response.getBody();
			ctDTO = counterTypeList.stream().filter(x -> x.name().equals(COUNTER_TYPE_NAME)).findFirst().get();
		}
		{
			writeCount(ctDTO, "2023-01-01", 1);
			writeCount(ctDTO, "2023-01-02", 2);
			writeCount(ctDTO, "2023-01-03", 3);
			writeCount(ctDTO, "2023-01-04", 4);
			writeCount(ctDTO, "2023-01-05", 12);
			writeCount(ctDTO, "2023-01-28", 3);
			//
			writeCount(ctDTO, "2023-02-28", 10);
			//
			writeCount(ctDTO, "2023-04-10", 99);
			//
			writeCount(ctDTO, "2023-12-31", 1);
		}
		//
		{
			String year = "2023";
			ResponseEntity<DatasetCounterStatsDTO> response = restTemplate.exchange(
					"/counterStats/stats/" + ctDTO.id() + "/" + STATS_TYPE.YEARLY + "/" + year, HttpMethod.GET, null,
					new ParameterizedTypeReference<DatasetCounterStatsDTO>() {
					});
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
			DatasetCounterStatsDTO dto = response.getBody();
			List<CounterStatsDTO> statsList = dto.csDTOList();
			//
			assertEquals("Year " + year, dto.label());
			//
			assertEquals(12, statsList.size());
			//
			assertEquals(Month.JANUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(0).label());
			assertEquals(25, statsList.get(0).counter());
			//
			assertEquals(Month.FEBRUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(1).label());
			assertEquals(10, statsList.get(1).counter());
			//
			assertEquals(Month.MARCH.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(2).label());
			assertEquals(0, statsList.get(2).counter());
			//
			assertEquals(Month.APRIL.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(3).label());
			assertEquals(99, statsList.get(3).counter());
			//
			assertEquals(Month.MAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(4).label());
			assertEquals(0, statsList.get(4).counter());
			//
			assertEquals(Month.JUNE.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(5).label());
			assertEquals(0, statsList.get(5).counter());
			//
			assertEquals(Month.JULY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(6).label());
			assertEquals(0, statsList.get(6).counter());
			//
			assertEquals(Month.AUGUST.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(7).label());
			assertEquals(0, statsList.get(7).counter());
			//
			assertEquals(Month.SEPTEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(8).label());
			assertEquals(0, statsList.get(8).counter());
			//
			assertEquals(Month.OCTOBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(9).label());
			assertEquals(0, statsList.get(9).counter());
			//
			assertEquals(Month.NOVEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(10).label());
			assertEquals(0, statsList.get(10).counter());
			//
			assertEquals(Month.DECEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(11).label());
			assertEquals(1, statsList.get(11).counter());

		}
		//
		{
			String period = "2023-01";
			ResponseEntity<DatasetCounterStatsDTO> response = restTemplate.exchange(
					"/counterStats/stats/" + ctDTO.id() + "/" + STATS_TYPE.MONTHLY + "/" + period, HttpMethod.GET, null,
					new ParameterizedTypeReference<DatasetCounterStatsDTO>() {
					});
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
			DatasetCounterStatsDTO dto = response.getBody();
			List<CounterStatsDTO> statsList = dto.csDTOList();
			//
			assertEquals("Month " + Month.JANUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
					+ " year 2023", dto.label());
			//
			assertEquals(31, statsList.size());
			//
			assertEquals("1", statsList.get(0).label());
			assertEquals(1, statsList.get(0).counter());
			//
			assertEquals("2", statsList.get(1).label());
			assertEquals(2, statsList.get(1).counter());
			//
			assertEquals("3", statsList.get(2).label());
			assertEquals(3, statsList.get(2).counter());
			//
			assertEquals("4", statsList.get(3).label());
			assertEquals(4, statsList.get(3).counter());
			//
			assertEquals("5", statsList.get(4).label());
			assertEquals(12, statsList.get(4).counter());
			//
			for (int i = 5; i < 27; i++) {
				assertEquals(String.valueOf(i + 1), statsList.get(i).label());
				assertEquals(0, statsList.get(i).counter());
			}
			//
			assertEquals("28", statsList.get(27).label());
			assertEquals(3, statsList.get(27).counter());
			//
			for (int i = 28; i < 31; i++) {
				assertEquals(String.valueOf(i + 1), statsList.get(i).label());
				assertEquals(0, statsList.get(i).counter());
			}
		}
		{
			String period = "2023-06";
			ResponseEntity<DatasetCounterStatsDTO> response = restTemplate.exchange(
					"/counterStats/stats/" + ctDTO.id() + "/" + STATS_TYPE.MONTHLY + "/" + period, HttpMethod.GET, null,
					new ParameterizedTypeReference<DatasetCounterStatsDTO>() {
					});
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
			DatasetCounterStatsDTO dto = response.getBody();
			List<CounterStatsDTO> statsList = dto.csDTOList();
			//
			assertEquals(
					"Month " + Month.JUNE.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " year 2023",
					dto.label());
			//
			assertEquals(30, statsList.size());
			for (int i = 0; i < 30; i++) {
				assertEquals(String.valueOf(i + 1), statsList.get(i).label());
				assertEquals(0, statsList.get(i).counter());
			}
		}
		{
			String period = "2023-01";
			ResponseEntity<DatasetCounterStatsDTO> response = restTemplate.exchange(
					"/counterStats/stats/" + ctDTO.id() + "/" + STATS_TYPE.WEEKLY + "/" + period, HttpMethod.GET, null,
					new ParameterizedTypeReference<DatasetCounterStatsDTO>() {
					});
			assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
			DatasetCounterStatsDTO dto = response.getBody();
			List<CounterStatsDTO> statsList = dto.csDTOList();
			//
			assertEquals("Week 01 year 2023", dto.label());
			//
			assertEquals(7, statsList.size());
			//
			assertEquals(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(0).label());
			assertEquals(2, statsList.get(0).counter());
			//
			assertEquals(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(1).label());
			assertEquals(3, statsList.get(1).counter());
			//
			assertEquals(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(2).label());
			assertEquals(4, statsList.get(2).counter());
			//
			assertEquals(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(3).label());
			assertEquals(12, statsList.get(3).counter());
			//
			assertEquals(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(4).label());
			assertEquals(0, statsList.get(4).counter());
			//
			assertEquals(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(5).label());
			assertEquals(0, statsList.get(5).counter());
			//
			assertEquals(DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
					statsList.get(6).label());
			assertEquals(0, statsList.get(6).counter());
		}
	}

	private void writeCount(CounterTypeDTO ctDTO, String dtString, long count) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(dtString);
		ResponseEntity<List<CounterDTO>> response = restTemplate.exchange("/counter/get/" + sdf.format(dt),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<CounterDTO>>() {
				});
		List<CounterDTO> counterList = response.getBody();
		CounterDTO cDTO = counterList.stream().filter(x -> x.counterTypeDTO().name().equals(ctDTO.name())).findFirst()
				.get();
		restTemplate.postForEntity(getBasePath() + "/counter/post",
				new CounterDTO(cDTO.id(), ctDTO, cDTO.eventDate(), count), String.class);
	}

}