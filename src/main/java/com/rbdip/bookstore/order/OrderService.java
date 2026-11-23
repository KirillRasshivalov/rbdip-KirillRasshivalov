package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.customer.CustomerRepository;
import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import com.rbdip.bookstore.service.EmailSenderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final String STATUS_NEW = "new";
    private static final String CUSTOMER_TYPE_REGULAR = "regular";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final PricingCalculator pricingCalculator = new PricingCalculator();
    private final EmailSenderService emailSenderService;
    private final OrderValidator orderValidator;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            EmailSenderService emailSenderService,
            OrderValidator orderValidator) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.emailSenderService = emailSenderService;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);

        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();
        for (CreateOrderRequest.Item raw : request.items()) {
            Product product = productRepository.findById(raw.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + raw.productId() + " not found"));
            int quantity = raw.quantity() == null ? 1 : raw.quantity();
            orderValidator.checkQuantity(quantity);
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), quantity));
        }

        String customerType = request.customerType() == null ? CUSTOMER_TYPE_REGULAR : request.customerType();
        BigDecimal total = pricingCalculator.calculateOrderTotal(lineItems, customerType, request.couponCode());


        Customer customer = customerRepository.save(Customer.fromFullName(
                request.customerFullName(), request.customerAddress(), request.customerPhone()));
        Order order = orderRepository.save(new Order(customer, STATUS_NEW));

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = lineItems.get(i).quantity();
            orderItemRepository.save(new OrderItem(order.getId(), product, quantity));
        }

        emailSenderService.sendConfirmationEmail(request.customerFullName(), order.getId(), total);

        return order;
    }
}
