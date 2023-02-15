package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Content;
import uz.pdp.springsecurity.entity.ContentProduct;

import java.util.UUID;

public interface ContentProductRepository extends JpaRepository<ContentProduct, UUID> {
}
