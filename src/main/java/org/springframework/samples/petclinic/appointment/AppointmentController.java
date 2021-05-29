package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Map;

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

}
