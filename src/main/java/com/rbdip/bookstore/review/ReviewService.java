package com.rbdip.bookstore.review;

import com.rbdip.bookstore.order.OrderItemRepository;
import com.rbdip.bookstore.order.OrderRepository;
import java.util.List;

import com.rbdip.bookstore.order.OrderService;
import com.rbdip.bookstore.service.ApiService;
import org.springframework.stereotype.Service;

/**
 * Намеренная архитектурная связанность: чтобы проверить, что автор отзыва
 * действительно покупал товар, сервис напрямую лезет во внутренние
 * репозитории пакета order, вместо обращения через выделенный контракт
 * (интерфейс/событие). Это цель для выделения модуля review по Strangler
 * Fig в ЛР4 - после рефакторинга ArchitectureRulesTest (пакет reference)
 * должен зафиксировать отсутствие такой зависимости.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApiService apiService;

    public ReviewService(
            ReviewRepository reviewRepository,
            ApiService apiService) {
        this.reviewRepository = reviewRepository;
        this.apiService = apiService;
    }

    public Review addReview(Long productId, String authorName, Integer rating, String comment) {
        // NB: в текущей "грязной" версии verifiedPurchase не используется дальше,
        // но сам факт прямого обращения к order-репозиториям отсюда - и есть
        // намеренная связанность, которую нужно устранить.
        boolean verifiedPurchase = apiService.checkExistingInOrderService();
        Review review = new Review(productId, authorName == null ? "anonymous" : authorName, rating, comment);
        return reviewRepository.save(review);
    }

    public List<Review> listReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
