package uz.pdp.springsecurity.service;

import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ProductionDto;

import java.util.UUID;

@Service
public class ProductionService {
    public ApiResponse add(Business business, ProductionDto productionDto) {
        return new ApiResponse();
    }

    public ApiResponse getAll(Business business) {
        return new ApiResponse();
    }

    public ApiResponse getOne(UUID productionId) {
        return new ApiResponse();
    }
}
