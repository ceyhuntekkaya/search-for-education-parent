package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
}
