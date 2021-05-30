/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.appointment.Appointment;
import org.springframework.samples.petclinic.appointment.AppointmentRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private final PetRepository petRepository;
	private final VisitRepository visitRepository;
	private final AppointmentRepository appointmentRepository;
	private final VetRepository vetRepository;

	@Autowired
	public VisitController(PetRepository petRepository, VisitRepository visitRepository, AppointmentRepository appointmentRepository, VetRepository vetRepository) {
		this.petRepository = petRepository;
		this.visitRepository = visitRepository;
		this.appointmentRepository = appointmentRepository;
		this.vetRepository = vetRepository;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("vetsList")
	public Collection<Vet> populateVets() {
		return vetRepository.findAll();
	}

	private void loadPetWithVisit(Integer petId, Map<String, Object> model) {
		Pet pet = petRepository.findById(petId);
		pet.setVisitsInternal(visitRepository.findByPetId(petId));
		model.put("pet", pet);

		Visit visit = new Visit();
		pet.addVisit(visit);
		model.put("visit", visit);
	}

	@GetMapping("/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") Integer petId, Map<String, Object> model) {
		loadPetWithVisit(petId, model);
		return "pets/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		visitRepository.save(visit);
		return "redirect:/owners/{ownerId}";
	}

	private void loadPetWithAppointment(Integer petId, Map<String, Object> model) {
		Pet pet = petRepository.findById(petId);
		Collection<Appointment> appointments = appointmentRepository.findByPetId(petId);
		for (Appointment appointment : appointments) {
			Vet vet = vetRepository.findById(appointment.getVetId());
			appointment.setVetName(vet.getFirstName() + " " + vet.getLastName());
		}
		pet.setAppointmentInternal(appointments);
		model.put("pet", pet);

		Appointment appointment = new Appointment();
		pet.addAppointment(appointment);
		model.put("appointment", appointment);
	}

	@GetMapping("/owners/*/pets/{petId}/appointments/new")
	public String initNewAppointmentForm(@PathVariable("petId") Integer petId, Map<String, Object> model) {
		loadPetWithAppointment(petId, model);
		return "pets/createOrUpdateAppointmentForm";
	}

	@PostMapping("/owners/{ownerId}/pets/*/appointments/new")
	public String processNewAppointmentForm(@Valid Appointment appointment, BindingResult result) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateAppointmentForm";
		}
		appointmentRepository.save(appointment);
		return "redirect:/owners/{ownerId}";
	}

	@RequestMapping("/owners/{ownerId}/pets/*/appointments/{appointmentId}/delete")
	public String deleteAppointmentById(@PathVariable Integer appointmentId) {
		appointmentRepository.deleteById(appointmentId);
		return "redirect:/owners/{ownerId}";
	}
}
