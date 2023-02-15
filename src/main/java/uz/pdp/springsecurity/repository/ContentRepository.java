package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Content;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<Content, UUID> {
}
