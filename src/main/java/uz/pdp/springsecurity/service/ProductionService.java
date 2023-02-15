package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.entity.ContentProduct;
import uz.pdp.springsecurity.entity.Production;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.GetOneContentProductionDto;
import uz.pdp.springsecurity.payload.ProductionDto;
import uz.pdp.springsecurity.repository.ContentProductRepository;
import uz.pdp.springsecurity.repository.ProductionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductionService {
    private final ProductionRepository productionRepository;
    private final ContentProductRepository contentProductRepository;
    public ApiResponse add(Business business, ProductionDto productionDto) {
        return new ApiResponse();
    }

    public ApiResponse getAll(UUID businessId) {
        List<Production> productionList = productionRepository.findAllByBusinessId(businessId);
        if (productionList.isEmpty())return new ApiResponse("NOT FOUND", false);
        return new ApiResponse(true, productionList);
    }

    public ApiResponse getOne(UUID productionId) {
        Optional<Production> optionalProduction = productionRepository.findById(productionId);
        if (optionalProduction.isEmpty())return new ApiResponse("NOT FOUND", false);
        Production production = optionalProduction.get();
        List<ContentProduct> contentProductList = contentProductRepository.findAllByProductionId(productionId);
        if (contentProductList.isEmpty()) return new ApiResponse("NOT FOUND CONTENT PRODUCTS", false);
        GetOneContentProductionDto getOneContentProductionDto = new GetOneContentProductionDto(
                production,
                contentProductList
        );
        return new ApiResponse(true, getOneContentProductionDto);
    }
}
