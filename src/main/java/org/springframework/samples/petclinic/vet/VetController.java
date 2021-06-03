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
package org.springframework.samples.petclinic.vet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.appointment.Appointment;
import org.springframework.samples.petclinic.appointment.AppointmentRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private static final String VIEWS_VET_CREATE_OR_UPDATE_FORM = "vets/createOrUpdateVetForm";

	private final VetRepository vetRepository;
	private final SpecialtyRepository specialtyRepository;
	private final AppointmentRepository appointmentRepository;
	private final PetRepository petRepository;

	@Autowired
	public VetController(VetRepository clinicService, SpecialtyRepository specialtyRepository, AppointmentRepository appointmentRepository, PetRepository petRepository) {
		this.vetRepository = clinicService;
		this.specialtyRepository = specialtyRepository;
		this.appointmentRepository = appointmentRepository;
		this.petRepository = petRepository;
	}

	@ModelAttribute("specialtiesList")
	public Collection<Specialty> populateSpecialties() {
		return specialtyRepository.findAll();
	}

	@GetMapping("/vets.html")
	public String showVetList(Map<String, Object> model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		List<Vet> allVets = (List<Vet>) vetRepository.findAll();
		Collections.sort(allVets, Comparator.comparing(vet -> vet.getFirstName()));
		vets.getVetList().addAll(allVets);
		model.put("vets", vets);
		return "vets/vetList";
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}

	@GetMapping("/vets/new")
	public String initCreationForm(Map<String, Object> model) {
		Vet vet = new Vet();
		model.put("vet", vet);
		return VIEWS_VET_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/vets/new")
	public String processCreationForm(String[] specialties, Vet vet, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_VET_CREATE_OR_UPDATE_FORM;
		}
		if (specialties != null) {
			for (String specialtyName : specialties) {
				Specialty specialty = specialtyRepository.findByName(specialtyName);
				vet.addSpecialty(specialty);
			}
		}
		this.vetRepository.save(vet);
		return "redirect:/vets.html";
	}

	@GetMapping("/vets/{vetId}")
	public ModelAndView showVet(@PathVariable("vetId") Integer vetId) {
		ModelAndView mav = new ModelAndView("vets/vetDetails");
		Vet vet = vetRepository.findById(vetId);

		Collection<Appointment> appointments = appointmentRepository.findByVetId(vetId);
		for (Appointment appointment : appointments) {
			Pet pet = petRepository.findById(appointment.getPetId());
			appointment.setPetName(pet.getName());
			appointment.setPetTypeName(pet.getType().getName());
			Owner owner = pet.getOwner();
			appointment.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
		}
		vet.setAppointmentInternal(appointments);

		mav.addObject(vet);
		return mav;
	}
}
