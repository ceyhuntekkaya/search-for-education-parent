package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.ProductDto;
import com.genixo.education.search.dto.supply.WishlistCheckDto;
import com.genixo.education.search.dto.supply.WishlistCreateDto;
import com.genixo.education.search.dto.supply.WishlistDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.supply.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/supply/wishlists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wishlist Management", description = "APIs for managing wishlists in the supply system")
public class WishlistController {

    private final WishlistService wishlistService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Add to wishlist", description = "Add a product to user's wishlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product added to wishlist successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Product is already in wishlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(
            @Valid @RequestBody WishlistCreateDto createDto,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        WishlistDto wishlistDto = wishlistService.addToWishlist(user.getId(), createDto);

        ApiResponse<WishlistDto> response = ApiResponse.success(wishlistDto, "Product added to wishlist successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get user wishlist", description = "Get all products in user's wishlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ProductDto>>> getUserWishlist(
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        List<ProductDto> wishlist = wishlistService.getUserWishlist(user.getId());

        ApiResponse<List<ProductDto>> response = ApiResponse.success(wishlist, "Wishlist retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check product in wishlist", description = "Check if a product is in user's wishlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully")
    })
    public ResponseEntity<ApiResponse<WishlistCheckDto>> checkProductInWishlist(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        WishlistCheckDto checkDto = wishlistService.checkProductInWishlist(user.getId(), productId);

        ApiResponse<WishlistCheckDto> response = ApiResponse.success(checkDto, "Check completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove from wishlist", description = "Remove a product from wishlist by wishlist ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product removed from wishlist successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Wishlist item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "You can only remove your own wishlist items")
    })
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @Parameter(description = "Wishlist ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        wishlistService.removeFromWishlist(user.getId(), id);

        ApiResponse<Void> response = ApiResponse.success(null, "Product removed from wishlist successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/by-product/{productId}")
    @Operation(summary = "Remove product from wishlist", description = "Remove a product from wishlist by product ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product removed from wishlist successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or wishlist item not found")
    })
    public ResponseEntity<ApiResponse<Void>> removeByProduct(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        wishlistService.removeByProduct(user.getId(), productId);

        ApiResponse<Void> response = ApiResponse.success(null, "Product removed from wishlist successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

