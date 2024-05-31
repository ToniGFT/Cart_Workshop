package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartMicroservice.exceptions.UserWithCartException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    public static final String CART_NOT_FOUND = "Cart with ID ";
    public static final String NOT_FOUND = " not found";
    public static final String NOT_ENOUGH_STOCK = "Not enough stock to add product to cart. Desired amount: ";
    public static final String ACTUAL_STOCK = ". Actual stock: ";
    public static final String USER_ALREADY_HAS_CART = "User with ID ";
    public static final String ALREADY_HAS_CART = " already has a cart.";
    public static final String NO_ABANDONED_CARTS_FOUND = "No abandoned carts found before ";
    public static final String FOUND_ABANDONED_CARTS = "Found {} abandoned carts before ";
    public static final String ABANDONED_CART = "Abandoned cart: {}, at ";

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository, CartProductRepository cartProductRepository, ProductService productService, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public void checkForAbandonedCarts() {
        LocalDate thresholdDate = LocalDate.now().minusDays(1);
        identifyAbandonedCarts(thresholdDate);
    }

    @Override
    public void addProductToCart(CartProduct cartProduct) {
        checkForAbandonedCarts();
        validateProductStock(cartProduct);
        findExistingCartProduct(cartProduct)
                .ifPresentOrElse(
                        existingCartProduct -> updateExistingCartProduct(existingCartProduct, cartProduct),
                        () -> addNewCartProduct(cartProduct)
                );
    }

    public Optional<CartProduct> findExistingCartProduct(CartProduct cartProduct) {
        return cartProductRepository.findByCartIdAndProductId(cartProduct.getCart().getId(), cartProduct.getProductId());
    }

    public void updateExistingCartProduct(CartProduct existingCartProduct, CartProduct newCartProduct) {
        existingCartProduct.setQuantity(existingCartProduct.getQuantity() + newCartProduct.getQuantity());
        cartProductRepository.save(existingCartProduct);
    }

    public void addNewCartProduct(CartProduct cartProduct) {
        Cart cart = fetchCartById(cartProduct.getCart().getId());
        addCartProduct(cart, cartProduct);
    }

    public void addCartProduct(Cart cart, CartProduct cartProduct) {
        cart.getCartProducts().add(cartProduct);
        cartProduct.setCart(cart);
        cartProductRepository.save(cartProduct);
    }

    public void validateProductStock(CartProduct cartProduct) {
        int totalDesiredQuantity = calculateTotalDesiredQuantity(cartProduct);
        int availableStock = getAvailableStock(cartProduct);

        if (totalDesiredQuantity > availableStock) {
            throw new CartProductInvalidQuantityException(
                    NOT_ENOUGH_STOCK + totalDesiredQuantity + ACTUAL_STOCK + availableStock);
        }
    }

    public int calculateTotalDesiredQuantity(CartProduct cartProduct) {
        return getCurrentQuantity(cartProduct) + cartProduct.getQuantity();
    }


    public int getCurrentQuantity(CartProduct cartProduct) {
        return findExistingCartProduct(cartProduct)
                .map(CartProduct::getQuantity)
                .orElse(0);
    }

    public int getAvailableStock(CartProduct cartProduct) {
        return productService.getProductById(cartProduct.getProductId()).getCurrentStock();
    }

    @Override
    public BigDecimal calculateCartTotal(Long cartId, Long userId) {
        User user = fetchUserById(userId);
        Cart cart = fetchCartById(cartId);

        List<CartProductDto> cartProductDtos = convertToDtoList(cart.getCartProducts());
        List<Product> products = productService.getProductByIdWithDiscountedPrice(cartProductDtos);

        BigDecimal totalProductCost = computeProductTotal(products);
        BigDecimal tax = computeTax(totalProductCost, user);
        BigDecimal shippingCost = computeShippingCost(computeTotalWeight(products));

        return totalProductCost.add(tax).add(shippingCost);
    }

    public User fetchUserById(Long userId) {
        return userService.getUserById(userId);
    }

    public BigDecimal computeProductTotal(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal computeTax(BigDecimal total, User user) {
        return total.multiply(BigDecimal.valueOf(user.getCountry().getTax() / 100.0));
    }

    public BigDecimal computeShippingCost(double totalWeight) {
        List<SimpleEntry<Double, BigDecimal>> weightCosts = createWeightCostList();
        return findShippingCostForWeight(totalWeight, weightCosts);
    }

    public List<SimpleEntry<Double, BigDecimal>> createWeightCostList() {
        return List.of(
                new SimpleEntry<>(5.0, new BigDecimal("5")),
                new SimpleEntry<>(10.0, new BigDecimal("10")),
                new SimpleEntry<>(20.0, new BigDecimal("20")),
                new SimpleEntry<>(Double.MAX_VALUE, new BigDecimal("50"))
        );
    }

    public BigDecimal findShippingCostForWeight(double totalWeight, List<SimpleEntry<Double, BigDecimal>> weightCosts) {
        return weightCosts.stream()
                .filter(entry -> totalWeight <= entry.getKey())
                .map(SimpleEntry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid weight: " + totalWeight));
    }

    public double computeTotalWeight(List<Product> products) {
        return products.stream()
                .mapToDouble(Product::getWeight)
                .sum();
    }

    public List<CartProductDto> convertToDtoList(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .map(EntityMapper::convertCartProductToDto)
                .toList();
    }

    @Override
    @Transactional
    public void emptyCart(Long cartId) {
        checkForAbandonedCarts();
        Cart cart = fetchCartById(cartId);
        clearCartProducts(cartId, cart);
        updateCartTimestamp(cart);
        cartRepository.save(cart);
    }

    public void clearCartProducts(Long cartId, Cart cart) {
        cartProductRepository.removeAllByCartId(cartId);
        cart.getCartProducts().clear();
    }

    public void updateCartTimestamp(Cart cart) {
        cart.setUpdatedAt(LocalDate.now());
    }

    @Override
    public List<CartDto> identifyAbandonedCarts(LocalDate thresholdDate) {
        List<Cart> abandonedCarts = fetchAbandonedCarts(thresholdDate);
        logAbandonedCartsInfo(abandonedCarts, thresholdDate);
        return convertCartsToDto(abandonedCarts);
    }

    public List<Cart> fetchAbandonedCarts(LocalDate thresholdDate) {
        return cartRepository.identifyAbandonedCarts(thresholdDate);
    }

    public void logAbandonedCartsInfo(List<Cart> abandonedCarts, LocalDate thresholdDate) {
        if (abandonedCarts.isEmpty()) {
            log.info(NO_ABANDONED_CARTS_FOUND + "{}", thresholdDate);
        } else {
            log.info(FOUND_ABANDONED_CARTS + "{}", abandonedCarts.size(), thresholdDate);
            abandonedCarts.forEach(cart -> log.debug(ABANDONED_CART + "{}", cart.getId(), cart.getUpdatedAt()));
        }
    }

    public List<CartDto> convertCartsToDto(List<Cart> abandonedCarts) {
        return abandonedCarts.stream()
                .map(EntityMapper::convertCartToDto)
                .toList();
    }

    @Override
    public CartDto createCart(Long userId) {
        checkForAbandonedCarts();
        ensureUserDoesNotAlreadyHaveCart(userId);
        Cart cart = buildAndSaveCart(userId);
        return EntityMapper.convertCartToDto(cart);
    }

    public void ensureUserDoesNotAlreadyHaveCart(Long userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> {
                    throw new UserWithCartException(USER_ALREADY_HAS_CART + userId + ALREADY_HAS_CART);
                });
    }

    public Cart buildAndSaveCart(Long userId) {
        Cart cart = Cart.builder()
                .updatedAt(LocalDate.now())
                .userId(userId)
                .build();
        return cartRepository.save(cart);
    }

    @Override
    public CartDto fetchValidatedCart(Long cartId) {
        checkForAbandonedCarts();
        Cart cart = fetchCartById(cartId);
        validateCartProductsStock(cart);
        updateAndSaveCartProductInfo(cart);
        return prepareCartDto(cart);
    }

    @Transactional
    public void updateAndSaveCartProductInfo(Cart cart) {
        Map<Long, Product> productMap = getProductMap(cart);
        updateCartProductsInfo(cart, productMap);
        updateCartTimestamp(cart);
    }

    public void updateCartProductsInfo(Cart cart, Map<Long, Product> productMap) {
        cart.getCartProducts().forEach(cartProduct -> {
            Product product = productMap.get(cartProduct.getProductId());
            if (product != null) {
                setCartProductInfo(cartProduct, product);
            }
        });
        cartProductRepository.saveAll(cart.getCartProducts());
    }

    public void setCartProductInfo(CartProduct cartProduct, Product product) {
        cartProduct.setPrice(product.getPrice());
        cartProduct.setProductName(product.getName());
        cartProduct.setProductDescription(product.getDescription());
    }

    public Cart fetchCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));
    }

    public CartDto prepareCartDto(Cart cart) {
        CartDto cartDto = EntityMapper.convertCartToDto(cart);
        cartDto.setTotalPrice(calculateCartTotal(cart.getId(), cart.getUserId()));
        return cartDto;
    }

    public void validateCartProductsStock(Cart cart) {
        Map<Long, Product> productMap = getProductMap(cart);
        checkStockForCartProducts(cart, productMap);
    }

    public Map<Long, Product> getProductMap(Cart cart) {
        List<Long> productIds = cart.getCartProducts().stream()
                .map(CartProduct::getProductId)
                .toList();
        return productService.findProductsByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    public void checkStockForCartProducts(Cart cart, Map<Long, Product> productMap) {
        cart.getCartProducts().forEach(cartProduct -> {
            Product product = productMap.get(cartProduct.getProductId());
            if (cartProduct.getQuantity() > product.getCurrentStock()) {
                throw new CartProductInvalidQuantityException(
                        NOT_ENOUGH_STOCK + cartProduct.getQuantity() + ACTUAL_STOCK + product.getCurrentStock());
            }
        });
    }

    public List<Cart> fetchAllCarts() {
        return cartRepository.findAll();
    }

}
