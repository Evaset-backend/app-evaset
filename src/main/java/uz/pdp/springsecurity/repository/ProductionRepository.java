package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Production;

import java.util.UUID;

public interface ProductionRepository extends JpaRepository<Production, UUID> {
}