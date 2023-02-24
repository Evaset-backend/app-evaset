package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ContentProductDto;
import uz.pdp.springsecurity.payload.GetOneContentProductionDto;
import uz.pdp.springsecurity.payload.ProductionDto;
import uz.pdp.springsecurity.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductionService {
    private final ProductionRepository productionRepository;
    private final ContentProductRepository contentProductRepository;
    private final ProductRepository productRepository;
    private final ProductTypePriceRepository productTypePriceRepository;
    private final BranchRepository branchRepository;
    private final WarehouseService warehouseService;

    public ApiResponse add(ProductionDto productionDto) {
        Optional<Branch> optionalBranch = branchRepository.findById(productionDto.getBranchId());
        if (optionalBranch.isEmpty()) return new ApiResponse("NOT FOUND BRANCH", false);

        Production production = new Production();
        production.setBranch(optionalBranch.get());

        if (productionDto.getProductId() != null) {
            Optional<Product> optional = productRepository.findById(productionDto.getProductId());
            if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT", false);
            Product product = optional.get();
            product.setBuyPrice(production.getTotalPrice() / production.getQuantity());
            production.setProduct(product);
        } else {
            Optional<ProductTypePrice> optional = productTypePriceRepository.findById(productionDto.getProductTypePriceId());
            if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT TYPE PRICE", false);
            ProductTypePrice productTypePrice = optional.get();
            productTypePrice.setBuyPrice(production.getTotalPrice() / production.getQuantity());
            production.setProductTypePrice(productTypePrice);
        }
        production.setQuantity(productionDto.getQuantity());
        production.setDate(productionDto.getDate());
        production.setCostEachOne(productionDto.isCostEachOne());
        production.setContentPrice(productionDto.getContentPrice());
        production.setCost(productionDto.getCost());
        production.setTotalPrice(productionDto.getTotalPrice());

        productionRepository.save(production);

        List<ContentProductDto> contentProductDtoList = productionDto.getContentProductDtoList();
        List<ContentProduct>contentProductList = new ArrayList<>();

        for (ContentProductDto contentProductDto : contentProductDtoList) {
            ContentProduct contentProduct = new ContentProduct();
            contentProduct.setProduction(production);
            ContentProduct savedContentProduct = warehouseService.createContentProduct(contentProduct, contentProductDto);
            if (savedContentProduct == null) continue;
            savedContentProduct.setQuantity(contentProductDto.getQuantity());
            savedContentProduct.setTotalPrice(contentProductDto.getTotalPrice());
            contentProductList.add(savedContentProduct);
        }
        if (contentProductList.isEmpty()) return new ApiResponse("NOT FOUND CONTENT PRODUCTS", false);
        contentProductRepository.saveAll(contentProductList);
        warehouseService.createOrEditWareHouse(production);
        return new ApiResponse("SUCCESS", true);
    }

    public ApiResponse getAll(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) return new ApiResponse("NOT FOUND BRANCH", false);
        List<Production> productionList = productionRepository.findAllByBranchId(branchId);
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
