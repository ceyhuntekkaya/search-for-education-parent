package com.genixo.education.search.service.hr;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.entity.hr.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.repository.hr.*;
import com.genixo.education.search.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final ApplicationNoteRepository applicationNoteRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final UserRepository userRepository;
    private final HrNotificationService hrNotificationService;

    @Transactional
    public ApplicationDto create(ApplicationCreateDto dto, Long currentUserId) {
        JobPosting jobPosting = jobPostingRepository.findById(dto.getJobPostingId())
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", dto.getJobPostingId()));

        TeacherProfile teacher = teacherProfileRepository.findById(dto.getTeacherProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", dto.getTeacherProfileId()));

        if (applicationRepository.existsByJobPostingIdAndTeacherId(dto.getJobPostingId(), dto.getTeacherProfileId())) {
            throw BusinessException.duplicateResource("Application", "jobPosting+teacher", "Bu ilana zaten başvurdunuz");
        }

        if (jobPosting.getStatus() != JobPosting.JobPostingStatus.PUBLISHED) {
            throw BusinessException.invalidState(jobPosting.getStatus().name(), "Başvuru kabul edilmiyor");
        }

        Application application = new Application();
        application.setJobPosting(jobPosting);
        application.setTeacher(teacher);
        application.setCoverLetter(dto.getCoverLetter());
        application.setStatus(Application.ApplicationStatus.RECEIVED);
        application.setIsWithdrawn(false);

        Application saved = applicationRepository.save(application);

        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            for (ApplicationDocumentCreateDto docDto : dto.getDocuments()) {
                ApplicationDocument doc = new ApplicationDocument();
                doc.setApplication(saved);
                doc.setDocumentName(docDto.getDocumentName());
                doc.setDocumentUrl(docDto.getDocumentUrl());
                doc.setDocumentType(docDto.getDocumentType());
                doc.setFileSize(docDto.getFileSize());
                applicationDocumentRepository.save(doc);
            }
        }

        hrNotificationService.notifyApplicationReceived(jobPosting, teacher, saved);

        return getById(saved.getId());
    }

    public Page<ApplicationDto> search(Long jobPostingId, Long teacherId, String status, Pageable pageable) {
        return applicationRepository.search(jobPostingId, teacherId, status, pageable)
                .map(this::mapToDto);
    }

    public ApplicationDto getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
        return mapToDto(application);
    }

    public Page<ApplicationDto> getByJobPostingId(Long jobPostingId, Pageable pageable) {
        return applicationRepository.findByJobPostingId(jobPostingId, pageable).map(this::mapToDto);
    }

    public Page<ApplicationDto> getByTeacherId(Long teacherId, Pageable pageable) {
        return applicationRepository.findByTeacherId(teacherId, pageable).map(this::mapToDto);
    }

    public Page<ApplicationDto> getByCampusId(Long campusId, Pageable pageable) {
        return applicationRepository.findByCampusId(campusId, pageable).map(this::mapToDto);
    }

    @Transactional
    public ApplicationDto updateStatus(Long id, ApplicationStatusUpdateDto dto, Long currentUserId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        Application.ApplicationStatus newStatus = Application.ApplicationStatus.valueOf(dto.getStatus());
        application.setStatus(newStatus);

        Application saved = applicationRepository.save(application);
        hrNotificationService.notifyStatusUpdated(application, newStatus);
        return mapToDto(saved);
    }

    @Transactional
    public ApplicationDto withdraw(Long id, Long currentUserId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (!application.getTeacher().getUser().getId().equals(currentUserId)) {
            throw BusinessException.accessDenied("Bu başvuruyu geri çekme");
        }

        application.setIsWithdrawn(true);
        Application saved = applicationRepository.save(application);
        hrNotificationService.notifyApplicationWithdrawn(application);
        return mapToDto(saved);
    }

    @Transactional
    public ApplicationNoteDto addNote(Long applicationId, ApplicationNoteCreateDto dto, Long currentUserId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

        ApplicationNote note = new ApplicationNote();
        note.setApplication(application);
        note.setNoteCreatedBy(user);
        note.setNoteText(dto.getNoteText());

        ApplicationNote saved = applicationNoteRepository.save(note);
        return mapNoteToDto(saved);
    }

    public List<ApplicationNoteDto> getNotes(Long applicationId) {
        return applicationNoteRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId)
                .stream().map(this::mapNoteToDto).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDocumentDto addDocument(Long applicationId, ApplicationDocumentCreateDto dto) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        ApplicationDocument doc = new ApplicationDocument();
        doc.setApplication(application);
        doc.setDocumentName(dto.getDocumentName());
        doc.setDocumentUrl(dto.getDocumentUrl());
        doc.setDocumentType(dto.getDocumentType());
        doc.setFileSize(dto.getFileSize());

        ApplicationDocument saved = applicationDocumentRepository.save(doc);
        return mapDocumentToDto(saved);
    }

    public List<ApplicationDocumentDto> getDocuments(Long applicationId) {
        return applicationDocumentRepository.findByApplicationId(applicationId)
                .stream().map(this::mapDocumentToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long applicationId, Long documentId) {
        ApplicationDocument doc = applicationDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationDocument", documentId));
        if (!doc.getApplication().getId().equals(applicationId)) {
            throw new ResourceNotFoundException("ApplicationDocument", documentId);
        }
        applicationDocumentRepository.delete(doc);
    }

    private ApplicationDto mapToDto(Application a) {
        return ApplicationDto.builder()
                .id(a.getId())
                .jobPostingId(a.getJobPosting().getId())
                .jobPosting(JobPostingSummaryDto.builder()
                        .id(a.getJobPosting().getId())
                        .positionTitle(a.getJobPosting().getPositionTitle())
                        .branch(a.getJobPosting().getBranch())
                        .employmentType(a.getJobPosting().getEmploymentType())
                        .applicationDeadline(a.getJobPosting().getApplicationDeadline())
                        .status(a.getJobPosting().getStatus().name())
                        .campus(CampusSummaryDto.builder()
                                .id(a.getJobPosting().getCampus().getId())
                                .name(a.getJobPosting().getCampus().getName())
                                .slug(a.getJobPosting().getCampus().getSlug())
                                .email(a.getJobPosting().getCampus().getEmail())
                                .build())
                        .build())
                .teacherId(a.getTeacher().getId())
                .teacher(TeacherProfileSummaryDto.builder()
                        .id(a.getTeacher().getId())
                        .fullName(a.getTeacher().getFullName())
                        .email(a.getTeacher().getEmail())
                        .branch(a.getTeacher().getBranch())
                        .profilePhotoUrl(a.getTeacher().getProfilePhotoUrl())
                        .build())
                .coverLetter(a.getCoverLetter())
                .status(a.getStatus().name())
                .isWithdrawn(a.getIsWithdrawn())
                .notes(applicationNoteRepository.findByApplicationIdOrderByCreatedAtDesc(a.getId())
                        .stream().map(this::mapNoteToDto).collect(Collectors.toList()))
                .documents(applicationDocumentRepository.findByApplicationId(a.getId())
                        .stream().map(this::mapDocumentToDto).collect(Collectors.toList()))
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private ApplicationNoteDto mapNoteToDto(ApplicationNote n) {
        return ApplicationNoteDto.builder()
                .id(n.getId())
                .applicationId(n.getApplication().getId())
                .createdByUserId(n.getNoteCreatedBy().getId())
                .createdByUserName(n.getNoteCreatedBy().getFirstName() + " " + n.getNoteCreatedBy().getLastName())
                .noteText(n.getNoteText())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private ApplicationDocumentDto mapDocumentToDto(ApplicationDocument d) {
        return ApplicationDocumentDto.builder()
                .id(d.getId())
                .applicationId(d.getApplication().getId())
                .documentName(d.getDocumentName())
                .documentUrl(d.getDocumentUrl())
                .documentType(d.getDocumentType())
                .fileSize(d.getFileSize())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
