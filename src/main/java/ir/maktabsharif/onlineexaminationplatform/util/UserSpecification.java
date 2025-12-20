package ir.maktabsharif.onlineexaminationplatform.util;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ir.maktabsharif.onlineexaminationplatform.dto.SearchDto;
import ir.maktabsharif.onlineexaminationplatform.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> searchUsers(SearchDto dto) {
        return (root, cq, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (dto.firstName() != null && !dto.firstName().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + dto.firstName().toLowerCase() + "%"
                ));
            }

            if (dto.lastName() != null && !dto.lastName().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + dto.lastName().toLowerCase() + "%"
                ));
            }

            if (dto.nationalCode() != null && !dto.nationalCode().trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("nationalCode"),
                        dto.nationalCode()
                ));
            }

            if (dto.role() != null && !dto.role().trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("role"),
                        dto.role()
                ));
            }
            predicates.add(cb.notEqual(root.get("role"), "MANAGER"));
            predicates.add(cb.equal(root.get("isEnable"), false));

            return cb.and(predicates);
        };
    }
    public static Specification<User> searchAllUsers(SearchDto dto) {
        return (root, cq, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (dto.firstName() != null && !dto.firstName().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + dto.firstName().toLowerCase() + "%"
                ));
            }

            if (dto.lastName() != null && !dto.lastName().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + dto.lastName().toLowerCase() + "%"
                ));
            }

            if (dto.nationalCode() != null && !dto.nationalCode().trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("nationalCode"),
                        dto.nationalCode()
                ));
            }

            if (dto.role() != null && !dto.role().trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("role"),
                        dto.role()
                ));
            }
            predicates.add(cb.notEqual(root.get("role"),"MANAGER"));

            return cb.and(predicates);
        };
    }
}
