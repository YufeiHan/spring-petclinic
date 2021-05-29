package org.springframework.samples.petclinic.vet;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;

import java.util.Collection;

public interface SpecialtyRepository extends Repository<Specialty, Integer> {
	Collection<Specialty> findAll() throws DataAccessException;

	Specialty findByName(String name);
}
