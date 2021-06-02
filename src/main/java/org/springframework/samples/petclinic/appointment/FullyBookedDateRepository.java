package org.springframework.samples.petclinic.appointment;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface FullyBookedDateRepository extends Repository<FullyBookedDate, Integer> {
	@Query("SELECT fullyBookedDate.bookedDate FROM FullyBookedDate fullyBookedDate " +
		"WHERE fullyBookedDate.vetId =:vetId and fullyBookedDate.bookedDate >= :dateFrom AND fullyBookedDate.bookedDate <= :dateTo " +
		"ORDER BY fullyBookedDate.bookedDate")
	@Transactional(readOnly = true)
	List<LocalDate> findFullyBookedDatesByVetId(@Param("vetId") Integer vetId, @Param("dateFrom") LocalDate dateFrom,
												@Param("dateTo") LocalDate dateTo);

	void save(FullyBookedDate fullyBookedDate) throws DataAccessException;

	@Transactional
	void deleteFullyBookedDateByVetIdAndBookedDate(Integer vetId, LocalDate bookedDate) throws DataAccessException;
}
