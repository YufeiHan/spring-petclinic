package org.springframework.samples.petclinic.appointment;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.util.Collection;
import java.util.List;

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

	List<Appointment> findByPetId(Integer petId);

	List<Appointment> findByVetId(Integer vetId);

	void deleteById(Integer id);
}

