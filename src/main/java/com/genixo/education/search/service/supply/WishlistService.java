package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.WishlistCheckDto;
import com.genixo.education.search.dto.supply.WishlistCreateDto;
import com.genixo.education.search.dto.supply.WishlistDto;
import com.genixo.education.search.entity.supply.Product;
import com.genixo.education.search.entity.supply.Wishlist;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.supply.ProductRepository;
import com.genixo.education.search.supply.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    // ================================ WISHLIST CRUD ================================

    @Transactional
    public WishlistDto addToWishlist(Long userId, WishlistCreateDto createDto) {
        log.info("Adding product ID: {} to wishlist for user ID: {}", createDto.getProductId(), userId);

        // Validate product
        Product product = productRepository.findById(createDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.getProductId()));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userId, createDto.getProductId())) {
            throw new BusinessException("Product is already in wishlist");
        }

        // Create wishlist entry
        Wishlist wishlist = new Wishlist();
        User user = new User();
        user.setId(userId);
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist saved = wishlistRepository.save(wishlist);
        log.info("Product added to wishlist successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    public List<WishlistDto> getUserWishlist(Long userId) {
        log.info("Fetching wishlist for user ID: {}", userId);

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public WishlistCheckDto checkProductInWishlist(Long userId, Long productId) {
        log.info("Checking if product ID: {} is in wishlist for user ID: {}", productId, userId);

        Optional<Wishlist> wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);
        
        return WishlistCheckDto.builder()
                .isInWishlist(wishlist.isPresent())
                .wishlistId(wishlist.map(Wishlist::getId).orElse(null))
                .build();
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long wishlistId) {
        log.info("Removing wishlist item ID: {} for user ID: {}", wishlistId, userId);

        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist", wishlistId));

        // Verify ownership
        if (!wishlist.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only remove your own wishlist items");
        }

        wishlistRepository.delete(wishlist);
        log.info("Wishlist item removed successfully with ID: {}", wishlistId);
    }

    @Transactional
    public void removeByProduct(Long userId, Long productId) {
        log.info("Removing product ID: {} from wishlist for user ID: {}", productId, userId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        // Check if in wishlist
        Optional<Wishlist> wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (wishlist.isEmpty()) {
            throw new ResourceNotFoundException("Wishlist", "productId", productId);
        }

        wishlistRepository.delete(wishlist.get());
        log.info("Product removed from wishlist successfully");
    }

    // ================================ HELPER METHODS ================================

    private WishlistDto mapToDto(Wishlist wishlist) {
        Product product = wishlist.getProduct();

        return WishlistDto.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSku(product.getSku())
                .productMainImageUrl(product.getMainImageUrl())
                .supplierCompanyName(product.getSupplier().getCompanyName())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}

