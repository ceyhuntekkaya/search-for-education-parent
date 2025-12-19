package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.IdNotAvailableException;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.supply.*;
import com.genixo.education.search.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    @Value("${storage.base}")
    private String TEST_OUTPUT_PATH;

    private final Integer supplyCount = 5;
    private final Integer productCount = 10;

    private final Long userId = 1L;
    private final Long campusId = 1L;

    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ProductDocumentRepository productDocumentRepository;
    private final ProductVariantRepository productVariantRepository;
    private final RFQRepository rfqRepository;
    private final RFQItemRepository rfqItemRepository;
    private final RFQInvitationRepository rfqInvitationRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SupplierRatingRepository supplierRatingRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductPaymentRepository productPaymentRepository;
    private final ProductConversationRepository productConversationRepository;
    private final ProductMessageRepository productMessageRepository;
    private final NotificationRepository notificationRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CampusRepository campusRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();
    private final String[] loremWords = {
            "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
            "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
            "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud", "exercitation"
    };

    //@EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeCourseData() throws IdNotAvailableException, IOException {
        log.info("Starting supply system data initialization...");

        // Check if data already exists
        if (supplierRepository.count() > 0) {
            log.info("Supply data already exists. Skipping initialization.");
            return;
        }

        // Get or create Campus
        Campus campus = campusRepository.findById(campusId)
                .orElseThrow(() -> new RuntimeException("Campus not found with ID: " + campusId));

        // Get or create User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 1. Create Categories
        List<Category> categories = createCategories();

        // 2. Create Suppliers
        List<Supplier> suppliers = createSuppliers();

        // 3. Create Products for each Supplier
        List<Product> allProducts = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            List<Product> products = createProductsForSupplier(supplier, categories);
            allProducts.addAll(products);
        }

        // 4. Create Product Images, Attributes, Discounts, Documents, Variants
        for (Product product : allProducts) {
            createProductImages(product);
            createProductAttributes(product);
            createProductDiscounts(product);
            createProductDocuments(product);
            createProductVariants(product);
        }

        // 5. Create RFQs
        List<RFQ> rfqs = createRFQs(campus);

        // 6. Create RFQ Items
        for (RFQ rfq : rfqs) {
            createRFQItems(rfq, categories);
        }

        // 7. Create RFQ Invitations
        for (RFQ rfq : rfqs) {
            createRFQInvitations(rfq, suppliers);
        }

        // 8. Create Quotations
        List<Quotation> quotations = createQuotations(rfqs, suppliers);

        // 9. Create Quotation Items
        for (Quotation quotation : quotations) {
            createQuotationItems(quotation);
        }

        // 10. Create Orders
        List<Order> orders = createOrders(campus, suppliers, quotations, allProducts);

        // 11. Create Order Items
        for (Order order : orders) {
            createOrderItems(order, allProducts);
        }

        // 12. Create Supplier Ratings
        for (Order order : orders) {
            if (random.nextBoolean()) {
                createSupplierRating(order);
            }
        }

        // 13. Create Wishlists
        createWishlists(user, allProducts);

        // 14. Create Product Payments
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.DELIVERED) {
                createProductPayment(order);
            }
        }

        // 15. Create Product Conversations
        List<ProductConversation> conversations = createProductConversations(campus, suppliers, allProducts);

        // 16. Create Product Messages
        for (ProductConversation conversation : conversations) {
            createProductMessages(conversation, user);
        }

        // 17. Create Notifications
        createNotifications(user, rfqs, quotations, orders);

        // 18. Create Category Attributes
        for (Category category : categories) {
            if (random.nextBoolean()) {
                createCategoryAttributes(category);
            }
        }

        log.info("Supply system data initialization completed successfully!");
        log.info("Created: {} suppliers, {} products, {} RFQs, {} quotations, {} orders",
                suppliers.size(), allProducts.size(), rfqs.size(), quotations.size(), orders.size());
    }

    private List<Category> createCategories() {
        log.info("Creating categories...");
        List<Category> categories = new ArrayList<>();

        String[] rootCategoryNames = {"Elektronik", "Mobilya", "Kırtasiye", "Temizlik", "Gıda"};
        for (int i = 0; i < rootCategoryNames.length; i++) {
            Category category = new Category();
            category.setName(rootCategoryNames[i]);
            category.setDescription(generateLoremText(20));
            category.setIsActive(true);
            category.setDisplayOrder(i);
            category.setCreatedAt(LocalDateTime.now());
            category.setCreatedBy(userId);
            category = categoryRepository.save(category);
            categories.add(category);

            // Create subcategories
            for (int j = 0; j < 3; j++) {
                Category subCategory = new Category();
                subCategory.setName(rootCategoryNames[i] + " - " + getSubCategoryName(i, j));
                subCategory.setDescription(generateLoremText(15));
                subCategory.setParent(category);
                subCategory.setIsActive(true);
                subCategory.setDisplayOrder(j);
                subCategory.setCreatedAt(LocalDateTime.now());
                subCategory.setCreatedBy(userId);
                subCategory = categoryRepository.save(subCategory);
                categories.add(subCategory);
            }
        }

        return categories;
    }

    private String getSubCategoryName(int rootIndex, int subIndex) {
        String[][] subCategories = {
                {"Bilgisayar", "Telefon", "Tablet"},
                {"Masa", "Sandalye", "Dolap"},
                {"Kalem", "Defter", "Silgi"},
                {"Temizlik Malzemesi", "Çöp Torbası", "Bulaşık Deterjanı"},
                {"Atıştırmalık", "İçecek", "Kahvaltılık"}
        };
        return subCategories[rootIndex][subIndex];
    }

    private List<Supplier> createSuppliers() {
        log.info("Creating {} suppliers...", supplyCount);
        List<Supplier> suppliers = new ArrayList<>();

        for (int i = 1; i <= supplyCount; i++) {
            Supplier supplier = new Supplier();
            supplier.setCompanyName("Tedarikçi " + i + " A.Ş.");
            supplier.setTaxNumber(String.format("%010d", 1000000000L + i));
            supplier.setEmail("supplier" + i + "@example.com");
            supplier.setPhone("+90 555 " + String.format("%07d", 1000000 + i));
            supplier.setAddress(generateLoremText(30));
            supplier.setIsActive(true);
            supplier.setDescription(generateLoremText(50));
            supplier.setAverageRating(BigDecimal.valueOf(3.5 + random.nextDouble() * 1.5));
            supplier.setCreatedAt(LocalDateTime.now());
            supplier.setCreatedBy(userId);
            supplier = supplierRepository.save(supplier);
            suppliers.add(supplier);
        }

        return suppliers;
    }

    private List<Product> createProductsForSupplier(Supplier supplier, List<Category> categories) {
        log.info("Creating {} products for supplier: {}", productCount, supplier.getCompanyName());
        List<Product> products = new ArrayList<>();
        List<Category> activeCategories = categories.stream()
                .filter(c -> c.getIsActive() && c.getParent() != null)
                .toList();

        for (int i = 1; i <= productCount; i++) {
            Product product = new Product();
            product.setSupplier(supplier);
            product.setCategory(activeCategories.get(random.nextInt(activeCategories.size())));
            product.setName(generateProductName(supplier.getId(), i));
            product.setSku("SKU-" + supplier.getId() + "-" + String.format("%03d", i));
            product.setDescription(generateLoremText(100));
            product.setTechnicalSpecs(generateLoremText(80));
            product.setStatus(ProductStatus.values()[random.nextInt(ProductStatus.values().length)]);
            product.setStockTrackingType(StockTrackingType.LIMITED);
            product.setStockQuantity(random.nextInt(1000) + 10);
            product.setMinStockLevel(random.nextInt(50) + 5);
            product.setBasePrice(BigDecimal.valueOf(10 + random.nextDouble() * 990));
            product.setCurrency(Currency.TRY);
            product.setTaxRate(new BigDecimal("20.00"));
            product.setMinOrderQuantity(random.nextInt(5) + 1);
            product.setDeliveryDays(random.nextInt(14) + 3);
            product.setMainImageUrl("https://via.placeholder.com/400x300?text=Product+" + i);
            product.setCreatedAt(LocalDateTime.now());
            product.setCreatedBy(userId);
            product = productRepository.save(product);
            products.add(product);
        }

        return products;
    }

    private String generateProductName(Long supplierId, int productIndex) {
        String[] productTypes = {"Ürün", "Malzeme", "Ekipman", "Aksesuar", "Parça"};
        return productTypes[random.nextInt(productTypes.length)] + " " + supplierId + "-" + productIndex;
    }

    private void createProductImages(Product product) {
        int imageCount = random.nextInt(3) + 1;
        for (int i = 0; i < imageCount; i++) {
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl("https://via.placeholder.com/400x300?text=Image+" + (i + 1));
            image.setDisplayOrder(i);
            image.setCreatedAt(LocalDateTime.now());
            image.setCreatedBy(userId);
            productImageRepository.save(image);
        }
    }

    private void createProductAttributes(Product product) {
        int attrCount = random.nextInt(5) + 2;
        String[] attributeNames = {"Renk", "Boyut", "Ağırlık", "Malzeme", "Garanti"};
        String[] attributeValues = {"Kırmızı", "Büyük", "500g", "Plastik", "2 Yıl"};

        for (int i = 0; i < attrCount && i < attributeNames.length; i++) {
            ProductAttribute attribute = new ProductAttribute();
            attribute.setProduct(product);
            attribute.setAttributeName(attributeNames[i]);
            attribute.setAttributeValue(attributeValues[i] + " " + (i + 1));
            attribute.setCreatedBy(userId);
            productAttributeRepository.save(attribute);
        }
    }

    private void createProductDiscounts(Product product) {
        if (random.nextBoolean()) {
            ProductDiscount discount = new ProductDiscount();
            discount.setProduct(product);
            discount.setDiscountName("Özel İndirim");
            discount.setDiscountType(DiscountType.PERCENTAGE);
            discount.setDiscountValue(BigDecimal.valueOf(10 + random.nextInt(20)));
            discount.setMinQuantity(random.nextInt(5) + 1);
            discount.setStartDate(LocalDate.now().minusDays(10));
            discount.setEndDate(LocalDate.now().plusDays(30));
            discount.setIsActive(true);
            discount.setCreatedAt(LocalDateTime.now());
            discount.setCreatedBy(userId);
            productDiscountRepository.save(discount);
        }
    }

    private void createProductDocuments(Product product) {
        if (random.nextBoolean()) {
            ProductDocument document = new ProductDocument();
            document.setProduct(product);
            document.setDocumentName("Ürün Kataloğu");
            document.setDocumentUrl("https://example.com/docs/catalog.pdf");
            document.setDocumentType("certificate");
            document.setCreatedAt(LocalDateTime.now());
            document.setCreatedBy(userId);
            productDocumentRepository.save(document);
        }
    }

    private void createProductVariants(Product product) {
        if (random.nextBoolean()) {
            int variantCount = random.nextInt(3) + 1;
            for (int i = 0; i < variantCount; i++) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                variant.setVariantName("Varyant " + (i + 1));
                variant.setSku(product.getSku() + "-V" + (i + 1));
                variant.setPriceAdjustment(BigDecimal.valueOf(random.nextDouble() * 50 - 25));
                variant.setStockQuantity(random.nextInt(500) + 10);
                variant.setIsActive(true);
                variant.setCreatedBy(userId);
                productVariantRepository.save(variant);
            }
        }
    }

    private List<RFQ> createRFQs(Campus campus) {
        log.info("Creating RFQs...");
        List<RFQ> rfqs = new ArrayList<>();
        int rfqCount = supplyCount * 2;

        for (int i = 1; i <= rfqCount; i++) {
            RFQ rfq = new RFQ();
            rfq.setCompany(campus);
            rfq.setTitle("Alım İlanı " + i);
            rfq.setDescription(generateLoremText(150));
            rfq.setRfqType(RFQType.values()[random.nextInt(RFQType.values().length)]);
            rfq.setStatus(RFQStatus.values()[random.nextInt(RFQStatus.values().length)]);
            rfq.setSubmissionDeadline(LocalDate.now().plusDays(random.nextInt(30) + 7));
            rfq.setExpectedDeliveryDate(LocalDate.now().plusDays(random.nextInt(60) + 30));
            rfq.setPaymentTerms(generateLoremText(50));
            rfq.setEvaluationCriteria(generateLoremText(60));
            rfq.setTechnicalRequirements(generateLoremText(80));
            rfq.setCreatedAt(LocalDateTime.now());
            rfq.setCreatedBy(userId);
            rfq = rfqRepository.save(rfq);
            rfqs.add(rfq);
        }

        return rfqs;
    }

    private void createRFQItems(RFQ rfq, List<Category> categories) {
        int itemCount = random.nextInt(5) + 2;
        List<Category> activeCategories = categories.stream()
                .filter(c -> c.getIsActive() && c.getParent() != null)
                .toList();

        for (int i = 0; i < itemCount; i++) {
            RFQItem item = new RFQItem();
            item.setRfq(rfq);
            item.setCategory(activeCategories.get(random.nextInt(activeCategories.size())));
            item.setItemName("İlan Kalemi " + (i + 1));
            item.setSpecifications(generateLoremText(50));
            item.setQuantity(random.nextInt(100) + 10);
            item.setUnit("adet");
            item.setCreatedBy(userId);
            rfqItemRepository.save(item);
        }
    }

    private void createRFQInvitations(RFQ rfq, List<Supplier> suppliers) {
        if (rfq.getRfqType() == RFQType.INVITED) {
            int inviteCount = random.nextInt(suppliers.size()) + 1;
            for (int i = 0; i < inviteCount && i < suppliers.size(); i++) {
                RFQInvitation invitation = new RFQInvitation();
                invitation.setRfq(rfq);
                invitation.setSupplier(suppliers.get(i));
                invitation.setInvitedAt(LocalDateTime.now());
                invitation.setCreatedBy(userId);
                rfqInvitationRepository.save(invitation);
            }
        }
    }

    private List<Quotation> createQuotations(List<RFQ> rfqs, List<Supplier> suppliers) {
        log.info("Creating quotations...");
        List<Quotation> quotations = new ArrayList<>();

        for (RFQ rfq : rfqs) {
            if (rfq.getStatus() == RFQStatus.PUBLISHED) {
                int quotationCount = random.nextInt(3) + 1;
                for (int i = 0; i < quotationCount && i < suppliers.size(); i++) {
                    Quotation quotation = new Quotation();
                    quotation.setRfq(rfq);
                    quotation.setSupplier(suppliers.get(random.nextInt(suppliers.size())));
                    quotation.setStatus(QuotationStatus.values()[random.nextInt(QuotationStatus.values().length)]);
                    quotation.setTotalAmount(BigDecimal.valueOf(1000 + random.nextDouble() * 9000));
                    quotation.setCurrency(Currency.TRY);
                    quotation.setValidUntil(LocalDate.now().plusDays(random.nextInt(30) + 7));
                    quotation.setDeliveryDays(random.nextInt(14) + 7);
            quotation.setPaymentTerms(generateLoremText(40));
            quotation.setNotes(generateLoremText(60));
            quotation.setVersionNumber(1);
            quotation.setCreatedAt(LocalDateTime.now());
                    quotation.setCreatedBy(userId);
                    quotation = quotationRepository.save(quotation);
                    quotations.add(quotation);
                }
            }
        }

        return quotations;
    }

    private void createQuotationItems(Quotation quotation) {
        List<RFQItem> rfqItems = rfqItemRepository.findByRfqId(quotation.getRfq().getId());
        for (RFQItem rfqItem : rfqItems) {
            QuotationItem item = new QuotationItem();
            item.setQuotation(quotation);
            item.setRfqItem(rfqItem);
            item.setItemName(rfqItem.getItemName());
            item.setQuantity(rfqItem.getQuantity());
            item.setUnitPrice(BigDecimal.valueOf(10 + random.nextDouble() * 90));
            item.setDiscountAmount(BigDecimal.valueOf(random.nextDouble() * 10));
            item.setTotalPrice(item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .subtract(item.getDiscountAmount()));
            item.setCreatedBy(userId);
            quotationItemRepository.save(item);
        }
    }

    private List<Order> createOrders(Campus campus, List<Supplier> suppliers, List<Quotation> quotations, List<Product> products) {
        log.info("Creating orders...");
        List<Order> orders = new ArrayList<>();
        int orderCount = supplyCount * 3;

        for (int i = 1; i <= orderCount; i++) {
            Order order = new Order();
            order.setOrderNumber("ORD-" + String.format("%06d", i));
            if (random.nextBoolean() && !quotations.isEmpty()) {
                order.setQuotation(quotations.get(random.nextInt(quotations.size())));
            }
            order.setCompany(campus);
            order.setSupplier(suppliers.get(random.nextInt(suppliers.size())));
            order.setStatus(OrderStatus.values()[random.nextInt(OrderStatus.values().length)]);
            order.setSubtotal(BigDecimal.valueOf(500 + random.nextDouble() * 4500));
            order.setTaxAmount(order.getSubtotal().multiply(new BigDecimal("0.20")));
            order.setTotalAmount(order.getSubtotal().add(order.getTaxAmount()));
            order.setCurrency(Currency.TRY);
            order.setDeliveryAddress(generateLoremText(40));
            order.setExpectedDeliveryDate(LocalDate.now().plusDays(random.nextInt(14) + 7));
            if (order.getStatus() == OrderStatus.DELIVERED) {
                order.setActualDeliveryDate(LocalDate.now().minusDays(random.nextInt(10)));
            }
            order.setNotes(generateLoremText(40));
            order.setInvoiceNumber("INV-" + String.format("%06d", i));
            order.setTrackingNumber("TRACK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            order.setCreatedAt(LocalDateTime.now());
            order.setCreatedBy(userId);
            order = orderRepository.save(order);
            orders.add(order);
        }

        return orders;
    }

    private void createOrderItems(Order order, List<Product> products) {
        int itemCount = random.nextInt(5) + 1;
        List<Product> supplierProducts = products.stream()
                .filter(p -> p.getSupplier().getId().equals(order.getSupplier().getId()))
                .toList();

        if (supplierProducts.isEmpty()) {
            return;
        }

        for (int i = 0; i < itemCount && i < supplierProducts.size(); i++) {
            Product product = supplierProducts.get(random.nextInt(supplierProducts.size()));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setItemName(product.getName());
            item.setQuantity(random.nextInt(10) + 1);
            item.setUnitPrice(product.getBasePrice());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setCreatedBy(userId);
            orderItemRepository.save(item);
        }
    }

    private void createSupplierRating(Order order) {
        SupplierRating rating = new SupplierRating();
        rating.setSupplier(order.getSupplier());
        rating.setCompany(order.getCompany());
        rating.setOrder(order);
        rating.setDeliveryRating(random.nextInt(3) + 3);
        rating.setQualityRating(random.nextInt(3) + 3);
        rating.setCommunicationRating(random.nextInt(3) + 3);
        rating.setComment(generateLoremText(50));
        rating.setCreatedAt(LocalDateTime.now());
        rating.setCreatedBy(userId);
        supplierRatingRepository.save(rating);
    }

    private void createWishlists(User user, List<Product> products) {
        int wishlistCount = Math.min(random.nextInt(10) + 5, products.size());
        List<Product> shuffledProducts = new ArrayList<>(products);
        java.util.Collections.shuffle(shuffledProducts);
        
        int addedCount = 0;
        for (Product product : shuffledProducts) {
            if (addedCount >= wishlistCount) {
                break;
            }
            
            // Check if wishlist already exists for this user-product combination
            boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());
            if (exists) {
                continue;
            }
            
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setProduct(product);
            wishlist.setCreatedAt(LocalDateTime.now());
            wishlist.setCreatedBy(userId);
            wishlistRepository.save(wishlist);
            addedCount++;
        }
    }

    private void createProductPayment(Order order) {
        ProductPayment payment = new ProductPayment();
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.values()[random.nextInt(PaymentMethod.values().length)]);
        payment.setStatus(PaymentStatus.values()[random.nextInt(PaymentStatus.values().length)]);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(Currency.TRY);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            payment.setPaidAt(LocalDateTime.now().minusDays(random.nextInt(10)));
        }
        payment.setNotes(generateLoremText(30));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(userId);
        productPaymentRepository.save(payment);
    }

    private List<ProductConversation> createProductConversations(Campus campus, List<Supplier> suppliers, List<Product> products) {
        log.info("Creating product conversations...");
        List<ProductConversation> conversations = new ArrayList<>();
        int conversationCount = supplyCount * 2;

        for (int i = 0; i < conversationCount; i++) {
            ProductConversation conversation = new ProductConversation();
            conversation.setCompany(campus);
            conversation.setSupplier(suppliers.get(random.nextInt(suppliers.size())));
            if (random.nextBoolean() && !products.isEmpty()) {
                conversation.setProduct(products.get(random.nextInt(products.size())));
            }
            conversation.setMessageType(ProductMessageType.values()[random.nextInt(ProductMessageType.values().length)]);
            conversation.setSubject("Konuşma " + (i + 1));
            conversation.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            conversation.setCreatedBy(userId);
            conversation = productConversationRepository.save(conversation);
            conversations.add(conversation);
        }

        return conversations;
    }

    private void createProductMessages(ProductConversation conversation, User user) {
        int messageCount = random.nextInt(5) + 2;
        for (int i = 0; i < messageCount; i++) {
            ProductMessage message = new ProductMessage();
            message.setConversation(conversation);
            message.setSender(user);
            message.setContent(generateLoremText(80));
            message.setIsRead(i < messageCount - 1);
            message.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(10)));
            message.setCreatedBy(userId);
            productMessageRepository.save(message);
        }
    }

    private void createNotifications(User user, List<RFQ> rfqs, List<Quotation> quotations, List<Order> orders) {
        log.info("Creating notifications...");

        // RFQ notifications
        for (RFQ rfq : rfqs) {
            if (rfq.getStatus() == RFQStatus.PUBLISHED) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setTitle("Yeni Alım İlanı");
                notification.setMessage("Yeni bir alım ilanı yayınlandı: " + rfq.getTitle());
                notification.setNotificationType("RFQ_PUBLISHED");
                notification.setIsRead(false);
                notification.setReferenceId(rfq.getId());
                notification.setReferenceType("RFQ");
                notification.setCreatedAt(LocalDateTime.now());
                notification.setCreatedBy(userId);
                notificationRepository.save(notification);
            }
        }

        // Order notifications
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CONFIRMED) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setTitle("Sipariş Onaylandı");
                notification.setMessage("Siparişiniz onaylandı: " + order.getOrderNumber());
                notification.setNotificationType("ORDER_CONFIRMED");
                notification.setIsRead(false);
                notification.setReferenceId(order.getId());
                notification.setReferenceType("ORDER");
                notification.setCreatedAt(LocalDateTime.now());
                notification.setCreatedBy(userId);
                notificationRepository.save(notification);
            }
        }
    }

    private void createCategoryAttributes(Category category) {
        int attrCount = random.nextInt(3) + 1;
        String[] attributeNames = {"Renk", "Boyut", "Marka"};
        String[] possibleValues = {"[\"Kırmızı\", \"Mavi\", \"Yeşil\"]", "[\"Küçük\", \"Orta\", \"Büyük\"]", "[\"Marka A\", \"Marka B\"]"};

        for (int i = 0; i < attrCount && i < attributeNames.length; i++) {
            CategoryAttribute attribute = new CategoryAttribute();
            attribute.setCategory(category);
            attribute.setAttributeName(attributeNames[i]);
            attribute.setPossibleValues(possibleValues[i]);
            attribute.setIsRequired(random.nextBoolean());
            attribute.setDisplayOrder(i);
            attribute.setCreatedBy(userId);
            categoryAttributeRepository.save(attribute);
        }
    }

    private String generateLoremText(int wordCount) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            if (i > 0) {
                text.append(" ");
            }
            text.append(loremWords[random.nextInt(loremWords.length)]);
        }
        return text.toString();
    }
}
