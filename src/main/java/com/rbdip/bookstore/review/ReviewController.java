package com.rbdip.bookstore.review;

import com.rbdip.bookstore.dto.CreateReviewRequest;
import com.rbdip.bookstore.dto.ReviewCreatedResponse;
import com.rbdip.bookstore.dto.ReviewView;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewCreatedResponse addReview(
            @PathVariable Long productId, @RequestBody CreateReviewRequest body) {
        Review review = reviewService.addReview(productId, body.authorName(), body.rating(), body.comment());
        return new ReviewCreatedResponse(review.getId());
    }

    @GetMapping("/products/{productId}/reviews")
    public List<ReviewView> listReviews(@PathVariable Long productId) {
        return reviewService.listReviews(productId).stream()
                .map(r -> new ReviewView(
                        r.getAuthorName(),
                        r.getRating(),
                        r.getComment() == null ? "" : r.getComment()))
                .toList();
    }
}
