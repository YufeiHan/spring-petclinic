package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

	private static final int TEST_PET_ID = 1;
	private static final int TEST_VET_ID = 1;
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2021-06-01
	private static DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 2021-06-01 13:00

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AppointmentRepository appointmentRepository;

	@MockBean
	private FullyBookedDateRepository fullyBookedDateRepository;

	@BeforeEach
	void setUp() {
	}

	@Test
	void showVetAvailableTimeList() throws Exception {
		LocalDate date = LocalDate.parse("2021-06-01", dateFormatter);

		List<LocalDate> fullyBookedDateList = new ArrayList<>(); // empty
		given(fullyBookedDateRepository.findFullyBookedDatesByVetId(TEST_VET_ID, date, date)).willReturn(fullyBookedDateList);

		LocalDateTime dateTimeFrom = LocalDateTime.parse("2021-06-01 07:59", fullDateTimeFormatter);
		LocalDateTime dateTimeTo = LocalDateTime.parse("2021-06-01 17:01", fullDateTimeFormatter);

		List<Appointment> appointmentList = new ArrayList<>();

		Appointment appointment1 = new Appointment();
		appointment1.setStartTime(LocalDateTime.parse("2021-06-01 08:00", fullDateTimeFormatter));
		appointmentList.add(appointment1);

		Appointment appointment2 = new Appointment();
		appointment2.setStartTime(LocalDateTime.parse("2021-06-01 10:00", fullDateTimeFormatter));
		appointmentList.add(appointment2);

		given(appointmentRepository.findAppointmentByVetId(TEST_PET_ID, dateTimeFrom, dateTimeTo)).willReturn(appointmentList);

		mockMvc.perform(get("/appointments/checkTime").param("vetId", "1").param("date", "2021-06-01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(7)))
			.andExpect(jsonPath("$[0][0]").value("09:00 AM"))
			.andExpect(jsonPath("$[0][1]").value("9:00 AM"));
	}

	@Test
	void showVetAvailableTimeListDayFullyBooked() throws Exception {
		LocalDate date = LocalDate.parse("2021-06-01", dateFormatter);

		List<LocalDate> fullyBookedDateList = new ArrayList<>();
		fullyBookedDateList.add(date);
		given(fullyBookedDateRepository.findFullyBookedDatesByVetId(TEST_VET_ID, date, date)).willReturn(fullyBookedDateList);

		mockMvc.perform(get("/appointments/checkTime").param("vetId", "1").param("date", "2021-06-01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void showVetNextAvailableDate() throws Exception {
		LocalDate dateFrom = LocalDate.parse("2021-06-02", dateFormatter);
		LocalDate dateTo = LocalDate.parse("2021-06-05", dateFormatter);

		List<LocalDate> fullyBookedDateList = new ArrayList<>();
		fullyBookedDateList.add(LocalDate.parse("2021-06-02", dateFormatter));
		given(fullyBookedDateRepository.findFullyBookedDatesByVetId(TEST_VET_ID, dateFrom, dateTo)).willReturn(fullyBookedDateList);

		mockMvc.perform(get("/appointments/checkNextDate").param("vetId", "1")
			.param("date", "2021-06-01").param("nextDateRange", "4"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("2021-06-03"));
	}

	@Test
	void showVetNextAvailableDateOverWeekend() throws Exception {
		LocalDate dateFrom = LocalDate.parse("2021-06-04", dateFormatter);
		LocalDate dateTo = LocalDate.parse("2021-06-07", dateFormatter);

		List<LocalDate> fullyBookedDateList = new ArrayList<>();
		fullyBookedDateList.add(LocalDate.parse("2021-06-04", dateFormatter));
		// 2021-06-05 and 2021-06-06 is the weekend days
		given(fullyBookedDateRepository.findFullyBookedDatesByVetId(TEST_VET_ID, dateFrom, dateTo)).willReturn(fullyBookedDateList);

		mockMvc.perform(get("/appointments/checkNextDate").param("vetId", "1")
			.param("date", "2021-06-03").param("nextDateRange", "4"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("2021-06-07"));
	}

	@Test
	void showVetNextAvailableDateNoResult() throws Exception {
		LocalDate dateFrom = LocalDate.parse("2021-06-02", dateFormatter);
		LocalDate dateTo = LocalDate.parse("2021-06-04", dateFormatter);

		List<LocalDate> fullyBookedDateList = new ArrayList<>();
		fullyBookedDateList.add(LocalDate.parse("2021-06-02", dateFormatter));
		fullyBookedDateList.add(LocalDate.parse("2021-06-03", dateFormatter));
		fullyBookedDateList.add(LocalDate.parse("2021-06-04", dateFormatter));
		given(fullyBookedDateRepository.findFullyBookedDatesByVetId(TEST_VET_ID, dateFrom, dateTo)).willReturn(fullyBookedDateList);

		mockMvc.perform(get("/appointments/checkNextDate").param("vetId", "1")
			.param("date", "2021-06-01").param("nextDateRange", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("Not Found"));
	}
}
