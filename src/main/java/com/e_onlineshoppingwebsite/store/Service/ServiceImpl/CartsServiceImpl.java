
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Cart;
import com.e_onlineshoppingwebsite.store.Model.CartItem;
import com.e_onlineshoppingwebsite.store.Model.Member;
import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Repository.CartItemRepository;
import com.e_onlineshoppingwebsite.store.Repository.CartRepository;
import com.e_onlineshoppingwebsite.store.Repository.MemberRepository;
import com.e_onlineshoppingwebsite.store.Repository.ProductRepository;
import com.e_onlineshoppingwebsite.store.Service.CartsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartsServiceImpl implements CartsService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByMember_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng"));
    }

    @Override
    public Cart createCart(Long userId) {

        if (cartRepository.existsByMember_UserId(userId)) {
            return getCartByUserId(userId);
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        Cart cart = new Cart();
        cart.setMember(member);

        return cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {

        Cart cart = createCart(userId);

        return cartItemRepository.findByCart_CartId(cart.getCartId());
    }

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Cart cart = createCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        if (!"active".equals(product.getStatus())) {
            throw new IllegalArgumentException("Sản phẩm ngừng kinh doanh");
        }

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(
                        cart.getCartId(), productId)
                .orElse(null);

        if (item != null) {
            item.setQuantity(Math.addExact(item.getQuantity(), quantity));
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
        }

        cartItemRepository.save(item);
    }

    @Override
    public void updateQuantity(
            Long userId, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Cart cart = getCartByUserId(userId);

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(
                        cart.getCartId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không có trong giỏ"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {

        Cart cart = getCartByUserId(userId);

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(
                        cart.getCartId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không có trong giỏ"));

        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getCartByUserId(userId);

        cartItemRepository.deleteByCart_CartId(cart.getCartId());
    }

    @Override
    public BigDecimal calculateTotal(Long userId) {

        List<CartItem> items = getCartItems(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {

            Product product = item.getProduct();

            BigDecimal price = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            total = total.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return total;
    }

    @Override
    public int countCartItems(Long userId) {

        Cart cart = createCart(userId);

        // Đếm tổng số lượng sản phẩm, không chỉ số dòng giỏ hàng.
        return cartItemRepository.findByCart_CartId(cart.getCartId())
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public boolean isProductInCart(Long userId, Long productId) {

        return cartRepository.findByMember_UserId(userId)
                .map(cart -> cartItemRepository.existsByCart_CartIdAndProduct_ProductId(
                        cart.getCartId(), productId))
                .orElse(false);
    }
}
