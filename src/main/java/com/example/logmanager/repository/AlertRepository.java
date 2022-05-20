package com.example.logmanager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.logmanager.model.Alert;


@Repository
public interface AlertRepository extends CrudRepository<Alert, String> {
}
