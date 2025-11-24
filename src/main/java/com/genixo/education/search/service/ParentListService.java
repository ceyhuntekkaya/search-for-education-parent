package com.genixo.education.search.service;



import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.dto.institution.SchoolDto;
import com.genixo.education.search.dto.institution.SchoolSearchResultDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.appointment.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.ListStatus;
import com.genixo.education.search.enumaration.NoteCategory;
import com.genixo.education.search.enumaration.SchoolItemStatus;
import com.genixo.education.search.repository.appointment.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.converter.InstitutionConverterService;
import com.genixo.education.search.service.converter.ParentListConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParentListService {

    private final ParentSchoolListRepository parentSchoolListRepository;
    private final ParentSchoolListItemRepository parentSchoolListItemRepository;
    private final ParentSchoolNoteRepository parentSchoolNoteRepository;
    private final ParentListNoteRepository parentListNoteRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ParentListConverterService converterService;
    private final InstitutionConverterService institutionConverterService;
    private final ParentSearchListRepository parentSearchListRepository;

    // ========================= PARENT SCHOOL LIST OPERATIONS =========================

    /**
     * Yeni liste oluştur
     */
    public ParentSchoolListResponse createList(Long parentUserId, CreateParentSchoolListRequest request) {

        // Veli var mı kontrol et
        User parent = userRepository.findById(parentUserId)
                .orElseThrow(() -> new RuntimeException("Parent user not found"));

        // Aynı isimde liste var mı kontrol et
        if (parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, request.getListName())) {
            throw new RuntimeException("Bu isimde bir liste zaten mevcut");
        }

        // Eğer bu ilk liste ise veya default olarak işaretlenirse, diğer default'ları kaldır
        if (request.getIsDefault() ||
                parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE) == 0) {

            parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        parentSchoolListRepository.save(existingDefault);
                    });
            request.setIsDefault(true);
        }

        ParentSchoolList list = ParentSchoolList.builder()
                .parentUser(parent)
                .listName(request.getListName())
                .description(request.getDescription())
                .status(ListStatus.ACTIVE)
                .isDefault(request.getIsDefault())
                .colorCode(request.getColorCode())
                .icon(request.getIcon())
                .notes(request.getNotes())
                .schoolCount(0)
                .lastAccessedAt(LocalDateTime.now())
                .build();

        ParentSchoolList savedList = parentSchoolListRepository.save(list);

        return converterService.mapToDto(savedList);
    }

    /**
     * Liste güncelle
     */
    public ParentSchoolListResponse updateList(Long parentUserId, Long listId, UpdateParentSchoolListRequest request) {

        ParentSchoolList list = parentSchoolListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        // Yetki kontrol
        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeyi güncelleme yetkiniz yok");
        }

        // İsim değişikliği kontrolü
        if (request.getListName() != null && !request.getListName().equals(list.getListName())) {
            if (parentSchoolListRepository.existsByParentUserIdAndListNameAndIdNot(
                    parentUserId, request.getListName(), listId)) {
                throw new RuntimeException("Bu isimde bir liste zaten mevcut");
            }
            list.setListName(request.getListName());
        }

        // Default değişikliği
        if (request.getIsDefault() != null && request.getIsDefault() && !list.getIsDefault()) {
            // Diğer default'ları kaldır
            parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        parentSchoolListRepository.save(existingDefault);
                    });
            list.setIsDefault(true);
        }

        // Diğer alanları güncelle
        if (request.getDescription() != null) list.setDescription(request.getDescription());
        if (request.getStatus() != null) list.setStatus(request.getStatus());
        if (request.getColorCode() != null) list.setColorCode(request.getColorCode());
        if (request.getIcon() != null) list.setIcon(request.getIcon());
        if (request.getNotes() != null) list.setNotes(request.getNotes());

        list.setLastAccessedAt(LocalDateTime.now());

        ParentSchoolList updatedList = parentSchoolListRepository.save(list);

        return converterService.mapToDto(updatedList);
    }

    /**
     * Liste sil (soft delete)
     */
    public void deleteList(Long parentUserId, Long listId) {

        ParentSchoolList list = parentSchoolListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        // Yetki kontrol
        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeyi silme yetkiniz yok");
        }

        // Default liste silinmeye çalışılıyorsa
        if (list.getIsDefault()) {
            // Başka aktif liste var mı?
            List<ParentSchoolList> otherLists = parentSchoolListRepository
                    .findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE)
                    .stream()
                    .filter(l -> !l.getId().equals(listId))
                    .toList();

            if (!otherLists.isEmpty()) {
                // İlk bulduğunu default yap
                ParentSchoolList newDefault = otherLists.get(0);
                newDefault.setIsDefault(true);
                parentSchoolListRepository.save(newDefault);
            }
        }

        list.setStatus(ListStatus.DELETED);
        parentSchoolListRepository.save(list);

    }

    /**
     * Velinin tüm listelerini getir
     */
    @Transactional(readOnly = true)
    public List<ParentSchoolListResponse> getParentLists(Long parentUserId) {

        List<ParentSchoolList> lists = parentSchoolListRepository
                .findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE);

        return converterService.mapToDto(lists);
    }

    /**
     * Liste detayını getir
     */
    @Transactional(readOnly = true)
    public ParentSchoolListResponse getListById(Long parentUserId, Long listId) {

        ParentSchoolList list = parentSchoolListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        // Yetki kontrol
        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeyi görme yetkiniz yok");
        }

        // Son erişim zamanını güncelle
        list.setLastAccessedAt(LocalDateTime.now());
        parentSchoolListRepository.save(list);

        return converterService.mapToDto(list);
    }

    // ========================= SCHOOL LIST ITEM OPERATIONS =========================

    /**
     * Okulu listeye ekle
     */
    public ParentSchoolListItemResponse addSchoolToList(Long parentUserId, AddSchoolToListRequest request) {
        // Okul var mı kontrol et
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("Okul bulunamadı"));

        // Liste kontrolü
        ParentSchoolList list;
        if (request.getParentSchoolListId() != null) {
            list = parentSchoolListRepository.findById(request.getParentSchoolListId())
                    .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));
        } else {
            // Default listeye ekle
            list = parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Default liste bulunamadı"));
        }

        // Yetki kontrol
        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeye okul ekleme yetkiniz yok");
        }

        // Zaten var mı kontrol et
        if (parentSchoolListItemRepository.existsByParentSchoolListIdAndSchoolId(list.getId(), school.getId())) {
            throw new RuntimeException("Bu okul zaten listede mevcut");
        }

        ParentSchoolListItem item = ParentSchoolListItem.builder()
                .parentSchoolList(list)
                .school(school)
                .starRating(request.getStarRating())
                .status(SchoolItemStatus.ACTIVE)
                .isFavorite(request.getIsFavorite())
                .isBlocked(false)
                .priorityOrder(request.getPriorityOrder())
                .personalNotes(request.getPersonalNotes())
                .pros(request.getPros())
                .cons(request.getCons())
                .tags(request.getTags())
                .visitPlannedDate(request.getVisitPlannedDate())
                .addedFromSearch(request.getAddedFromSearch())
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        ParentSchoolListItem savedItem = parentSchoolListItemRepository.save(item);

        // Liste okul sayısını güncelle
        list.setSchoolCount(list.getSchoolCount() + 1);
        parentSchoolListRepository.save(list);


        return converterService.mapToDto(savedItem);
    }

    /**
     * Listedeki okulu güncelle
     */
    public ParentSchoolListItemResponse updateSchoolInList(Long parentUserId, Long itemId, UpdateSchoolInListRequest request) {
        ParentSchoolListItem item = parentSchoolListItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Liste öğesi bulunamadı"));

        // Yetki kontrol
        if (!item.getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu öğeyi güncelleme yetkiniz yok");
        }

        // Alanları güncelle
        if (request.getStarRating() != null) item.setStarRating(request.getStarRating());
        if (request.getIsFavorite() != null) item.setIsFavorite(request.getIsFavorite());
        if (request.getIsBlocked() != null) item.setIsBlocked(request.getIsBlocked());
        if (request.getPriorityOrder() != null) item.setPriorityOrder(request.getPriorityOrder());
        if (request.getPersonalNotes() != null) item.setPersonalNotes(request.getPersonalNotes());
        if (request.getPros() != null) item.setPros(request.getPros());
        if (request.getCons() != null) item.setCons(request.getCons());
        if (request.getTags() != null) item.setTags(request.getTags());
        if (request.getVisitPlannedDate() != null) item.setVisitPlannedDate(request.getVisitPlannedDate());
        if (request.getVisitCompletedDate() != null) item.setVisitCompletedDate(request.getVisitCompletedDate());

        item.setLastUpdatedAt(LocalDateTime.now());

        ParentSchoolListItem updatedItem = parentSchoolListItemRepository.save(item);

        return converterService.mapToDto(updatedItem);
    }

    /**
     * Okulu listeden kaldır
     */
    public void removeSchoolFromList(Long parentUserId, Long itemId) {
        ParentSchoolListItem item = parentSchoolListItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Liste öğesi bulunamadı"));

        // Yetki kontrol
        if (!item.getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu öğeyi kaldırma yetkiniz yok");
        }

        ParentSchoolList list = item.getParentSchoolList();

        // Soft delete
        item.setStatus(SchoolItemStatus.REMOVED);
        parentSchoolListItemRepository.save(item);

        // Liste okul sayısını güncelle
        list.setSchoolCount(Math.max(0, list.getSchoolCount() - 1));
        parentSchoolListRepository.save(list);

    }

    /**
     * Liste öğelerini getir (sayfalı)
     */
    @Transactional(readOnly = true)
    public Page<ParentSchoolListItemResponse> getListItems(Long parentUserId, Long listId, ParentSchoolListSearchRequest request) {

        // Liste yetki kontrol
        ParentSchoolList list = parentSchoolListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeyi görme yetkiniz yok");
        }

        // Sayfalama ve sıralama
        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Filtreleme
        Page<ParentSchoolListItem> items = parentSchoolListItemRepository.findWithFilters(
                parentUserId,
                SchoolItemStatus.ACTIVE,
                request.getOnlyFavorites(),
                request.getOnlyBlocked(),
                request.getMinStarRating(),
                request.getMaxStarRating(),
                request.getKeyword(),
                pageable
        );

        return items.map(item -> {
            ParentSchoolListItemResponse response = converterService.mapToDto(item);
            // Randevu bilgilerini ekle
            addAppointmentInfo(response, parentUserId, item.getSchool().getId());
            return response;
        });
    }

    // ========================= HELPER METHODS =========================

    private Sort createSort(String sortBy, String direction) {
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return switch (sortBy) {
            case "starRating" -> Sort.by(sortDirection, "starRating");
            case "addedAt" -> Sort.by(sortDirection, "createdAt");
            case "schoolName" -> Sort.by(sortDirection, "school.name");
            case "priorityOrder" -> Sort.by(sortDirection, "priorityOrder");
            default -> Sort.by(sortDirection, "priorityOrder");
        };
    }

    private void addAppointmentInfo(ParentSchoolListItemResponse response, Long parentUserId, Long schoolId) {
        try {
            // Randevu bilgilerini getir - bu method AppointmentRepository'de olmalı
            List<Appointment> appointments = appointmentRepository
                    .findByParentUserIdAndSchoolIdOrderByAppointmentDateDesc(parentUserId, schoolId);

            response.setAppointmentCount(appointments.size());

            if (!appointments.isEmpty()) {
                // En son randevu
                Optional<Appointment> lastAppointment = appointments.stream()
                        .filter(a -> a.getAppointmentDate().isBefore(LocalDateTime.now().toLocalDate()))
                        .findFirst();
                lastAppointment.ifPresent(appointment ->
                        response.setLastAppointmentDate(appointment.getAppointmentDate().atTime(appointment.getStartTime())));

                // Gelecek randevu
                Optional<Appointment> nextAppointment = appointments.stream()
                        .filter(a -> a.getAppointmentDate().isAfter(LocalDateTime.now().toLocalDate()) ||
                                (a.getAppointmentDate().equals(LocalDateTime.now().toLocalDate()) &&
                                        a.getStartTime().isAfter(LocalDateTime.now().toLocalTime())))
                        .findFirst();
                nextAppointment.ifPresent(appointment ->
                        response.setNextAppointmentDate(appointment.getAppointmentDate().atTime(appointment.getStartTime())));
            }
        } catch (Exception e) {
            log.warn("Error getting appointment info for school: {} parent: {}", schoolId, parentUserId, e);
            response.setAppointmentCount(0);
        }
    }

    // ========================= BULK OPERATIONS =========================

    /**
     * Toplu işlemler (favori, blok, kaldır vb.)
     */
    public void performBulkOperation(Long parentUserId, BulkSchoolListOperationRequest request) {

        List<ParentSchoolListItem> items = parentSchoolListItemRepository
                .findByParentSchoolList_ParentUserIdAndSchoolIdIn(parentUserId, request.getSchoolIds());

        switch (request.getOperation().toUpperCase()) {
            case "FAVORITE" -> items.forEach(item -> item.setIsFavorite(true));
            case "UNFAVORITE" -> items.forEach(item -> item.setIsFavorite(false));
            case "BLOCK" -> items.forEach(item -> item.setIsBlocked(true));
            case "UNBLOCK" -> items.forEach(item -> item.setIsBlocked(false));
            case "REMOVE" -> {
                items.forEach(item -> item.setStatus(SchoolItemStatus.REMOVED));
                // Liste sayılarını güncelle
                items.stream()
                        .collect(java.util.stream.Collectors.groupingBy(ParentSchoolListItem::getParentSchoolList))
                        .forEach((list, itemsInList) -> {
                            list.setSchoolCount(Math.max(0, list.getSchoolCount() - itemsInList.size()));
                            parentSchoolListRepository.save(list);
                        });
            }
            case "MOVE" -> {
                if (request.getTargetListId() == null) {
                    throw new RuntimeException("Hedef liste belirtilmeli");
                }
                ParentSchoolList targetList = parentSchoolListRepository.findById(request.getTargetListId())
                        .orElseThrow(() -> new RuntimeException("Hedef liste bulunamadı"));

                if (!targetList.getParentUser().getId().equals(parentUserId)) {
                    throw new RuntimeException("Hedef listeye erişim yetkiniz yok");
                }

                items.forEach(item -> {
                    ParentSchoolList oldList = item.getParentSchoolList();
                    item.setParentSchoolList(targetList);

                    // Sayıları güncelle
                    oldList.setSchoolCount(Math.max(0, oldList.getSchoolCount() - 1));
                    targetList.setSchoolCount(targetList.getSchoolCount() + 1);
                    parentSchoolListRepository.save(oldList);
                    parentSchoolListRepository.save(targetList);
                });
            }
            case "RATE" -> {
                if (request.getStarRating() != null) {
                    items.forEach(item -> item.setStarRating(request.getStarRating()));
                }
            }
            default -> throw new RuntimeException("Geçersiz işlem: " + request.getOperation());
        }

        items.forEach(item -> item.setLastUpdatedAt(LocalDateTime.now()));
        parentSchoolListItemRepository.saveAll(items);

    }

    /**
     * Hızlı ekleme (arama sonuçlarından)
     */
    public List<ParentSchoolListItemResponse> quickAddSchools(Long parentUserId, QuickAddSchoolRequest request) {

        // Hedef listeyi belirle
        ParentSchoolList targetList;
        if (request.getParentSchoolListId() != null) {
            targetList = parentSchoolListRepository.findById(request.getParentSchoolListId())
                    .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));
        } else {
            targetList = parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Default liste bulunamadı"));
        }

        // Yetki kontrol
        if (!targetList.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeye okul ekleme yetkiniz yok");
        }

        List<ParentSchoolListItem> addedItems = request.getSchoolIds().stream()
                .filter(schoolId -> !parentSchoolListItemRepository.existsByParentSchoolListIdAndSchoolId(targetList.getId(), schoolId))
                .map(schoolId -> {
                    School school = schoolRepository.findById(schoolId)
                            .orElseThrow(() -> new RuntimeException("Okul bulunamadı: " + schoolId));

                    return ParentSchoolListItem.builder()
                            .parentSchoolList(targetList)
                            .school(school)
                            .status(SchoolItemStatus.ACTIVE)
                            .isFavorite(request.getMarkAsFavorite())
                            .isBlocked(false)
                            .priorityOrder(0)
                            .addedFromSearch(request.getSearchQuery())
                            .lastUpdatedAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        List<ParentSchoolListItem> savedItems = parentSchoolListItemRepository.saveAll(addedItems);

        // Liste sayısını güncelle
        targetList.setSchoolCount(targetList.getSchoolCount() + savedItems.size());
        parentSchoolListRepository.save(targetList);


        return converterService.mapToListItemDto(savedItems);
    }

    // ========================= NOTE OPERATIONS =========================

    /**
     * Okul notu oluştur
     */
    public ParentSchoolNoteResponse createSchoolNote(Long parentUserId, CreateParentSchoolNoteRequest request) {

        // Okul var mı kontrol et
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("Okul bulunamadı"));

        // Liste öğesi kontrolü (isteğe bağlı)
        ParentSchoolListItem listItem = null;
        if (request.getParentSchoolListItemId() != null) {
            listItem = parentSchoolListItemRepository.findById(request.getParentSchoolListItemId())
                    .orElseThrow(() -> new RuntimeException("Liste öğesi bulunamadı"));

            if (!listItem.getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
                throw new RuntimeException("Bu listeye not ekleme yetkiniz yok");
            }
        }

        NoteCategory category = request.getCategory() != null ?
                NoteCategory.valueOf(request.getCategory()) : NoteCategory.GENERAL;

        ParentSchoolNote note = ParentSchoolNote.builder()
                .parentSchoolListItem(listItem)
                .school(school)
                .noteTitle(request.getNoteTitle())
                .noteContent(request.getNoteContent())
                .category(category)
                .isImportant(request.getIsImportant())
                .reminderDate(request.getReminderDate())
                .source(request.getSource())
                .build();

        ParentSchoolNote savedNote = parentSchoolNoteRepository.save(note);

        return converterService.mapToDto(savedNote);
    }

    /**
     * Okul notunu güncelle
     */
    public ParentSchoolNoteResponse updateSchoolNote(Long parentUserId, Long noteId, UpdateParentSchoolNoteRequest request) {

        ParentSchoolNote note = parentSchoolNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));

        // Yetki kontrol
        if (note.getParentSchoolListItem() != null &&
                !note.getParentSchoolListItem().getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu notu güncelleme yetkiniz yok");
        }

        // Alanları güncelle
        if (request.getNoteTitle() != null) note.setNoteTitle(request.getNoteTitle());
        if (request.getNoteContent() != null) note.setNoteContent(request.getNoteContent());
        if (request.getCategory() != null) note.setCategory(NoteCategory.valueOf(request.getCategory()));
        if (request.getIsImportant() != null) note.setIsImportant(request.getIsImportant());
        if (request.getReminderDate() != null) note.setReminderDate(request.getReminderDate());
        if (request.getSource() != null) note.setSource(request.getSource());

        ParentSchoolNote updatedNote = parentSchoolNoteRepository.save(note);

        return converterService.mapToDto(updatedNote);
    }

    /**
     * Liste notu oluştur
     */
    public ParentListNoteResponse createListNote(Long parentUserId, CreateParentListNoteRequest request) {

        ParentSchoolList list = parentSchoolListRepository.findById(request.getParentSchoolListId())
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeye not ekleme yetkiniz yok");
        }

        ParentListNote note = ParentListNote.builder()
                .parentSchoolList(list)
                .noteTitle(request.getNoteTitle())
                .noteContent(request.getNoteContent())
                .isImportant(request.getIsImportant())
                .reminderDate(request.getReminderDate())
                .build();

        ParentListNote savedNote = parentListNoteRepository.save(note);

        return converterService.mapToDto(savedNote);
    }

    // ========================= DASHBOARD =========================

    /**
     * Dashboard özet bilgileri
     */
    @Transactional(readOnly = true)
    public ParentSchoolListDashboardResponse getDashboard(Long parentUserId) {

        // Temel istatistikler
        Integer totalLists = parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE);
        Integer favoriteSchools = parentSchoolListItemRepository.countFavoritesByParentUserId(parentUserId);
        Integer blockedSchools = parentSchoolListItemRepository.countBlockedByParentUserId(parentUserId);

        // Ziyaret istatistikleri
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusDays(30);
        List<ParentSchoolListItem> upcomingVisits = parentSchoolListItemRepository
                .findUpcomingVisits(parentUserId, now, futureLimit);

        Integer pendingVisits = upcomingVisits.size();

        // Tamamlanan ziyaretler (son 30 gün)
        LocalDateTime pastLimit = now.minusDays(30);
        Integer completedVisits = Math.toIntExact(parentSchoolListItemRepository
                .findByParentSchoolList_ParentUserIdAndSchoolIdIn(parentUserId, List.of())
                .stream()
                .filter(item -> item.getVisitCompletedDate() != null &&
                        item.getVisitCompletedDate().isAfter(pastLimit))
                .count());

        // Randevulu okul sayısı
        Integer schoolsWithAppointments = 0;
        try {
            List<Appointment> appointments = appointmentRepository.findByParentUserIdAndAppointmentDateAfter(
                    parentUserId, now.toLocalDate());
            schoolsWithAppointments = appointments.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Appointment::getSchool))
                    .size();
        } catch (Exception e) {
            log.warn("Error calculating schools with appointments", e);
        }

        // Toplam okul sayısı
        Integer totalSchools = Math.toIntExact(parentSchoolListItemRepository
                .findByParentSchoolList_ParentUserIdAndSchoolIdIn(parentUserId, List.of())
                .stream()
                .filter(item -> item.getStatus() == SchoolItemStatus.ACTIVE)
                .count());

        // Son listeler
        Pageable recentListsPageable = PageRequest.of(0, 5);
        List<ParentSchoolList> recentLists = parentSchoolListRepository
                .findByParentUserIdOrderByIsDefaultDescLastAccessedAtDesc(parentUserId)
                .stream()
                .limit(5)
                .toList();

        // Son eklenen okullar
        List<ParentSchoolListItem> recentlyAddedSchools = parentSchoolListItemRepository
                .findRecentlyAddedByParentUserId(parentUserId, PageRequest.of(0, 5));

        // Yaklaşan hatırlatıcılar
        List<ParentSchoolNote> upcomingReminderNotes = parentSchoolNoteRepository
                .findUpcomingReminders(parentUserId, now, futureLimit);

        Integer upcomingReminders = upcomingReminderNotes.size();

        return converterService.mapToDashboardDto(
                parentUserId,
                totalLists,
                totalSchools,
                favoriteSchools,
                blockedSchools,
                schoolsWithAppointments,
                pendingVisits,
                completedVisits,
                upcomingReminders, // upcomingReminderCount olarak kullanılıyor
                recentLists,
                recentlyAddedSchools,
                upcomingReminderNotes
        );
    }

    // ========================= SEARCH & FILTER =========================

    /**
     * Tüm okulları filtreli arama
     */
    @Transactional(readOnly = true)
    public Page<ParentSchoolListItemResponse> searchAllSchools(Long parentUserId, ParentSchoolListSearchRequest request) {

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ParentSchoolListItem> items = parentSchoolListItemRepository.findWithFilters(
                parentUserId,
                SchoolItemStatus.ACTIVE,
                request.getOnlyFavorites(),
                request.getOnlyBlocked(),
                request.getMinStarRating(),
                request.getMaxStarRating(),
                request.getKeyword(),
                pageable
        );

        return items.map(item -> {
            ParentSchoolListItemResponse response = converterService.mapToDto(item);
            addAppointmentInfo(response, parentUserId, item.getSchool().getId());
            return response;
        });
    }

    /**
     * Okul notlarını getir
     */
    @Transactional(readOnly = true)
    public List<ParentSchoolNoteResponse> getSchoolNotes(Long parentUserId, Long schoolId) {

        List<ParentSchoolNote> notes = parentSchoolNoteRepository
                .findBySchoolIdAndParentSchoolListItem_ParentSchoolList_ParentUserIdOrderByCreatedAtDesc(
                        schoolId, parentUserId);

        return converterService.mapToSchoolNoteDto(notes);
    }

    /**
     * Liste notlarını getir
     */
    @Transactional(readOnly = true)
    public List<ParentListNoteResponse> getListNotes(Long parentUserId, Long listId) {

        // Liste yetki kontrol
        ParentSchoolList list = parentSchoolListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı"));

        if (!list.getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu listeyi görme yetkiniz yok");
        }

        List<ParentListNote> notes = parentListNoteRepository
                .findByParentSchoolListIdOrderByCreatedAtDesc(listId);

        return converterService.mapToListNoteDto(notes);
    }

    /**
     * Yaklaşan hatırlatıcıları getir
     */
    @Transactional(readOnly = true)
    public List<ParentSchoolNoteResponse> getUpcomingReminders(Long parentUserId, Integer days) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusDays(days != null ? days : 7);

        List<ParentSchoolNote> reminders = parentSchoolNoteRepository
                .findUpcomingReminders(parentUserId, now, futureLimit);

        return converterService.mapToSchoolNoteDto(reminders);
    }

    // ========================= UTILITY METHODS =========================

    /**
     * Default liste oluştur (ilk kullanım için)
     */
    public ParentSchoolListResponse createDefaultList(Long parentUserId) {

        CreateParentSchoolListRequest request = CreateParentSchoolListRequest.builder()
                .listName("Favorilerim")
                .description("İlgilendiğim okullar")
                .isDefault(true)
                .colorCode("#2563eb")
                .icon("heart")
                .build();

        return createList(parentUserId, request);
    }

    /**
     * Okul listede var mı kontrol et
     */
    @Transactional(readOnly = true)
    public boolean isSchoolInAnyList(Long parentUserId, Long schoolId) {
        List<ParentSchoolListItem> items = parentSchoolListItemRepository
                .findBySchoolIdAndParentSchoolList_ParentUserIdAndStatus(
                        schoolId, parentUserId, SchoolItemStatus.ACTIVE);
        return !items.isEmpty();
    }

    /**
     * Okulun hangi listelerde olduğunu getir
     */
    @Transactional(readOnly = true)
    public List<ParentSchoolListSummaryResponse> getSchoolLists(Long parentUserId, Long schoolId) {
        List<ParentSchoolListItem> items = parentSchoolListItemRepository
                .findBySchoolIdAndParentSchoolList_ParentUserIdAndStatus(
                        schoolId, parentUserId, SchoolItemStatus.ACTIVE);

        List<ParentSchoolList> lists = items.stream()
                .map(ParentSchoolListItem::getParentSchoolList)
                .distinct()
                .toList();

        return converterService.mapToSummaryDto(lists);
    }

    /**
     * Not silme işlemi
     */
    public void deleteSchoolNote(Long parentUserId, Long noteId) {

        ParentSchoolNote note = parentSchoolNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));

        // Yetki kontrol
        if (note.getParentSchoolListItem() != null &&
                !note.getParentSchoolListItem().getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu notu silme yetkiniz yok");
        }

        parentSchoolNoteRepository.delete(note);
    }

    /**
     * Liste notunu silme işlemi
     */
    public void deleteListNote(Long parentUserId, Long noteId) {

        ParentListNote note = parentListNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));

        // Yetki kontrol
        if (!note.getParentSchoolList().getParentUser().getId().equals(parentUserId)) {
            throw new RuntimeException("Bu notu silme yetkiniz yok");
        }

        parentListNoteRepository.delete(note);
    }


    @Transactional(readOnly = true)
    public List<ParentSchoolListSummaryResponse> getParentListsSummary(Long parentUserId) {

        List<ParentSchoolList> lists = parentSchoolListRepository
                .findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE);

        return converterService.mapToSummaryDto(lists);
    }


    public ParentSearchList createSearch(ParentSearchList parentSearchList) {
        return parentSearchListRepository.saveAndFlush(parentSearchList);
    }

    public List<ParentSearchList> getParentSearchList(Long parentId) {
        return parentSearchListRepository.findByParentId(parentId);
    }

    public void deleteSearchList(Long listId) {
        parentSearchListRepository.deleteById(listId);
    }

    public List<SchoolSearchResultDto> getListSchools(Long listId) {
        List<School> schools = parentSchoolListRepository.findSchoolsByListId(listId);
        return institutionConverterService.mapSchoolsToSearchResultDto(schools);
    }
}