
package com.e_onlineshoppingwebsite.store.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.e_onlineshoppingwebsite.store.Model.Member;
import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Service.AuthService;
import com.e_onlineshoppingwebsite.store.Service.BrandsService;
import com.e_onlineshoppingwebsite.store.Service.CartsService;
import com.e_onlineshoppingwebsite.store.Service.CategoriesService;
import com.e_onlineshoppingwebsite.store.Service.ProductImagesService;
import com.e_onlineshoppingwebsite.store.Service.ProductsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductsService productsService;
    private final CategoriesService categoriesService;
    private final BrandsService brandsService;
    private final ProductImagesService productImagesService;
    private final CartsService cartsService;
    private final AuthService authService;

    // 1. Chuyển từ / sang /home
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    // 2. Hiển thị trang chủ
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {

        model.addAttribute("products",
                productsService.getActiveProducts());

        model.addAttribute("newProducts",
                productsService.getNewestProducts());

        model.addAttribute("saleProducts",
                productsService.getSaleProducts());

        model.addAttribute("categories",
                categoriesService.getActiveCategories());

        model.addAttribute("brands",
                brandsService.getActiveBrands());

        model.addAttribute("loggedInUser",
                session.getAttribute("loggedInUser"));

        return "views/user/home";
    }

    // 3. Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String showLogin() {
        return "views/shared/login";
    }

    // 4. Xử lý đăng nhập
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            Member member = authService.login(email, password);

            // Tạo session mới sau khi đăng nhập thành công
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);

            // Chỉ lưu ID, không lưu Entity chứa passwordHash
            session.setAttribute("userId", member.getUserId());

            redirectAttributes.addFlashAttribute(
                    "success", "Đăng nhập thành công");

            return "redirect:/home";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", "Email hoặc mật khẩu không đúng");

            return "redirect:/login";
        }
    }

    // 5. Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegister(Model model) {

        model.addAttribute("member", new Member());

        return "views/shared/register";
    }

    // 6. Xử lý đăng ký
    @PostMapping("/register")
    public String register(
            @RequestParam String fullname,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "error", "Mật khẩu xác nhận không khớp");

            return "redirect:/register";
        }

        if (password.length() < 8) {

            redirectAttributes.addFlashAttribute(
                    "error", "Mật khẩu phải có ít nhất 8 ký tự");

            return "redirect:/register";
        }

        try {
            Member member = new Member();

            member.setFullname(fullname);
            member.setEmail(email.trim().toLowerCase());
            member.setPhone(phone);

            // AuthService sẽ mã hóa mật khẩu trước khi lưu.
            member.setPasswordHash(password);

            authService.register(member);

            redirectAttributes.addFlashAttribute(
                    "success", "Đăng ký thành công, vui lòng đăng nhập");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/register";
        }
    }

    // 7. Đăng xuất bằng POST
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/home";
    }

    // 8. Chi tiết sản phẩm
    @GetMapping("/detail")
    public String productDetail(
            @RequestParam Long productId,
            Model model,
            HttpSession session) {

        Product product = productsService.getProductById(productId);

        if (!"active".equals(product.getStatus())) {
            return "redirect:/home";
        }

        model.addAttribute("product", product);

        model.addAttribute("images",
                productImagesService.getImagesByProductId(productId));

        model.addAttribute("loggedInUser",
                session.getAttribute("userId"));

        return "views/user/products-detail";
    }

    // 9. Hiển thị giỏ hàng
    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {

        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("cartItems",
                cartsService.getCartItems(userId));

        model.addAttribute("total",
                cartsService.calculateTotal(userId));

        return "views/user/cart";
    }

    // 10. Thêm sản phẩm vào giỏ hàng
    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {

        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập");
        }

        if (quantity <= 0) {
            return ResponseEntity.badRequest()
                    .body("Số lượng phải lớn hơn 0");
        }

        try {
            cartsService.addToCart(userId, productId, quantity);

            return ResponseEntity.ok(
                    Map.of("message", "Thêm vào giỏ hàng thành công"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 11. Lấy danh sách sản phẩm trong giỏ hàng
    @GetMapping("/api/cart/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems(HttpSession session) {

        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập");
        }

        List<Map<String, Object>> items = cartsService.getCartItems(userId)
                .stream()
                .map(item -> {
                    Product p = item.getProduct();

                    BigDecimal price = p.getSalePrice() != null
                            ? p.getSalePrice()
                            : p.getPrice();

                    return Map.<String, Object>of(
                            "productId", p.getProductId(),
                            "productName", p.getProductName(),
                            "price", price,
                            "quantity", item.getQuantity());
                })
                .toList();

        return ResponseEntity.ok(items);
    }

    // 12. Cập nhật số lượng trong giỏ hàng
    @PostMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<?> updateCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            HttpSession session) {

        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập");
        }

        try {
            if (quantity <= 0) {
                cartsService.removeFromCart(userId, productId);
            } else {
                cartsService.updateQuantity(
                        userId, productId, quantity);
            }

            return ResponseEntity.ok(
                    Map.of("message", "Cập nhật giỏ hàng thành công"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 13. Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(
            @RequestParam Long productId,
            HttpSession session) {

        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập");
        }

        try {
            cartsService.removeFromCart(userId, productId);

            return ResponseEntity.ok(
                    Map.of("message", "Đã xóa sản phẩm khỏi giỏ hàng"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy ID người dùng từ session
    private Long getUserId(HttpSession session) {

        Object value = session.getAttribute("userId");

        return value instanceof Long ? (Long) value : null;
    }
}
