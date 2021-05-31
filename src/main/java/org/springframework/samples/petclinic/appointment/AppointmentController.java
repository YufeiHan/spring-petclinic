package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Yufei Han
 */

@Controller
public class AppointmentController {

	private final AppointmentRepository appointmentRepository;

	public AppointmentController(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	@GetMapping("/appointments.html")
	public String showVetList(Map<String, Object> model) {
		Collection<Appointment> appointments = this.appointmentRepository.findAll();
		model.put("appointments", appointments);
		return "appointments/appointmentsList";
	}

	@GetMapping("/appointments/checktime")
	// http://localhost:8080/appointments/checktime?vetId=1&date=2021-06-01
	public @ResponseBody List<List<String>> showVetAvailableTimeList(@RequestParam Integer vetId,
																	 @RequestParam String date) {
		Set<String> bookedValues = getBookedValuesOfADay(vetId, date);
		List<List<String>> availableTimeList = getAvailableTimeOfADay(bookedValues);
		return availableTimeList;
	}

	private Set<String> getBookedValuesOfADay(Integer vetId, String date) {
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 2021-06-01 13:00
		LocalDateTime dateFrom = LocalDateTime.parse(date + " 07:59", fullDateTimeFormatter);
		LocalDateTime dateTo = LocalDateTime.parse(date + " 17:01", fullDateTimeFormatter);
		List<Appointment> bookedAppointments = appointmentRepository.findAppointmentByVetId(vetId, dateFrom, dateTo);

		DateTimeFormatter valueFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 08:00 AM
		Set<String> bookedValues = new HashSet<>();
		for (Appointment appointment : bookedAppointments) {
			bookedValues.add(appointment.getStartTime().format(valueFormatter));
		}
		return bookedValues;
	}

	private List<List<String>> getAvailableTimeOfADay(Set<String> bookedValues) {
		DateTimeFormatter valueFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 08:00 AM
		DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("h:mm a"); // 8:00 AM

		List<List<String>> availableTimeList = new ArrayList<>();
		LocalDateTime time = LocalDateTime.of(2000, 1, 1, 8, 0, 0);
		for (int hourOfTheDay = 8; hourOfTheDay < 17; hourOfTheDay++) {
			LocalDateTime timeIter = time.withHour(hourOfTheDay);
			String valueText = timeIter.format(valueFormatter);
			if (bookedValues.contains(valueText)) {
				continue;
			}
			String displayText = timeIter.format(displayFormatter);
			availableTimeList.add(Arrays.asList(valueText, displayText));
		}
		return availableTimeList;
	}

}
