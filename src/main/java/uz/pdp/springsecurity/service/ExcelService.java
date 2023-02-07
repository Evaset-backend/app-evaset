package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.springsecurity.entity.Branch;
import uz.pdp.springsecurity.entity.Measurement;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.entity.Warehouse;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ProductViewDtos;
import uz.pdp.springsecurity.repository.BranchRepository;
import uz.pdp.springsecurity.repository.MeasurementRepository;
import uz.pdp.springsecurity.repository.ProductRepository;
import uz.pdp.springsecurity.repository.WarehouseRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    BranchRepository branchRepository;

    public List<ProductViewDtos> getByBusiness(UUID businessId) {
        List<ProductViewDtos> productViewDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);
        if (productList.isEmpty()) {
            return null;
        } else {
            for (Product product : productList) {
                ProductViewDtos productViewDto = new ProductViewDtos();
                productViewDto.setProductName(product.getName());
                productViewDto.setBrandName(product.getBrand().getName());
                productViewDto.setBarcode(product.getBarcode());
                productViewDto.setBuyPrice(product.getBuyPrice());
                productViewDto.setSalePrice(product.getSalePrice());
                productViewDto.setMinQuantity(product.getMinQuantity());
                List<Branch> branchList = product.getBranch();
                for (Branch branch : branchList) {
                    productViewDto.setBranch(branch.getName());
                }
                productViewDto.setExpiredDate(product.getExpireDate().toString());

                Optional<Measurement> optionalMeasurement = measurementRepository.findById(product.getMeasurement().getId());
                optionalMeasurement.ifPresent(measurement -> productViewDto.setMeasurementId(measurement.getName()));
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranch_BusinessIdAndProductId(businessId, product.getId());
                optionalWarehouse.ifPresent(warehouse -> productViewDto.setAmount(warehouse.getAmount()));
                productViewDtoList.add(productViewDto);
            }

            return productViewDtoList;
        }
    }

    public ApiResponse save(MultipartFile file, UUID branchId) {

        try {
            List<ProductViewDtos> productViewDtosList = ExcelHelper.excelToTutorials(file.getInputStream());
            assert productViewDtosList != null;
            for (ProductViewDtos productViewDtos : productViewDtosList) {
                String name = productViewDtos.getProductName();
                Optional<Product> optionalProduct = productRepository.findByNameAndBranchIdAndActiveTrue(name, branchId);
                if (optionalProduct.isPresent()) {
                    Product product = optionalProduct.get();
                    UUID productId = product.getId();
                    Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(productId);
                    if (optionalWarehouse.isPresent()) {
                        Warehouse warehouse = optionalWarehouse.get();
                        warehouse.setAmount(productViewDtos.getAmount() + warehouse.getAmount());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());

        }
        return new ApiResponse();
    }
}