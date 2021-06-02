package org.springframework.samples.petclinic.appointment;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for <code>Appointment</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Yufei Han
 */

public interface AppointmentRepository extends Repository<Appointment, Integer> {
	/**
	 * Save a <code>Appointment</code> to the data store, either inserting or updating it.
	 * @param appointment the <code>Appointment</code> to save
	 * @see BaseEntity#isNew
	 */
	void save(Appointment appointment) throws DataAccessException;

	Collection<Appointment> findAll() throws DataAccessException;

	Optional<Appointment> findById(Integer id);

	List<Appointment> findByPetId(Integer petId);

	List<Appointment> findByVetId(Integer vetId);

	void deleteById(Integer id);

	@Query("SELECT appointment FROM Appointment appointment " +
			"WHERE appointment.vetId =:vetId and appointment.startTime > :dateFrom AND appointment.startTime < :dateTo")
	@Transactional(readOnly = true)
	List<Appointment> findAppointmentByVetId(@Param("vetId") Integer vetId,
											 @Param("dateFrom") LocalDateTime dateFrom,
											 @Param("dateTo") LocalDateTime dateTo);

}

