package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.Category;
import com.genixo.education.search.entity.supply.CategoryAttribute;
import com.genixo.education.search.supply.CategoryAttributeRepository;
import com.genixo.education.search.supply.CategoryRepository;
import com.genixo.education.search.supply.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductRepository productRepository;

    // ================================ CREATE ================================

    @Transactional
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        log.info("Creating new category: {}", createDto.getName());

        // Validate parent if provided
        Category parent = null;
        if (createDto.getParentId() != null) {
            parent = categoryRepository.findById(createDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", createDto.getParentId()));
            
            if (!parent.getIsActive()) {
                throw new BusinessException("Cannot create subcategory under inactive parent category");
            }
        }

        // Check name uniqueness at same level
        Long parentId = parent != null ? parent.getId() : null;
        if (categoryRepository.existsByNameAndParentId(createDto.getName(), parentId)) {
            throw BusinessException.duplicateResource("Category", "name", createDto.getName());
        }

        Category category = new Category();
        category.setName(createDto.getName());
        category.setDescription(createDto.getDescription());
        category.setParent(parent);
        category.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);
        
        // Set display order
        if (createDto.getDisplayOrder() != null) {
            category.setDisplayOrder(createDto.getDisplayOrder());
        } else {
            Integer maxOrder;
            if (parent != null) {
                maxOrder = categoryRepository.findMaxDisplayOrderByParentId(parent.getId());
            } else {
                maxOrder = categoryRepository.findMaxDisplayOrderForRootCategories();
            }
            category.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 0);
        }
        
        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    // ================================ READ ================================

    public Page<CategoryDto> getAllCategories(String searchTerm, Boolean isActive, Long parentId, Pageable pageable) {
        log.info("Fetching categories with searchTerm: {}, isActive: {}, parentId: {}", searchTerm, isActive, parentId);

        Page<Category> categories = categoryRepository.searchCategories(searchTerm, isActive, parentId, pageable);
        return categories.map(this::mapToDto);
    }

    public List<CategoryTreeDto> getCategoryTree(Boolean includeInactive) {
        log.info("Fetching category tree, includeInactive: {}", includeInactive);

        List<Category> rootCategories = includeInactive != null && includeInactive
                ? categoryRepository.findByParentIsNull()
                : categoryRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();

        return rootCategories.stream()
                .map(cat -> mapToTreeDtoRecursive(cat, includeInactive))
                .collect(Collectors.toList());
    }

    private CategoryTreeDto mapToTreeDtoRecursive(Category category, Boolean includeInactive) {
        List<Category> subCategories = includeInactive != null && includeInactive
                ? categoryRepository.findByParentIdOrderByDisplayOrderAsc(category.getId())
                : categoryRepository.findSubCategoriesByParentId(category.getId(), true);

        List<CategoryTreeDto> subCategoryDtos = subCategories.stream()
                .map(cat -> mapToTreeDtoRecursive(cat, includeInactive))
                .collect(Collectors.toList());

        return CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .isActive(category.getIsActive())
                .displayOrder(category.getDisplayOrder())
                .subCategoryCount(subCategories.size())
                .subCategories(subCategoryDtos)
                .build();
    }

    public CategoryDto getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        return mapToDto(category);
    }

    public List<CategoryDto> getSubCategories(Long id, Boolean includeInactive) {
        log.info("Fetching subcategories for category ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }

        List<Category> subCategories = includeInactive != null && includeInactive
                ? categoryRepository.findByParentId(id)
                : categoryRepository.findSubCategoriesByParentId(id, true);

        return subCategories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================================ UPDATE ================================

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryUpdateDto updateDto) {
        log.info("Updating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Check name uniqueness if changed
        if (updateDto.getName() != null && !updateDto.getName().equals(category.getName())) {
            Long currentParentId = category.getParent() != null ? category.getParent().getId() : null;
            if (categoryRepository.existsByNameAndParentIdAndIdNot(updateDto.getName(), currentParentId, id)) {
                throw BusinessException.duplicateResource("Category", "name", updateDto.getName());
            }
            category.setName(updateDto.getName());
        }

        if (updateDto.getDescription() != null) {
            category.setDescription(updateDto.getDescription());
        }

        // Update parent if changed
        if (updateDto.getParentId() != null) {
            if (category.getParent() == null || !category.getParent().getId().equals(updateDto.getParentId())) {
                Category newParent = categoryRepository.findById(updateDto.getParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent Category", updateDto.getParentId()));
                
                // Prevent circular reference
                if (isDescendant(newParent, id)) {
                    throw new BusinessException("Cannot set parent: would create circular reference");
                }
                
                category.setParent(newParent);
            }
        } else if (updateDto.getParentId() == null && category.getParent() != null) {
            category.setParent(null);
        }

        if (updateDto.getIsActive() != null) {
            category.setIsActive(updateDto.getIsActive());
        }

        if (updateDto.getDisplayOrder() != null) {
            category.setDisplayOrder(updateDto.getDisplayOrder());
        }

        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ ACTIVATE/DEACTIVATE ================================

    @Transactional
    public CategoryDto activateCategory(Long id) {
        log.info("Activating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setIsActive(true);
        Category updated = categoryRepository.save(category);
        log.info("Category activated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public CategoryDto deactivateCategory(Long id) {
        log.info("Deactivating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Check if category has active subcategories
        Long activeSubCount = categoryRepository.countActiveByParentId(id);
        if (activeSubCount > 0) {
            throw new BusinessException("Cannot deactivate category with active subcategories");
        }

        category.setIsActive(false);
        Category updated = categoryRepository.save(category);
        log.info("Category deactivated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ REORDER ================================

    @Transactional
    public CategoryDto reorderCategory(Long id, CategoryReorderDto reorderDto) {
        log.info("Reordering category with ID: {} to position: {}", id, reorderDto.getDisplayOrder());

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setDisplayOrder(reorderDto.getDisplayOrder());
        Category updated = categoryRepository.save(category);
        log.info("Category reordered successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ DELETE ================================

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Check if category has subcategories
        Long subCount = categoryRepository.countByParentId(id);
        if (subCount > 0) {
            throw BusinessException.resourceInUse("Category", subCount + " subcategories");
        }

        // Check if category has products
        Long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw BusinessException.resourceInUse("Category", productCount + " products");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with ID: {}", id);
    }

    // ================================ ATTRIBUTES ================================

    public List<CategoryAttributeDto> getCategoryAttributes(Long categoryId) {
        log.info("Fetching attributes for category ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }

        List<CategoryAttribute> attributes = categoryAttributeRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
        return attributes.stream()
                .map(this::mapToAttributeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryAttributeDto createCategoryAttribute(Long categoryId, CategoryAttributeCreateDto createDto) {
        log.info("Creating attribute for category ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        // Check attribute name uniqueness
        if (categoryAttributeRepository.existsByCategoryIdAndAttributeName(categoryId, createDto.getAttributeName())) {
            throw BusinessException.duplicateResource("Category Attribute", "attributeName", createDto.getAttributeName());
        }

        CategoryAttribute attribute = new CategoryAttribute();
        attribute.setCategory(category);
        attribute.setAttributeName(createDto.getAttributeName());
        attribute.setPossibleValues(createDto.getPossibleValues());
        attribute.setIsRequired(createDto.getIsRequired() != null ? createDto.getIsRequired() : false);
        
        if (createDto.getDisplayOrder() != null) {
            attribute.setDisplayOrder(createDto.getDisplayOrder());
        } else {
            Integer maxOrder = categoryAttributeRepository.findMaxDisplayOrderByCategoryId(categoryId);
            attribute.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 0);
        }

        CategoryAttribute saved = categoryAttributeRepository.save(attribute);
        log.info("Category attribute created successfully with ID: {}", saved.getId());

        return mapToAttributeDto(saved);
    }

    @Transactional
    public CategoryAttributeDto updateCategoryAttribute(Long categoryId, Long attributeId, CategoryAttributeUpdateDto updateDto) {
        log.info("Updating attribute ID: {} for category ID: {}", attributeId, categoryId);

        CategoryAttribute attribute = categoryAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Category Attribute", attributeId));

        if (!attribute.getCategory().getId().equals(categoryId)) {
            throw new ResourceNotFoundException("Category Attribute", attributeId);
        }

        // Check attribute name uniqueness if changed
        if (updateDto.getAttributeName() != null && !updateDto.getAttributeName().equals(attribute.getAttributeName())) {
            if (categoryAttributeRepository.existsByCategoryIdAndAttributeNameAndIdNot(categoryId, updateDto.getAttributeName(), attributeId)) {
                throw BusinessException.duplicateResource("Category Attribute", "attributeName", updateDto.getAttributeName());
            }
            attribute.setAttributeName(updateDto.getAttributeName());
        }

        if (updateDto.getPossibleValues() != null) {
            attribute.setPossibleValues(updateDto.getPossibleValues());
        }

        if (updateDto.getIsRequired() != null) {
            attribute.setIsRequired(updateDto.getIsRequired());
        }

        if (updateDto.getDisplayOrder() != null) {
            attribute.setDisplayOrder(updateDto.getDisplayOrder());
        }

        CategoryAttribute updated = categoryAttributeRepository.save(attribute);
        log.info("Category attribute updated successfully with ID: {}", attributeId);

        return mapToAttributeDto(updated);
    }

    @Transactional
    public void deleteCategoryAttribute(Long categoryId, Long attributeId) {
        log.info("Deleting attribute ID: {} for category ID: {}", attributeId, categoryId);

        CategoryAttribute attribute = categoryAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Category Attribute", attributeId));

        if (!attribute.getCategory().getId().equals(categoryId)) {
            throw new ResourceNotFoundException("Category Attribute", attributeId);
        }

        categoryAttributeRepository.delete(attribute);
        log.info("Category attribute deleted successfully with ID: {}", attributeId);
    }

    // ================================ HELPER METHODS ================================

    private boolean isDescendant(Category potentialAncestor, Long categoryId) {
        if (potentialAncestor.getId().equals(categoryId)) {
            return true;
        }
        if (potentialAncestor.getParent() == null) {
            return false;
        }
        return isDescendant(potentialAncestor.getParent(), categoryId);
    }


    private CategoryDto mapToDto(Category category) {
        Long subCount = categoryRepository.countByParentId(category.getId());
        
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .isActive(category.getIsActive())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .subCategoryCount(subCount.intValue())
                .build();
    }

    private CategoryAttributeDto mapToAttributeDto(CategoryAttribute attribute) {
        return CategoryAttributeDto.builder()
                .id(attribute.getId())
                .categoryId(attribute.getCategory().getId())
                .categoryName(attribute.getCategory().getName())
                .attributeName(attribute.getAttributeName())
                .possibleValues(attribute.getPossibleValues())
                .isRequired(attribute.getIsRequired())
                .displayOrder(attribute.getDisplayOrder())
                .build();
    }
}

