package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentNoteRepository extends JpaRepository<AppointmentNote, Long> {
}
