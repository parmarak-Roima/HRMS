package com.HRMS.HRMS.repository.TravelRepositories;

import com.HRMS.HRMS.entity.TravelEntities.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseTypeRepo extends JpaRepository<ExpenseType,Long> {
}
