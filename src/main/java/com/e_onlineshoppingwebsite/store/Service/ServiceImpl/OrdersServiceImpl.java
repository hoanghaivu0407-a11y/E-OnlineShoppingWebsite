
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Address;
import com.e_onlineshoppingwebsite.store.Model.Cart;
import com.e_onlineshoppingwebsite.store.Model.CartItem;
import com.e_onlineshoppingwebsite.store.Model.Member;
import com.e_onlineshoppingwebsite.store.Model.Order;
import com.e_onlineshoppingwebsite.store.Model.OrderItem;
import com.e_onlineshoppingwebsite.store.Model.OrderPromotion;
import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Model.Promotion;
import com.e_onlineshoppingwebsite.store.Repository.AddressRepository;
import com.e_onlineshoppingwebsite.store.Repository.CartItemRepository;
import com.e_onlineshoppingwebsite.store.Repository.CartRepository;
import com.e_onlineshoppingwebsite.store.Repository.MemberRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderItemRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderPromotionRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderRepository;
import com.e_onlineshoppingwebsite.store.Service.InventoriesService;
import com.e_onlineshoppingwebsite.store.Service.OrdersService;
import com.e_onlineshoppingwebsite.store.Service.PromotionsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PromotionsService promotionsService;
    private final InventoriesService inventoriesService;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
    }

    @Override
    public Order getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đơn hàng"));
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByMember_UserId(userId);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }

    @Override
    public List<Order> searchByKeyword(String keyword) {
        return orderRepository.searchByKeyword(
                keyword == null ? "" : keyword);
    }

    @Override
    public List<Order> findByStatus(String status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public List<Order> findByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<Order> getOrdersWithPagination(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Phân trang không hợp lệ");
        }
        return orderRepository.findAll(PageRequest.of(page, size))
                .getContent();
    }

    @Override
    public long countOrders(String status) {
        if (status == null || status.isBlank()) {
            return orderRepository.count();
        }
        return orderRepository.countByOrderStatus(status);
    }

    @Override
    public Order placeOrder(
            Long userId, Long addressId, String promotionCode) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        Address address = addressRepository
                .findByAddressIdAndMember_UserId(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không hợp lệ"));

        Cart cart = cartRepository.findByMember_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : items) {
            Product p = item.getProduct();

            if (!"active".equals(p.getStatus())) {
                throw new IllegalArgumentException(
                        "Sản phẩm không còn kinh doanh: " + p.getProductName());
            }

            if (!inventoriesService.checkStock(
                    p.getProductId(), item.getQuantity())) {
                throw new IllegalArgumentException(
                        "Không đủ tồn kho: " + p.getProductName());
            }

            BigDecimal price = p.getSalePrice() != null
                    ? p.getSalePrice()
                    : p.getPrice();

            subtotal = subtotal.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (promotionCode != null && !promotionCode.isBlank()) {
            if (!promotionsService.isValidPromotion(
                    promotionCode, subtotal)) {
                throw new IllegalArgumentException(
                        "Mã giảm giá không hợp lệ");
            }

            discount = promotionsService.calculateDiscount(
                    promotionCode, subtotal);
        }

        BigDecimal shippingFee = BigDecimal.ZERO;

        Order order = new Order();
        order.setOrderCode("ORD-" +
                UUID.randomUUID().toString()
                        .replace("-", "").substring(0, 20).toUpperCase());
        order.setMember(member);
        order.setAddress(address);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setShippingAddress(
                address.getStreetAddress() + ", "
                        + address.getWard() + ", " + address.getProvince());
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discount);
        order.setTotalAmount(
                subtotal.add(shippingFee).subtract(discount));
        order.setOrderStatus("pending");

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : items) {
            Product product = item.getProduct();

            BigDecimal price = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getProductName());
            orderItem.setUnitPrice(price);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(
                    price.multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItemRepository.save(orderItem);
        }

        if (promotionCode != null && !promotionCode.isBlank()) {
            Promotion promotion = promotionsService
                    .findByCode(promotionCode).orElseThrow();

            OrderPromotion op = new OrderPromotion();
            op.setOrder(savedOrder);
            op.setPromotion(promotion);
            op.setDiscountAmount(discount);

            // Lưu liên kết mã khuyến mãi bằng Repository riêng.
            // Xem phần bổ sung dependency bên dưới.
            orderPromotionRepository.save(op);
        }

        cartItemRepository.deleteByCart_CartId(cart.getCartId());

        return savedOrder;
    }

    private final OrderPromotionRepository orderPromotionRepository;

    @Override
    public void cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);

        if (!order.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Bạn không có quyền hủy đơn này");
        }

        if (!"pending".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ được hủy đơn đang chờ xác nhận");
        }

        order.setOrderStatus("cancelled");
        orderRepository.save(order);
    }

    @Override
    public void confirmOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (!"pending".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException(
                    "Đơn hàng không ở trạng thái chờ xác nhận");
        }

        order.setOrderStatus("confirmed");
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Override
    public BigDecimal calculateOrderTotal(Long orderId) {
        return getOrderById(orderId).getTotalAmount();
    }

    @Override
    public BigDecimal calculateRevenue(
            LocalDateTime startDate, LocalDateTime endDate) {

        BigDecimal revenue = orderRepository.calculateRevenue(startDate, endDate);

        return revenue == null ? BigDecimal.ZERO : revenue;
    }
}
