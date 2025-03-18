package com.example.ecomobile.specifications;

import com.example.ecomobile.dto.OrderFilter;
import com.example.ecomobile.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Order> orderSpecification(OrderFilter orderFilter) {
        return (taskRoot, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (orderFilter.getSearch() != null && !orderFilter.getSearch().isEmpty()) {
                String searchValue = "%" + orderFilter.getSearch().toLowerCase() + "%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("status")), searchValue),
                        criteriaBuilder.like(criteriaBuilder.function("TEXT", String.class, taskRoot.get("id")), searchValue), // ✅ Fix for ID
                        criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("location").get("region")), searchValue),
                        criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("location").get("district")), searchValue),
                        criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("location").get("home")), searchValue),
                        criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("location").get("street")), searchValue)
                );

                predicate = criteriaBuilder.and(predicate, searchPredicate);
            }

            if (orderFilter.getDate() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(taskRoot.get("createdAt"), orderFilter.getDate()));
            }

            return predicate;
        };

    }
}
