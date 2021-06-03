package org.springframework.samples.petclinic.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "fully_booked_date")
public class FullyBookedDate extends BaseEntity {

	@Column(name = "vet_id")
	private Integer vetId;

	@Column(name = "booked_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate bookedDate;

	public Integer getVetId() {
		return vetId;
	}

	public void setVetId(Integer vetId) {
		this.vetId = vetId;
	}

	public LocalDate getBookedDate() {
		return bookedDate;
	}

	public void setBookedDate(LocalDate bookedDate) {
		this.bookedDate = bookedDate;
	}

}
