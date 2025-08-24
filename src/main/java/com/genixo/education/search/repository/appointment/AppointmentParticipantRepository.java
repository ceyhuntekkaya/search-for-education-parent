package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentParticipantRepository extends JpaRepository<AppointmentParticipant, Long> {
}
