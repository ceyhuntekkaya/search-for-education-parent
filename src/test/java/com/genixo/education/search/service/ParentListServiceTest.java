package com.genixo.education.search.service;

import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.ListStatus;
import com.genixo.education.search.enumaration.SchoolItemStatus;
import com.genixo.education.search.repository.appointment.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.converter.ParentListConverterService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParentListService Tests")
class ParentListServiceTest {

    @Mock
    private ParentSchoolListRepository parentSchoolListRepository;
    @Mock
    private ParentSchoolListItemRepository parentSchoolListItemRepository;
    @Mock
    private SchoolRepository schoolRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParentListConverterService converterService;

    @InjectMocks
    private ParentListService parentListService;

    private User parentUser;
    private School testSchool;
    private ParentSchoolList defaultList;
    private ParentSchoolList regularList;
    private ParentSchoolListItem listItem;
    private CreateParentSchoolListRequest validCreateRequest;
    private ParentSchoolListResponse expectedResponse;

    @BeforeEach
    void setUp() {
        parentUser = new User();
        parentUser.setId(1L);
        parentUser.setEmail("parent@example.com");

        testSchool = new School();
        testSchool.setId(1L);
        testSchool.setName("Test School");

        defaultList = ParentSchoolList.builder()
                .parentUser(parentUser)
                .listName("Default List")
                .status(ListStatus.ACTIVE)
                .isDefault(true)
                .schoolCount(0)
                .lastAccessedAt(LocalDateTime.now())
                .build();
        defaultList.setId(1L);

        regularList = ParentSchoolList.builder()
                .parentUser(parentUser)
                .listName("Regular List")
                .status(ListStatus.ACTIVE)
                .isDefault(false)
                .schoolCount(1)
                .lastAccessedAt(LocalDateTime.now())
                .build();
        regularList.setId(2L);

        listItem = ParentSchoolListItem.builder()
                .parentSchoolList(defaultList)
                .school(testSchool)
                .status(SchoolItemStatus.ACTIVE)
                .isFavorite(false)
                .isBlocked(false)
                .priorityOrder(1)
                .build();
        listItem.setId(1L);

        validCreateRequest = CreateParentSchoolListRequest.builder()
                .listName("New List")
                .description("Test description")
                .isDefault(false)
                .colorCode("#FF5733")
                .icon("star")
                .notes("Test notes")
                .build();

        expectedResponse = ParentSchoolListResponse.builder()
                .id(1L)
                .listName("New List")
                .description("Test description")
                .status(ListStatus.ACTIVE)
                .isDefault(false)
                .schoolCount(0)
                .build();
    }

    // ================================ CREATE LIST TESTS ================================

    @Nested
    @DisplayName("createList() Tests")
    class CreateListTests {

        @Test
        @DisplayName("Should create list successfully with valid data")
        void shouldCreateListSuccessfullyWithValidData() {
            // Given
            Long parentUserId = 1L;
            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, "New List")).thenReturn(false);
            when(parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE)).thenReturn(1);
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            ParentSchoolListResponse result = parentListService.createList(parentUserId, validCreateRequest);

            // Then
            assertNotNull(result);
            assertEquals("New List", result.getListName());

            verify(userRepository).findById(parentUserId);
            verify(parentSchoolListRepository).existsByParentUserIdAndListName(parentUserId, "New List");
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getListName().equals("New List") &&
                            list.getDescription().equals("Test description") &&
                            list.getStatus() == ListStatus.ACTIVE &&
                            list.getParentUser().getId().equals(parentUserId) &&
                            list.getSchoolCount().equals(0)
            ));
            verify(converterService).mapToDto(any(ParentSchoolList.class));
        }

        @Test
        @DisplayName("Should throw RuntimeException when parent not found")
        void shouldThrowRuntimeExceptionWhenParentNotFound() {
            // Given
            Long nonExistentParentId = 999L;
            when(userRepository.findById(nonExistentParentId)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.createList(nonExistentParentId, validCreateRequest));

            assertEquals("Parent user not found", exception.getMessage());

            verify(userRepository).findById(nonExistentParentId);
            verifyNoInteractions(parentSchoolListRepository, converterService);
        }

        @Test
        @DisplayName("Should throw RuntimeException when list name already exists")
        void shouldThrowRuntimeExceptionWhenListNameExists() {
            // Given
            Long parentUserId = 1L;
            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, "New List")).thenReturn(true);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.createList(parentUserId, validCreateRequest));

            assertEquals("Bu isimde bir liste zaten mevcut", exception.getMessage());

            verify(userRepository).findById(parentUserId);
            verify(parentSchoolListRepository).existsByParentUserIdAndListName(parentUserId, "New List");
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should make first list default automatically")
        void shouldMakeFirstListDefaultAutomatically() {
            // Given
            Long parentUserId = 1L;
            CreateParentSchoolListRequest firstListRequest = CreateParentSchoolListRequest.builder()
                    .listName("First List")
                    .isDefault(false) // Even though user doesn't want it default
                    .build();

            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, "First List")).thenReturn(false);
            when(parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE)).thenReturn(0); // No lists yet
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            parentListService.createList(parentUserId, firstListRequest);

            // Then
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getIsDefault() == true // Should be forced to true for first list
            ));
        }

        @Test
        @DisplayName("Should remove default from existing list when new default is created")
        void shouldRemoveDefaultFromExistingListWhenNewDefaultCreated() {
            // Given
            Long parentUserId = 1L;
            CreateParentSchoolListRequest newDefaultRequest = CreateParentSchoolListRequest.builder()
                    .listName("New Default")
                    .isDefault(true)
                    .build();

            ParentSchoolList existingDefault = ParentSchoolList.builder()
                    .isDefault(true)
                    .build();
            existingDefault.setId(10L);
            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, "New Default")).thenReturn(false);
            when(parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE)).thenReturn(1);
            when(parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE))
                    .thenReturn(Optional.of(existingDefault));
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            parentListService.createList(parentUserId, newDefaultRequest);

            // Then
            verify(parentSchoolListRepository).findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE);
            verify(parentSchoolListRepository, times(2)).save(any(ParentSchoolList.class)); // Save existing + new
            // Verify existing default is updated
            assertEquals(false, existingDefault.getIsDefault());
        }

        @Test
        @DisplayName("Should set all fields correctly from request")
        void shouldSetAllFieldsCorrectlyFromRequest() {
            // Given
            Long parentUserId = 1L;
            when(userRepository.findById(parentUserId)).thenReturn(Optional.of(parentUser));
            when(parentSchoolListRepository.existsByParentUserIdAndListName(parentUserId, "New List")).thenReturn(false);
            when(parentSchoolListRepository.countByParentUserIdAndStatus(parentUserId, ListStatus.ACTIVE)).thenReturn(1);
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            parentListService.createList(parentUserId, validCreateRequest);

            // Then
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getListName().equals("New List") &&
                            list.getDescription().equals("Test description") &&
                            list.getStatus() == ListStatus.ACTIVE &&
                            list.getIsDefault().equals(false) &&
                            list.getColorCode().equals("#FF5733") &&
                            list.getIcon().equals("star") &&
                            list.getNotes().equals("Test notes") &&
                            list.getSchoolCount().equals(0) &&
                            list.getParentUser().getId().equals(parentUserId) &&
                            list.getLastAccessedAt() != null
            ));
        }
    }

    // ================================ UPDATE LIST TESTS ================================

    @Nested
    @DisplayName("updateList() Tests")
    class UpdateListTests {

        private UpdateParentSchoolListRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = UpdateParentSchoolListRequest.builder()
                    .listName("Updated List")
                    .description("Updated description")
                    .colorCode("#00FF00")
                    .icon("heart")
                    .build();
        }

        @Test
        @DisplayName("Should update list successfully with valid data")
        void shouldUpdateListSuccessfullyWithValidData() {
            // Given
            Long parentUserId = 1L;
            Long listId = 1L;

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(regularList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            ParentSchoolListResponse result = parentListService.updateList(parentUserId, listId, updateRequest);

            // Then
            assertNotNull(result);
            verify(parentSchoolListRepository).findById(listId);
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getListName().equals("Updated List") &&
                            list.getDescription().equals("Updated description") &&
                            list.getColorCode().equals("#00FF00") &&
                            list.getIcon().equals("heart") &&
                            list.getLastAccessedAt() != null
            ));
            verify(converterService).mapToDto(any(ParentSchoolList.class));
        }

        @Test
        @DisplayName("Should throw RuntimeException when list not found")
        void shouldThrowRuntimeExceptionWhenListNotFound() {
            // Given
            Long parentUserId = 1L;
            Long nonExistentListId = 999L;

            when(parentSchoolListRepository.findById(nonExistentListId)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.updateList(parentUserId, nonExistentListId, updateRequest));

            assertEquals("Liste bulunamadı", exception.getMessage());

            verify(parentSchoolListRepository).findById(nonExistentListId);
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when user has no permission")
        void shouldThrowRuntimeExceptionWhenUserHasNoPermission() {
            // Given
            Long wrongUserId = 999L;
            Long listId = 1L;

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.updateList(wrongUserId, listId, updateRequest));

            assertEquals("Bu listeyi güncelleme yetkiniz yok", exception.getMessage());

            verify(parentSchoolListRepository).findById(listId);
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when updated name already exists")
        void shouldThrowRuntimeExceptionWhenUpdatedNameExists() {
            // Given
            Long parentUserId = 1L;
            Long listId = 1L;

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));
            when(parentSchoolListRepository.existsByParentUserIdAndListNameAndIdNot(
                    parentUserId, "Updated List", listId)).thenReturn(true);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.updateList(parentUserId, listId, updateRequest));

            assertEquals("Bu isimde bir liste zaten mevcut", exception.getMessage());

            verify(parentSchoolListRepository).findById(listId);
            verify(parentSchoolListRepository).existsByParentUserIdAndListNameAndIdNot(parentUserId, "Updated List", listId);
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle default switching correctly")
        void shouldHandleDefaultSwitchingCorrectly() {
            // Given
            Long parentUserId = 1L;
            Long listId = 2L; // Regular list (not default)

            UpdateParentSchoolListRequest makeDefaultRequest = UpdateParentSchoolListRequest.builder()
                    .isDefault(true)
                    .build();

            ParentSchoolList existingDefault = ParentSchoolList.builder()
                    .isDefault(true)
                    .build();
            existingDefault.setId(10L);

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));
            when(parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE))
                    .thenReturn(Optional.of(existingDefault));
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(regularList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            parentListService.updateList(parentUserId, listId, makeDefaultRequest);

            // Then
            verify(parentSchoolListRepository).findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE);
            verify(parentSchoolListRepository, times(2)).save(any(ParentSchoolList.class)); // Save existing + updated

            // Verify existing default is removed and new one is set
            assertEquals(false, existingDefault.getIsDefault());
            assertEquals(true, regularList.getIsDefault());
        }

        @Test
        @DisplayName("Should handle partial updates correctly")
        void shouldHandlePartialUpdatesCorrectly() {
            // Given
            Long parentUserId = 1L;
            Long listId = 1L;

            UpdateParentSchoolListRequest partialUpdate = UpdateParentSchoolListRequest.builder()
                    .description("Only description updated")
                    // Other fields are null
                    .build();

            String originalName = regularList.getListName();
            String originalIcon = regularList.getIcon();

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(regularList);
            when(converterService.mapToDto(any(ParentSchoolList.class))).thenReturn(expectedResponse);

            // When
            parentListService.updateList(parentUserId, listId, partialUpdate);

            // Then
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getListName().equals(originalName) && // Should remain unchanged
                            list.getDescription().equals("Only description updated") && // Should be updated
                            list.getIcon().equals(originalIcon) // Should remain unchanged
            ));
        }
    }

    // ================================ DELETE LIST TESTS ================================

    @Nested
    @DisplayName("deleteList() Tests")
    class DeleteListTests {

        @Test
        @DisplayName("Should delete non-default list successfully")
        void shouldDeleteNonDefaultListSuccessfully() {
            // Given
            Long parentUserId = 1L;
            Long listId = 2L;

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(regularList));

            // When
            assertDoesNotThrow(() -> parentListService.deleteList(parentUserId, listId));

            // Then
            verify(parentSchoolListRepository).findById(listId);
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getStatus() == ListStatus.DELETED
            ));
        }

        @Test
        @DisplayName("Should throw RuntimeException when list not found")
        void shouldThrowRuntimeExceptionWhenListNotFound() {
            // Given
            Long parentUserId = 1L;
            Long nonExistentListId = 999L;

            when(parentSchoolListRepository.findById(nonExistentListId)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.deleteList(parentUserId, nonExistentListId));

            assertEquals("Liste bulunamadı", exception.getMessage());

            verify(parentSchoolListRepository).findById(nonExistentListId);
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when user has no permission")
        void shouldThrowRuntimeExceptionWhenUserHasNoPermission() {
            // Given
            Long wrongUserId = 999L;
            Long listId = 1L;

            when(parentSchoolListRepository.findById(listId)).thenReturn(Optional.of(defaultList));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.deleteList(wrongUserId, listId));

            assertEquals("Bu listeyi silme yetkiniz yok", exception.getMessage());

            verify(parentSchoolListRepository).findById(listId);
            verify(parentSchoolListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should transfer default to another list when deleting default list")
        void shouldTransferDefaultToAnotherListWhenDeletingDefaultList() {
            // Given
            Long parentUserId = 1L;
            Long defaultListId = 1L;

            ParentSchoolList otherList1 = ParentSchoolList.builder()
                    .isDefault(false)
                    .build();
            otherList1.setId(3L);
            ParentSchoolList otherList2 = ParentSchoolList.builder()
                    .isDefault(false)
                    .build();
            otherList2.setId(4L);

            when(parentSchoolListRepository.findById(defaultListId)).thenReturn(Optional.of(defaultList));
            when(parentSchoolListRepository.findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE))
                    .thenReturn(List.of(defaultList, otherList1, otherList2));

            // When
            parentListService.deleteList(parentUserId, defaultListId);

            // Then
            verify(parentSchoolListRepository).findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE);
            verify(parentSchoolListRepository, times(2)).save(any(ParentSchoolList.class)); // Save new default + deleted list

            // Verify first other list becomes default
            assertEquals(true, otherList1.getIsDefault());
            assertEquals(ListStatus.DELETED, defaultList.getStatus());
        }

        @Test
        @DisplayName("Should handle deletion when no other lists exist")
        void shouldHandleDeletionWhenNoOtherListsExist() {
            // Given
            Long parentUserId = 1L;
            Long defaultListId = 1L;

            when(parentSchoolListRepository.findById(defaultListId)).thenReturn(Optional.of(defaultList));
            when(parentSchoolListRepository.findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(parentUserId, ListStatus.ACTIVE))
                    .thenReturn(List.of(defaultList)); // Only the list being deleted

            // When
            parentListService.deleteList(parentUserId, defaultListId);

            // Then
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getStatus() == ListStatus.DELETED
            ));
            // Should only save once (the deleted list), no default transfer
            verify(parentSchoolListRepository, times(1)).save(any(ParentSchoolList.class));
        }
    }

    // ================================ ADD SCHOOL TO LIST TESTS ================================

    @Nested
    @DisplayName("addSchoolToList() Tests")
    class AddSchoolToListTests {

        private AddSchoolToListRequest addSchoolRequest;

        @BeforeEach
        void setUp() {
            addSchoolRequest = AddSchoolToListRequest.builder()
                    .schoolId(1L)
                    .parentSchoolListId(1L)
                    .starRating(4)
                    .isFavorite(true)
                    .priorityOrder(1)
                    .personalNotes("Great school")
                    .pros("Excellent teachers")
                    .cons("Expensive")
                    .tags("private,bilingual")
                    .addedFromSearch("school search")
                    .build();
        }

        @Test
        @DisplayName("Should add school to specified list successfully")
        void shouldAddSchoolToSpecifiedListSuccessfully() {
            // Given
            Long parentUserId = 1L;

            ParentSchoolListItemResponse expectedItemResponse = ParentSchoolListItemResponse.builder()
                    .id(1L)
                    .schoolId(1L)
                    .schoolName("Test School")
                    .build();

            when(schoolRepository.findById(1L)).thenReturn(Optional.of(testSchool));
            when(parentSchoolListRepository.findById(1L)).thenReturn(Optional.of(defaultList));
            when(parentSchoolListItemRepository.existsByParentSchoolListIdAndSchoolId(1L, 1L)).thenReturn(false);
            when(parentSchoolListItemRepository.save(any(ParentSchoolListItem.class))).thenReturn(listItem);
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolListItem.class))).thenReturn(expectedItemResponse);

            // When
            ParentSchoolListItemResponse result = parentListService.addSchoolToList(parentUserId, addSchoolRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getSchoolId());

            verify(schoolRepository).findById(1L);
            verify(parentSchoolListRepository).findById(1L);
            verify(parentSchoolListItemRepository).existsByParentSchoolListIdAndSchoolId(1L, 1L);
            verify(parentSchoolListItemRepository).save(argThat(item ->
                    item.getSchool().getId().equals(1L) &&
                            item.getParentSchoolList().getId().equals(1L) &&
                            item.getStarRating().equals(4) &&
                            item.getIsFavorite().equals(true) &&
                            item.getPersonalNotes().equals("Great school") &&
                            item.getPros().equals("Excellent teachers") &&
                            item.getCons().equals("Expensive") &&
                            item.getTags().equals("private,bilingual") &&
                            item.getAddedFromSearch().equals("school search") &&
                            item.getStatus() == SchoolItemStatus.ACTIVE
            ));

            // Verify school count is updated
            verify(parentSchoolListRepository).save(argThat(list ->
                    list.getSchoolCount().equals(1) // Should be incremented
            ));
        }

        @Test
        @DisplayName("Should add school to default list when no list specified")
        void shouldAddSchoolToDefaultListWhenNoListSpecified() {
            // Given
            Long parentUserId = 1L;
            AddSchoolToListRequest requestWithoutListId = AddSchoolToListRequest.builder()
                    .schoolId(1L)
                    .parentSchoolListId(null) // No list specified
                    .build();

            ParentSchoolListItemResponse expectedItemResponse = ParentSchoolListItemResponse.builder()
                    .id(1L)
                    .schoolId(1L)
                    .build();

            when(schoolRepository.findById(1L)).thenReturn(Optional.of(testSchool));
            when(parentSchoolListRepository.findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE))
                    .thenReturn(Optional.of(defaultList));
            when(parentSchoolListItemRepository.existsByParentSchoolListIdAndSchoolId(1L, 1L)).thenReturn(false);
            when(parentSchoolListItemRepository.save(any(ParentSchoolListItem.class))).thenReturn(listItem);
            when(parentSchoolListRepository.save(any(ParentSchoolList.class))).thenReturn(defaultList);
            when(converterService.mapToDto(any(ParentSchoolListItem.class))).thenReturn(expectedItemResponse);

            // When
            ParentSchoolListItemResponse result = parentListService.addSchoolToList(parentUserId, requestWithoutListId);

            // Then
            assertNotNull(result);
            verify(parentSchoolListRepository).findByParentUserIdAndIsDefaultTrueAndStatus(parentUserId, ListStatus.ACTIVE);
            verify(parentSchoolListRepository, never()).findById(any()); // Should not look for specific list
        }

        @Test
        @DisplayName("Should throw RuntimeException when school not found")
        void shouldThrowRuntimeExceptionWhenSchoolNotFound() {
            // Given
            Long parentUserId = 1L;
            when(schoolRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.addSchoolToList(parentUserId, addSchoolRequest));

            assertEquals("Okul bulunamadı", exception.getMessage());

            verify(schoolRepository).findById(1L);
            verifyNoInteractions(parentSchoolListRepository, parentSchoolListItemRepository);
        }

        @Test
        @DisplayName("Should throw RuntimeException when list not found")
        void shouldThrowRuntimeExceptionWhenListNotFound() {
            // Given
            Long parentUserId = 1L;
            when(schoolRepository.findById(1L)).thenReturn(Optional.of(testSchool));
            when(parentSchoolListRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.addSchoolToList(parentUserId, addSchoolRequest));

            assertEquals("Liste bulunamadı", exception.getMessage());

            verify(schoolRepository).findById(1L);
            verify(parentSchoolListRepository).findById(1L);
            verifyNoInteractions(parentSchoolListItemRepository);
        }

        @Test
        @DisplayName("Should throw RuntimeException when user has no permission to list")
        void shouldThrowRuntimeExceptionWhenUserHasNoPermissionToList() {
            // Given
            Long wrongUserId = 999L;
            when(schoolRepository.findById(1L)).thenReturn(Optional.of(testSchool));
            when(parentSchoolListRepository.findById(1L)).thenReturn(Optional.of(defaultList));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parentListService.addSchoolToList(wrongUserId, addSchoolRequest)
            );
            assertEquals("Bu listeye okul ekleme yetkiniz yok", exception.getMessage());
            verify(schoolRepository).findById(1L);
            verify(parentSchoolListRepository).findById(1L);
            verifyNoInteractions(parentSchoolListItemRepository);
        }
    }
}