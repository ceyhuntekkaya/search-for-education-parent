package com.genixo.education.search.service;


import com.genixo.education.search.repository.content.*;
import com.genixo.education.search.repository.insitution.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlugGeneratorService {

    private final BrandRepository brandRepository;
    private final CampusRepository campusRepository;
    private final SchoolRepository schoolRepository;
    private final PostRepository postRepository;
    private final GalleryRepository galleryRepository;

    // Pattern for removing non-alphanumeric characters except hyphens
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");
    private static final Pattern LEADING_TRAILING_HYPHENS = Pattern.compile("^-|-$");

    /**
     * Generate a unique slug for the given input and entity type
     * @param input The text to convert to slug
     * @param entityType The type of entity (brand, campus, school, post, gallery)
     * @return A unique slug
     */
    public String generateUniqueSlug(String input, String entityType) {
        if (!StringUtils.hasText(input)) {
            input = entityType + "-" + System.currentTimeMillis();
        }

        // Generate base slug
        String baseSlug = createSlugFromText(input);

        // Ensure uniqueness
        return ensureUniqueSlug(baseSlug, entityType);
    }

    /**
     * Generate a unique slug with maximum length limit
     * @param input The text to convert to slug
     * @param entityType The type of entity
     * @param maxLength Maximum length of the slug
     * @return A unique slug with length constraint
     */
    public String generateUniqueSlug(String input, String entityType, int maxLength) {
        String slug = generateUniqueSlug(input, entityType);

        if (slug.length() <= maxLength) {
            return slug;
        }

        // Truncate and ensure uniqueness again
        String truncated = slug.substring(0, maxLength);
        // Remove trailing hyphen if exists
        truncated = LEADING_TRAILING_HYPHENS.matcher(truncated).replaceAll("");

        return ensureUniqueSlug(truncated, entityType);
    }

    /**
     * Create a slug from text input
     * @param input The input text
     * @return Formatted slug (not guaranteed to be unique)
     */
    public String createSlugFromText(String input) {
        if (!StringUtils.hasText(input)) {
            return "item-" + System.currentTimeMillis();
        }

        log.debug("Creating slug from input: {}", input);

        // Step 1: Convert to lowercase and handle Turkish characters
        String slug = input.toLowerCase();

        // Step 2: Handle Turkish specific characters
        slug = replaceTurkishCharacters(slug);

        // Step 3: Normalize unicode characters (remove accents, etc.)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);

        // Step 4: Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Step 5: Remove all non-alphanumeric characters except hyphens
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // Step 6: Replace multiple consecutive hyphens with single hyphen
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");

        // Step 7: Remove leading and trailing hyphens
        slug = LEADING_TRAILING_HYPHENS.matcher(slug).replaceAll("");

        // Step 8: Ensure minimum length
        if (!StringUtils.hasText(slug) || slug.length() < 2) {
            slug = "item-" + System.currentTimeMillis();
        }

        log.debug("Generated base slug: {}", slug);
        return slug;
    }

    /**
     * Replace Turkish characters with their ASCII equivalents
     * @param input Input string
     * @return String with Turkish characters replaced
     */
    private String replaceTurkishCharacters(String input) {
        return input
                .replace("ç", "c")
                .replace("ğ", "g")
                .replace("ı", "i")
                .replace("ö", "o")
                .replace("ş", "s")
                .replace("ü", "u")
                .replace("İ", "i")
                .replace("Ç", "c")
                .replace("Ğ", "g")
                .replace("Ö", "o")
                .replace("Ş", "s")
                .replace("Ü", "u");
    }

    /**
     * Ensure the slug is unique for the given entity type
     * @param baseSlug The base slug to check
     * @param entityType The entity type
     * @return A unique slug
     */
    private String ensureUniqueSlug(String baseSlug, String entityType) {
        String candidateSlug = baseSlug;
        int attempt = 0;
        int maxAttempts = 100; // Prevent infinite loops

        while (attempt < maxAttempts && isSlugExists(candidateSlug, entityType)) {
            attempt++;

            if (attempt == 1) {
                // First attempt: add timestamp
                candidateSlug = baseSlug + "-" + System.currentTimeMillis();
            } else {
                // Subsequent attempts: add random number
                int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
                candidateSlug = baseSlug + "-" + randomNum;
            }
        }

        if (attempt >= maxAttempts) {
            // Fallback: use UUID-like string
            candidateSlug = baseSlug + "-" + System.currentTimeMillis() + "-" +
                    ThreadLocalRandom.current().nextInt(10000, 99999);

            log.warn("Maximum slug generation attempts reached for base: {}. Using fallback: {}",
                    baseSlug, candidateSlug);
        }

        log.debug("Generated unique slug: {} (attempts: {})", candidateSlug, attempt);
        return candidateSlug;
    }

    /**
     * Check if slug already exists for the given entity type
     * @param slug The slug to check
     * @param entityType The entity type
     * @return true if slug exists, false otherwise
     */
    private boolean isSlugExists(String slug, String entityType) {
        return switch (entityType.toLowerCase()) {
            case "brand" -> brandRepository.existsBySlug(slug);
            case "campus" -> campusRepository.existsBySlug(slug);
            case "school" -> schoolRepository.existsBySlug(slug);
            case "post" -> postRepository.existsBySlug(slug);
            case "gallery" -> galleryRepository.existsBySlug(slug);
            default -> {
                log.warn("Unknown entity type for slug check: {}", entityType);
                yield false;
            }
        };
    }

    /**
     * Validate if a slug is properly formatted
     * @param slug The slug to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidSlug(String slug) {
        if (!StringUtils.hasText(slug)) {
            return false;
        }

        // Check length constraints
        if (slug.length() < 2 || slug.length() > 255) {
            return false;
        }

        // Check format: only lowercase letters, numbers, and hyphens
        // Must start and end with alphanumeric character
        String pattern = "^[a-z0-9]+([a-z0-9-]*[a-z0-9]+)*$";
        return slug.matches(pattern);
    }

    /**
     * Generate slug suggestions based on input
     * @param input The input text
     * @param entityType The entity type
     * @param count Number of suggestions to generate
     * @return List of slug suggestions
     */
    public java.util.List<String> generateSlugSuggestions(String input, String entityType, int count) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        String baseSlug = createSlugFromText(input);

        // Add the base slug if it's available
        if (!isSlugExists(baseSlug, entityType)) {
            suggestions.add(baseSlug);
            count--;
        }

        // Generate variations
        int generated = 0;
        for (int i = 1; generated < count && i <= count * 3; i++) {
            String variation = baseSlug + "-" + i;
            if (!isSlugExists(variation, entityType)) {
                suggestions.add(variation);
                generated++;
            }
        }

        // If still need more, add some creative variations
        if (generated < count) {
            String[] suffixes = {"new", "latest", "updated", "fresh", "modern", "plus"};
            for (String suffix : suffixes) {
                if (generated >= count) break;
                String variation = baseSlug + "-" + suffix;
                if (!isSlugExists(variation, entityType)) {
                    suggestions.add(variation);
                    generated++;
                }
            }
        }

        return suggestions;
    }

    /**
     * Update slug when entity name changes
     * @param currentSlug Current slug
     * @param newName New name to generate slug from
     * @param entityType Entity type
     * @param forceUpdate Whether to force update even if current slug is still valid
     * @return Updated slug or current slug if no change needed
     */
    public String updateSlugIfNeeded(String currentSlug, String newName, String entityType, boolean forceUpdate) {
        // If current slug is valid and we're not forcing update, keep it
        if (!forceUpdate && isValidSlug(currentSlug) && !isSlugExists(currentSlug, entityType)) {
            return currentSlug;
        }

        // Generate new slug from new name
        String newBaseSlug = createSlugFromText(newName);

        // If new slug would be the same as current, keep current
        if (newBaseSlug.equals(currentSlug)) {
            return currentSlug;
        }

        // Generate unique slug
        return ensureUniqueSlug(newBaseSlug, entityType);
    }

    /**
     * Batch generate unique slugs for multiple items
     * @param inputs List of input texts
     * @param entityType Entity type
     * @return List of unique slugs
     */
    public java.util.List<String> generateBatchSlugs(java.util.List<String> inputs, String entityType) {
        java.util.List<String> slugs = new java.util.ArrayList<>();
        java.util.Set<String> usedSlugs = new java.util.HashSet<>();

        for (String input : inputs) {
            String baseSlug = createSlugFromText(input);
            String uniqueSlug = baseSlug;
            int attempt = 0;

            // Ensure uniqueness within this batch and in database
            while ((usedSlugs.contains(uniqueSlug) || isSlugExists(uniqueSlug, entityType)) && attempt < 100) {
                attempt++;
                if (attempt == 1) {
                    uniqueSlug = baseSlug + "-" + System.currentTimeMillis();
                } else {
                    int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
                    uniqueSlug = baseSlug + "-" + randomNum;
                }
            }

            usedSlugs.add(uniqueSlug);
            slugs.add(uniqueSlug);
        }

        return slugs;
    }
}

// ================================ USAGE EXAMPLES ================================

/*
Example usage in services:

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final SlugGeneratorService slugGenerator;

    public SchoolDto createSchool(SchoolCreateDto createDto, HttpServletRequest request) {
        // Generate unique slug
        String slug = slugGenerator.generateUniqueSlug(createDto.getName(), "school");

        School school = new School();
        school.setName(createDto.getName());
        school.setSlug(slug);
        // ... rest of the logic
    }

    public SchoolDto updateSchool(Long id, SchoolUpdateDto updateDto, HttpServletRequest request) {
        School school = findSchoolById(id);

        // Update slug if name changed
        if (!school.getName().equals(updateDto.getName())) {
            String newSlug = slugGenerator.updateSlugIfNeeded(
                school.getSlug(),
                updateDto.getName(),
                "school",
                false // Don't force update
            );
            school.setSlug(newSlug);
        }

        school.setName(updateDto.getName());
        // ... rest of the logic
    }
}

@Service
@RequiredArgsConstructor
public class ContentService {
    private final SlugGeneratorService slugGenerator;

    public PostDto createPost(PostCreateDto createDto, HttpServletRequest request) {
        // Generate slug with length limit for posts
        String slug = slugGenerator.generateUniqueSlug(createDto.getTitle(), "post", 100);

        Post post = new Post();
        post.setTitle(createDto.getTitle());
        post.setSlug(slug);
        // ... rest of the logic
    }
}
*/