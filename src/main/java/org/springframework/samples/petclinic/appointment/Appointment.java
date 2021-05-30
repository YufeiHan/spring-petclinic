package org.springframework.samples.petclinic.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Simple JavaBean domain object representing an appointment.
 *
 * @author Yufei Han
 */

@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

//	@Column(name = "start_time")
//	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm a")
//	private LocalDateTime startTime;
//
//	@Column(name = "end_time")
//	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm a")
//	private LocalDateTime endTime;

	@Column(name = "start_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@NotEmpty
	@Column(name = "description")
	private String description;

	@Column(name = "pet_id")
	private Integer petId;

	@Column(name = "vet_id")
	private Integer vetId;

	@Transient
	private String vetName;


	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

//	public LocalDateTime getStartTime() {
//		return startTime;
//	}
//
//	public void setStartTime(LocalDateTime startTime) {
//		this.startTime = startTime;
//	}
//
//	public LocalDateTime getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(LocalDateTime endTime) {
//		this.endTime = endTime;
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public Integer getVetId() {
		return vetId;
	}

	public void setVetId(Integer vetId) {
		this.vetId = vetId;
	}

	public String getVetName() {
		return vetName;
	}

	public void setVetName(String vetName) {
		this.vetName = vetName;
	}
}