package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.springsecurity.entity.Branch;
import uz.pdp.springsecurity.entity.Measurement;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.entity.Warehouse;
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
        List<ProductViewDtos> productViewDtoList=new ArrayList<>();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);
        if (productList.isEmpty()){
            return null;
        }else {
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

    public void save(MultipartFile file,UUID branchId) {
        try {
            List<ProductViewDtos> productViewDtosList = ExcelHelper.excelToTutorials(file.getInputStream());
            for (ProductViewDtos productViewDtos : productViewDtosList) {
                Optional<Product> optionalProduct = productRepository.findByNameAndBranchIdAndActiveTrue(productViewDtos.getProductName(),branchId);
                Warehouse warehouse;
                if (optionalProduct.isPresent()){
                    Product product = optionalProduct.get();
                    Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductId(branchId, product.getId());
                    if (optionalWarehouse.isPresent()){
                        warehouse = optionalWarehouse.get();
                        warehouse.setAmount(warehouse.getAmount()+productViewDtos.getAmount());
                    }else {
                        Warehouse warehouse1 = new Warehouse();
                        warehouse1.setAmount(productViewDtos.getAmount());
                        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
                        optionalBranch.ifPresent(warehouse1::setBranch);
                        warehouse1.setProduct(product);
                    }

                }

//                Product product=new Product();
//                product.setName(productViewDtos.getProductName());
//                product.setActive(true);
//
//
//                productViewDtos.setProductName(productViewDtos.getProductName());
//                productViewDtos.

            }

        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }


}
