package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Yufei Han
 */

@Controller
public class AppointmentController {

	private final AppointmentRepository appointmentRepository;
	private final FullyBookedDateRepository fullyBookedDateRepository;

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2021-06-01


	public AppointmentController(AppointmentRepository appointmentRepository, FullyBookedDateRepository fullyBookedDateRepository) {
		this.appointmentRepository = appointmentRepository;
		this.fullyBookedDateRepository = fullyBookedDateRepository;
	}

	@GetMapping("/appointments.html")
	public String showVetList(Map<String, Object> model) {
		Collection<Appointment> appointments = this.appointmentRepository.findAll();
		model.put("appointments", appointments);
		return "appointments/appointmentsList";
	}

	@GetMapping("/appointments/checkTime")
	// http://localhost:8080/appointments/checkTime?vetId=1&date=2021-06-01
	public @ResponseBody List<List<String>> showVetAvailableTimeList(@RequestParam Integer vetId,
																	 @RequestParam String date) {
		List<List<String>> availableTimeList = new ArrayList<>();

		LocalDate localDate = LocalDate.parse(date, dateFormatter);
		Set<String> bookedDays = getBookedDaysWithin(vetId, localDate, localDate);
		if (bookedDays.size() == 1) {
			return availableTimeList;
		}

		Set<String> bookedValues = getBookedTimeOfADay(vetId, date);
		availableTimeList = getAvailableTimeOfADay(date, bookedValues);
		return availableTimeList;
	}

	// return a set of booked days in the date range (inclusive) for a vet
	private Set<String> getBookedDaysWithin(Integer vetId, LocalDate dateFrom, LocalDate dateTo) {
		List<LocalDate> fullyBookedDates = fullyBookedDateRepository.findFullyBookedDatesByVetId(vetId, dateFrom, dateTo);

		Set<String> bookedDates = new HashSet<>();
		for (LocalDate currDate : fullyBookedDates) {
			bookedDates.add(currDate.format(dateFormatter));
		}
		return bookedDates;
	}

	private Set<String> getBookedTimeOfADay(Integer vetId, String date) {
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 2021-06-01 13:00
		LocalDateTime dateFrom = LocalDateTime.parse(date + " 07:59", fullDateTimeFormatter);
		LocalDateTime dateTo = LocalDateTime.parse(date + " 17:01", fullDateTimeFormatter);
		List<Appointment> bookedAppointments = appointmentRepository.findAppointmentByVetId(vetId, dateFrom, dateTo);

		DateTimeFormatter selectValueFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 08:00 AM
		Set<String> bookedValues = new HashSet<>();
		for (Appointment appointment : bookedAppointments) {
			bookedValues.add(appointment.getStartTime().format(selectValueFormatter));
		}
		return bookedValues;
	}

	private List<List<String>> getAvailableTimeOfADay(String date, Set<String> bookedValues) {
		List<List<String>> availableTimeList = new ArrayList<>();

		int hourOfTheDay = 8;
		LocalDateTime today = LocalDateTime.now();
		// if selected today, only provide future timeslot
		if (today.format(dateFormatter).equals(date)) {
			hourOfTheDay = Math.max(8, today.getHour() + 1);
		}

		DateTimeFormatter selectValueFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 08:00 AM
		DateTimeFormatter selectDisplayFormatter = DateTimeFormatter.ofPattern("h:mm a"); // 8:00 AM
		for (; hourOfTheDay < 17; hourOfTheDay++) {
			LocalTime time = LocalTime.of(hourOfTheDay, 0, 0);
			String valueText = time.format(selectValueFormatter);
			if (bookedValues.contains(valueText)) {
				continue;
			}
			String displayText = time.format(selectDisplayFormatter);
			availableTimeList.add(Arrays.asList(valueText, displayText));
		}
		return availableTimeList;
	}

	@GetMapping("/appointments/checkNextDate")
	// http://localhost:8080/appointments/checkNextDate?vetId=1&date=2021-06-01&nextDateRange=30
	public @ResponseBody String showVetNextAvailableDate(@RequestParam Integer vetId, @RequestParam String date,
														 @RequestParam Integer nextDateRange) {
		LocalDate localDate = LocalDate.parse(date, dateFormatter);

		Set<String> bookedDays = getBookedDaysWithin(vetId, localDate.plusDays(1), localDate.plusDays(nextDateRange));

		for (int i = 1; i <= nextDateRange; i++) {
			LocalDate newDate = localDate.plusDays(i);
			if (newDate.getDayOfWeek() == DayOfWeek.SATURDAY || newDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
				continue;
			}
			String newDateStr = newDate.format(dateFormatter);
			if (bookedDays.contains(newDateStr)) {
				continue;
			}
			return newDateStr;
		}
		return "Not Found";
	}


}
