package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.entity.Currency;
import uz.pdp.springsecurity.enums.Type;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    CurrentCourceRepository currentCourceRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductTypePriceRepository productTypePriceRepository;

    @Autowired
    ProductTypeValueRepository productTypeValueRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    private final ProductTypeComboRepository comboRepository;

    private final SubscriptionRepository subscriptionRepository;


    public ApiResponse addProduct(ProductDto productDto) {UUID businessId = productDto.getBusinessId();
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("not found business", false);
        }

        Optional<Subscription> optionalSubscription = subscriptionRepository.findByBusinessIdAndActiveTrue(businessId);
        if (optionalSubscription.isEmpty()) {
            return new ApiResponse("tariff aktiv emas", false);
        }

        Subscription subscription = optionalSubscription.get();

        List<Product> allProduct = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);
        int size = allProduct.size();

        if (subscription.getTariff().getProductAmount() >= size || subscription.getTariff().getProductAmount() == 0){
            Product product = new Product();
            product.setBusiness(optionalBusiness.get());
            return createOrEditProduct(product, productDto, false);
        }
        return new ApiResponse("You have to opened a sufficient branch according to the product", false);
    }

    public ApiResponse editProduct(UUID id, ProductDto productDto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        }
        return createOrEditProduct(optionalProduct.get(), productDto, true);
    }
    public ApiResponse createOrEditProduct(Product product, ProductDto productDto, boolean isUpdate){

        /*if (productDto.getType().equalsIgnoreCase("single")) {
            if (productDto.getBarcode() != null || !productDto.getBarcode().isBlank()) {
                if (isUpdate) {
                    if (productRepository.existsByBarcodeAndBusinessIdAndIdIsNotAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId(), product.getId()))
                        return new ApiResponse("product with the barcode is already exist");
                }else {
                    if (productRepository.existsByBarcodeAndBusinessIdAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId()))
                        return new ApiResponse("product with the barcode is already exist");
                }
            }
        }else {

        }*/

        UUID measurementId = productDto.getMeasurementId();
        List<UUID> branchId = productDto.getBranchId();

        Optional<Measurement> optionalMeasurement = measurementRepository.findById(measurementId);
        List<Branch> allBranch = branchRepository.findAllById(branchId);

        if (optionalMeasurement.isEmpty()) {
            return new ApiResponse("not found measurement", false);
        }
        if (allBranch.isEmpty()) {
            return new ApiResponse("not found branches", false);
        }

        product.setName(productDto.getName());
        product.setBranch(allBranch);
        product.setMeasurement(optionalMeasurement.get());

        product.setTax(productDto.getTax());
        product.setExpireDate(productDto.getExpireDate());
        product.setBarcode(productDto.getBarcode());
        product.setExpireDate(productDto.getExpireDate());
        product.setMinQuantity(productDto.getMinQuantity());
        product.setDueDate(productDto.getDueDate());
        product.setActive(true);

        if (productDto.getCategoryId() != null) {
            UUID categoryId = productDto.getCategoryId();
            Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
            optionalCategory.ifPresent(product::setCategory);

        }

        if (productDto.getBrandId() != null) {
            UUID brandId = productDto.getBrandId();
            Optional<Brand> optionalBrand = brandRepository.findById(brandId);
            optionalBrand.ifPresent(product::setBrand);
        }

        if (productDto.getPhotoId() != null) {
            Optional<Attachment> optionalAttachment = attachmentRepository.findById(productDto.getPhotoId());
            optionalAttachment.ifPresent(product::setPhoto);
        }

        if (productDto.getType().equals(Type.SINGLE.name())) {
            return addProductTypeSingleDto(productDto, product, isUpdate);
        } else if (productDto.getType().equals(Type.MANY.name())) {
            return addProductTypeManyDto(productDto, product, isUpdate);
        } else if (productDto.getType().equals(Type.COMBO.name())) {
            return addProductTypeComboDto(productDto, product, isUpdate);
        } else {
            return new ApiResponse("no such type exists", false);
        }
    }

    private ApiResponse addProductTypeComboDto(ProductDto productDto, Product product, boolean isUpdate) {

        product.setType(Type.COMBO);
        product.setBuyPrice(productDto.getBuyPrice());
        product.setSalePrice(productDto.getSalePrice());
        if (productDto.getBarcode() != null && !productDto.getBarcode().isBlank()) {
            if (isUpdate) {
                if (productRepository.existsByBarcodeAndBusinessIdAndIdIsNotAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId(), product.getId()))
                    return new ApiResponse("product with the barcode is already exist");
            }else {
                if (productRepository.existsByBarcodeAndBusinessIdAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId()))
                    return new ApiResponse("product with the barcode is already exist");
            }
            product.setBarcode(productDto.getBarcode());
        }else {
            product.setBarcode(generateBarcode(product.getBusiness().getId(), product.getName(), product.getId(), isUpdate));
        }
        Product saveProduct = productRepository.save(product);

        List<ProductTypeComboDto> productTypeComboDtoList = productDto.getProductTypeComboDtoList();

        List<ProductTypeCombo> productTypeComboList = new ArrayList<>();

        for (ProductTypeComboDto productTypeComboDto : productTypeComboDtoList) {
            Optional<Product> optionalProduct = productRepository.findById(productTypeComboDto.getContentProductId());
            if (optionalProduct.isEmpty()) {
                return new ApiResponse("not found content product", false);
            }
            if (isUpdate && productTypeComboDto.getComboId() != null) {
                Optional<ProductTypeCombo> comboOptional = comboRepository.findById(productTypeComboDto.getComboId());
                if (comboOptional.isEmpty()) {
                    return new ApiResponse("not found combo product", false);
                }
                ProductTypeCombo productTypeCombo = comboOptional.get();
                productTypeCombo.setMainProduct(saveProduct);
                productTypeCombo.setContentProduct(optionalProduct.get());
                productTypeCombo.setAmount(productTypeComboDto.getAmount());
                productTypeCombo.setBuyPrice(productTypeComboDto.getBuyPrice());
                productTypeCombo.setSalePrice(productTypeComboDto.getSalePrice());
//                    productTypeCombo.setMeasurement(saveProduct.getMeasurement());
                productTypeComboList.add(productTypeCombo);
            } else {
                ProductTypeCombo productTypeCombo = new ProductTypeCombo();
                productTypeCombo.setMainProduct(saveProduct);
//                productTypeCombo.setMeasurement(saveProduct.getMeasurement());
                productTypeCombo.setContentProduct(optionalProduct.get());
                productTypeCombo.setAmount(productTypeComboDto.getAmount());
                productTypeCombo.setBuyPrice(productTypeComboDto.getBuyPrice());
                productTypeCombo.setSalePrice(productTypeComboDto.getSalePrice());
                productTypeComboList.add(productTypeCombo);
            }
        }
        comboRepository.saveAll(productTypeComboList);
        return new ApiResponse("successfully saved", true);
    }

    private ApiResponse addProductTypeSingleDto(ProductDto productDto, Product product, boolean isUpdate) {
        product.setType(Type.SINGLE);
        product.setBuyPrice(productDto.getBuyPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setProfitPercent(productDto.getProfitPercent());
        if (productDto.getBarcode() != null && !productDto.getBarcode().isBlank()) {
            if (isUpdate) {
                if (productRepository.existsByBarcodeAndBusinessIdAndIdIsNotAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId(), product.getId()))
                    return new ApiResponse("product with the barcode is already exist");
            }else {
                if (productRepository.existsByBarcodeAndBusinessIdAndActiveTrue(productDto.getBarcode(), product.getBusiness().getId()))
                    return new ApiResponse("product with the barcode is already exist");
            }
            product.setBarcode(productDto.getBarcode());
        } else {
            product.setBarcode(generateBarcode(product.getBusiness().getId(), product.getName(), product.getId(), isUpdate));
        }

        productRepository.save(product);

        return new ApiResponse("successfully added", true);
    }


    private ApiResponse addProductTypeManyDto(ProductDto productDto, Product product, boolean isUpdate) {
        product.setType(Type.MANY);

        Product saveProduct = productRepository.save(product);
        List<ProductTypePrice> productTypePriceList = new ArrayList<>();

        for (ProductTypePricePostDto typePricePostDto : productDto.getProductTypePricePostDtoList()) {
            Optional<ProductTypeValue> optionalProductTypeValue = productTypeValueRepository.findById(typePricePostDto.getProductTypeValueId());
            if (optionalProductTypeValue.isEmpty()) return new ApiResponse("not found product type value", false);
            if (typePricePostDto.getProductTypePriceId() != null) {
                Optional<ProductTypePrice> typePriceOptional = productTypePriceRepository.findById(typePricePostDto.getProductTypePriceId());
                if (typePriceOptional.isEmpty()) {
                    return new ApiResponse("not found product type many id", false);
                }

                ProductTypeValue productTypeValue = optionalProductTypeValue.get();
                ProductTypePrice productTypePrice = typePriceOptional.get();
                productTypePrice.setProduct(saveProduct);
                productTypePrice.setName(product.getName() + "( " + productTypeValue.getProductType().getName() + " - " + productTypeValue.getName() + " )");
                productTypePrice.setProductTypeValue(optionalProductTypeValue.get());
                productTypePrice.setBuyPrice(typePricePostDto.getBuyPrice());
                productTypePrice.setSalePrice(typePricePostDto.getSalePrice());
                productTypePrice.setProfitPercent(typePricePostDto.getProfitPercent());
                if (typePricePostDto.getBarcode() != null && !typePricePostDto.getBarcode().isBlank()) {
                    if (productTypePriceRepository.existsByBarcodeAndProduct_BusinessIdAndIdIsNot(typePricePostDto.getBarcode(), product.getBusiness().getId(), productTypePrice.getId())) {
                        return new ApiResponse("product with the barcode is already exist");
                    }
                    productTypePrice.setBarcode(typePricePostDto.getBarcode());
                }else {
                    productTypePrice.setBarcode(generateBarcode(saveProduct.getBusiness().getId(), saveProduct.getName(), productTypePrice.getId(), true));
                }
                productTypePriceList.add(productTypePrice);
            } else {
                ProductTypeValue productTypeValue = optionalProductTypeValue.get();
                ProductTypePrice productTypePrice = new ProductTypePrice();
                productTypePrice.setProduct(saveProduct);
                productTypePrice.setName(product.getName() + "( " + productTypeValue.getProductType().getName() + " - " + productTypeValue.getName() + " )");
                productTypePrice.setProductTypeValue(optionalProductTypeValue.get());
                productTypePrice.setBuyPrice(typePricePostDto.getBuyPrice());
                productTypePrice.setSalePrice(typePricePostDto.getSalePrice());
                productTypePrice.setProfitPercent(typePricePostDto.getProfitPercent());
                if (typePricePostDto.getBarcode() != null && !typePricePostDto.getBarcode().isBlank()) {
                    if (productTypePriceRepository.existsByBarcodeAndProduct_BusinessId(typePricePostDto.getBarcode(), product.getBusiness().getId())) {
                        return new ApiResponse("product with the barcode is already exist");
                    }
                    productTypePrice.setBarcode(typePricePostDto.getBarcode());
                }else {
                    productTypePrice.setBarcode(generateBarcode(saveProduct.getBusiness().getId(), saveProduct.getName(), productTypePrice.getId(), false));
                }
                productTypePriceList.add(productTypePrice);
            }
        }

        productTypePriceRepository.saveAll(productTypePriceList);
        return new ApiResponse("successfully saved", true);
    }

    private String generateBarcode(UUID businessId, String productName, UUID productId, boolean isUpdate) {
        String name = productName.toLowerCase();
        StringBuilder str = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        str.append(name.charAt(0));
        str.reverse();
        String barcode = str.substring(0, 9);
        if (isUpdate) {
            if (productRepository.existsByBarcodeAndBusinessIdAndIdIsNotAndActiveTrue(barcode, businessId, productId) || productTypePriceRepository.existsByBarcodeAndProduct_BusinessIdAndIdIsNot(barcode, businessId, productId))
                return generateBarcode(businessId, productName, productId, isUpdate);
            return barcode;
        }else {
            if (productRepository.existsByBarcodeAndBusinessIdAndActiveTrue(barcode, businessId) || productTypePriceRepository.existsByBarcodeAndProduct_BusinessId(barcode, businessId))
                return generateBarcode(businessId, productName, productId, isUpdate);
            return barcode;
        }
    }

    public ApiResponse getAll(User user) {
        UUID businessId = user.getBusiness().getId();
        Set<Branch> branches = user.getBranches();
        List<Product> productList = new ArrayList<>();
        for (Branch branch : branches) {
            List<Product> all = productRepository.findAllByBranchIdAndActiveIsTrue(branch.getId());
            if (!all.isEmpty()) {
                for (Product product : all) {
                    Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(businessId);
                    if (!currency.getName().equalsIgnoreCase("SO'M")) {
                        CurrentCource cource = currentCourceRepository.getByCurrencyId(currency.getId());
                        product.setSalePrice(product.getSalePrice() / cource.getCurrentCourse());
                        product.setBuyPrice(product.getBuyPrice() / cource.getCurrentCourse());
                    } else {
                        break;
                    }
                }
                productList.addAll(all);
            }
        }
        if (productList.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        }
        return new ApiResponse("FOUND", true, productList);
    }

    public ApiResponse getProduct(UUID id, User user) {
        Set<Branch> branches = user.getBranches();
        for (Branch branch : branches) {
            Optional<Product> optionalProduct = productRepository.findByIdAndBranchIdAndActiveTrue(id, branch.getId());
            if (optionalProduct.isPresent()) {
                return getProductHelper(branch, optionalProduct.get());
            }
        }
        return new ApiResponse("NOT FOUND", false);
    }

    public ApiResponse getProductHelper(Branch branch, Product product) {
        ProductGetDto productGetDto = new ProductGetDto(product);

        if (product.getType().name().equals(Type.SINGLE.name())) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductId(branch.getId(), product.getId());
            product.setQuantity(optionalWarehouse.map(Warehouse::getAmount).orElse(0d));
            return new ApiResponse("productGetDto", true, productGetDto);
        } else if (product.getType().name().equals(Type.MANY.name())) {
            List<ProductTypePriceGetDto> productTypePriceGetDtoList = new ArrayList<>();
            List<ProductTypePrice> allByProductId = productTypePriceRepository.findAllByProductId(product.getId());
            for (ProductTypePrice productTypePrice : allByProductId) {
                ProductTypePriceGetDto productTypePriceGetDto = new ProductTypePriceGetDto();

                productTypePriceGetDto.setProductTypePriceId(productTypePrice.getId());
                productTypePriceGetDto.setProductTypeName(productTypePrice.getProductTypeValue().getProductType().getName());
                productTypePriceGetDto.setProductTypeValueName(productTypePrice.getProductTypeValue().getName());
                productTypePriceGetDto.setBarcode(productTypePrice.getBarcode());
                productTypePriceGetDto.setProfitPercent(productTypePrice.getProfitPercent());
                productTypePriceGetDto.setBuyPrice(productTypePrice.getBuyPrice());
                productTypePriceGetDto.setSalePrice(productTypePrice.getSalePrice());
                productTypePriceGetDto.setProductTypeValueNameId(productTypePrice.getProductTypeValue().getId());
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductTypePriceId(branch.getId(), productTypePrice.getId());
                productTypePriceGetDto.setQuantity(optionalWarehouse.map(Warehouse::getAmount).orElse(0d));
                productTypePriceGetDtoList.add(productTypePriceGetDto);
            }
            productGetDto.setProductTypePriceGetDtoList(productTypePriceGetDtoList);
            return new ApiResponse("productGetDto", true, productGetDto);

        } else {

            List<ProductTypeComboGetDto> comboGetDtoList = new ArrayList<>();
            List<ProductTypeCombo> allComboProduct = comboRepository.findAllByMainProductId(product.getId());
            for (ProductTypeCombo combo : allComboProduct) {
                ProductTypeComboGetDto comboGetDto = new ProductTypeComboGetDto();
                comboGetDto.setComboId(combo.getId());
                comboGetDto.setContentProduct(combo.getContentProduct());
                comboGetDto.setAmount(combo.getAmount());
                comboGetDto.setBuyPrice(combo.getBuyPrice());
                comboGetDto.setSalePrice(combo.getSalePrice());

                comboGetDtoList.add(comboGetDto);
            }

            productGetDto.setComboGetDtoList(comboGetDtoList);
            return new ApiResponse("productGetDto", true, productGetDto);
        }
    }

    public ApiResponse deleteProduct(UUID id, User user) {
        Set<Branch> branches = user.getBranches();
        for (Branch branch : branches) {
            Optional<Product> optionalProduct = productRepository.findByIdAndBranchIdAndActiveTrue(id, branch.getId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setActive(false);
                productRepository.save(product);
                return new ApiResponse("DELETED", true);
            }
        }
        return new ApiResponse("NOT FOUND", false);
    }

    /**
     * todo
     * @param barcode
     * @param user
     * @return
     */
    public ApiResponse getByBarcode(String barcode, User user) {
        Set<Branch> branches = user.getBranches();
        List<Product> productAllByBarcode = new ArrayList<>();
        for (Branch branch : branches) {
            Optional<Product> optionalProduct = productRepository.findAllByBarcodeAndBranchIdAndActiveTrue(barcode, branch.getId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(product.getBrand().getBusiness().getId());
                if (!currency.getName().equalsIgnoreCase("SO'M")) {
                    CurrentCource cource = currentCourceRepository.getByCurrencyId(currency.getId());
                    product.setSalePrice(product.getSalePrice() / cource.getCurrentCourse());
                    product.setBuyPrice(product.getBuyPrice() / cource.getCurrentCourse());
                }
                productAllByBarcode.add(product);
            }
        }


        List<ProductViewDto> viewDtos = new ArrayList<>();
        for (Product product : productAllByBarcode) {
            ProductViewDto productViewDto = new ProductViewDto();
            productViewDto.setProductId(product.getId());
            productViewDto.setProductName(product.getName());
            if (product.getBrand() != null)
                productViewDto.setBrandName(product.getBrand().getName());
            productViewDto.setBuyPrice(product.getBuyPrice());
            productViewDto.setSalePrice(product.getSalePrice());
            productViewDto.setMinQuantity(product.getMinQuantity());
            productViewDto.setBranch(product.getBranch());
            productViewDto.setExpiredDate(product.getExpireDate());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct_Id(product.getId());
            if (optionalWarehouse.isPresent()) {
                Warehouse warehouse = optionalWarehouse.get();
                if (warehouse.getProduct().getId().equals(product.getId())) {
                    productViewDto.setAmount(warehouse.getAmount());
                }
            }
            viewDtos.add(productViewDto);
        }

        if (viewDtos.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        }
        return new ApiResponse("FOUND", true, viewDtos);
    }

    /**
     *
     * @param category_id
     * @param user
     * @return
     */
    public ApiResponse getByCategory(UUID category_id, User user) {
        Set<Branch> branches = user.getBranches();
        List<Product> productList = new ArrayList<>();
        for (Branch branch : branches) {
            List<Product> all = productRepository.findAllByCategoryIdAndBranchIdAndActiveTrue(category_id, branch.getId());
            if (!all.isEmpty()) {
                for (Product product : all) {
                    Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(product.getBrand().getBusiness().getId());
                    if (!currency.getName().equalsIgnoreCase("SO'M")) {
                        CurrentCource cource = currentCourceRepository.getByCurrencyId(currency.getId());
                        product.setSalePrice(product.getSalePrice() / cource.getCurrentCourse());
                        product.setBuyPrice(product.getBuyPrice() / cource.getCurrentCourse());
                    } else {
                        break;
                    }
                }
                productList.addAll(all);
            }
        }

        List<ProductViewDto> viewDtos = new ArrayList<>();
        for (Product product : productList) {
            ProductViewDto productViewDto = new ProductViewDto();
            productViewDto.setProductId(product.getId());
            productViewDto.setProductName(product.getName());
            if (product.getBrand() != null)
                productViewDto.setBrandName(product.getBrand().getName());
            productViewDto.setBuyPrice(product.getBuyPrice());
            productViewDto.setSalePrice(product.getSalePrice());
            productViewDto.setMinQuantity(product.getMinQuantity());
            productViewDto.setBranch(product.getBranch());
            productViewDto.setExpiredDate(product.getExpireDate());
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct_Id(product.getId());
            if (optionalWarehouse.isPresent()) {
                Warehouse warehouse = optionalWarehouse.get();
                if (warehouse.getProduct().getId().equals(product.getId())) {
                    productViewDto.setAmount(warehouse.getAmount());
                }
            }
            viewDtos.add(productViewDto);
        }


        if (viewDtos.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        }
        return new ApiResponse("FOUND", true, viewDtos);
    }

    /**
     *
     * @param brand_id
     * @param user
     * @return
     */
    public ApiResponse getByBrand(UUID brand_id, User user) {
        Set<Branch> branches = user.getBranches();
        List<Product> productList = new ArrayList<>();
        for (Branch branch : branches) {
            List<Product> all = productRepository.findAllByBrandIdAndBranchIdAndActiveTrue(brand_id, branch.getId());
            if (!all.isEmpty()) {
                for (Product product : all) {
                    Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(product.getBrand().getBusiness().getId());
                    if (!currency.getName().equalsIgnoreCase("SO'M")) {
                        CurrentCource cource = currentCourceRepository.getByCurrencyId(currency.getId());
                        product.setSalePrice(product.getSalePrice() / cource.getCurrentCourse());
                        product.setBuyPrice(product.getBuyPrice() / cource.getCurrentCourse());
                    } else {
                        break;
                    }
                }
                productList.addAll(all);
            }
        }

        List<ProductViewDto> viewDtos = new ArrayList<>();
        for (Product product : productList) {
            ProductViewDto productViewDto = new ProductViewDto();
            productViewDto.setProductId(product.getId());
            productViewDto.setProductName(product.getName());
            if (product.getBrand() != null)
                productViewDto.setBrandName(product.getBrand().getName());
            productViewDto.setBuyPrice(product.getBuyPrice());
            productViewDto.setSalePrice(product.getSalePrice());
            productViewDto.setMinQuantity(product.getMinQuantity());
            productViewDto.setBranch(product.getBranch());
            productViewDto.setExpiredDate(product.getExpireDate());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct_Id(product.getId());
            if (optionalWarehouse.isPresent()) {
                Warehouse warehouse = optionalWarehouse.get();
                if (warehouse.getProduct().getId().equals(product.getId())) {
                    productViewDto.setAmount(warehouse.getAmount());
                }
            }
            viewDtos.add(productViewDto);
        }

        if (viewDtos.isEmpty()) return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, viewDtos);
    }

    public ApiResponse getByBranchAndBarcode(UUID branch_id, User user, ProductBarcodeDto barcodeDto) {
        Set<Branch> branches = user.getBranches();
        for (Branch branch : branches) {
            if (branch.getId().equals(branch_id)) {
                return new ApiResponse("BRANCH NOT FOUND OR NOT ALLOWED", false);
            }
        }
        List<Product> productList = productRepository.findAllByBranchIdAndBarcodeOrNameAndActiveTrue(branch_id, barcodeDto.getBarcode(), barcodeDto.getName());

        if (productList.isEmpty()) {
            return new ApiResponse("PRODUCT NOT FOUND", false);
        }
        return new ApiResponse("FOUND", true, productList);


    }

    public ApiResponse getByBranch(UUID branch_id) {
        ProductViewDto productViewDto = new ProductViewDto();


        List<Product> productList = productRepository.findAllByBranchIdAndActiveIsTrue(branch_id);

        if (productList.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        } else {
            List<ProductViewDto> viewDtos = new ArrayList<>();
            for (Product product : productList) {
                productViewDto.setProductId(product.getId());
                productViewDto.setProductName(product.getName());
                if (product.getBrand() != null)
                    productViewDto.setBrandName(product.getBrand().getName());
                productViewDto.setBuyPrice(product.getBuyPrice());
                productViewDto.setSalePrice(product.getSalePrice());
                productViewDto.setMinQuantity(product.getMinQuantity());
                productViewDto.setBranch(product.getBranch());
                productViewDto.setExpiredDate(product.getExpireDate());

                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct_Id(product.getId());
                if (optionalWarehouse.isPresent()) {
                    Warehouse warehouse = optionalWarehouse.get();
                    if (warehouse.getProduct().getId().equals(product.getId())) {
                        productViewDto.setAmount(warehouse.getAmount());
                    }
                }
                viewDtos.add(productViewDto);
            }
            if (viewDtos.isEmpty()) return new ApiResponse("NOT FOUND", false);

            return new ApiResponse("FOUND", true, viewDtos);
        }
    }

    public ApiResponse getByBranchForSearch(UUID branch_id) {

        List<ProductGetForPurchaseDto> getForPurchaseDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAllByBranchIdAndActiveIsTrue(branch_id);
        if (productList.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        } else {
            for (Product product : productList) {
                if (product.getType().equals(Type.MANY)) {
                    List<ProductTypePrice> productTypePriceList = productTypePriceRepository.findAllByProductId(product.getId());
                    for (ProductTypePrice productTypePrice : productTypePriceList) {
                        ProductGetForPurchaseDto getForPurchaseDto = new ProductGetForPurchaseDto();
                        getForPurchaseDto.setProductTypePriceId(productTypePrice.getId());
                        getForPurchaseDto.setType(Type.MANY.name());
                        getForPurchaseDto.setName(productTypePrice.getName());
                        getForPurchaseDto.setBarcode(productTypePrice.getBarcode());
                        getForPurchaseDto.setBuyPrice(productTypePrice.getBuyPrice());
                        getForPurchaseDto.setSalePrice(productTypePrice.getSalePrice());
                        getForPurchaseDto.setProfitPercent(productTypePrice.getProfitPercent());
                        getForPurchaseDto.setMinQuantity(product.getMinQuantity());
                        getForPurchaseDto.setExpiredDate(product.getExpireDate());
                        getForPurchaseDto.setMeasurementName(product.getMeasurement().getName());
                        if (product.getMeasurement() != null)
                            getForPurchaseDto.setMeasurementName(product.getMeasurement().getName());
                        if (product.getBrand() != null) getForPurchaseDto.setBrandName(product.getBrand().getName());
                        if (productTypePrice.getPhoto() != null)
                            getForPurchaseDto.setPhotoId(productTypePrice.getPhoto().getId());
                        Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductTypePriceId(branch_id, productTypePrice.getId());
                        getForPurchaseDto.setAmount(optionalWarehouse.map(Warehouse::getAmount).orElse(0d));
                        getForPurchaseDtoList.add(getForPurchaseDto);
                    }
                } else{
                    ProductGetForPurchaseDto getForPurchaseDto = new ProductGetForPurchaseDto();
                    getForPurchaseDto.setProductId(product.getId());
                    getForPurchaseDto.setType(product.getType().name());
                    getForPurchaseDto.setName(product.getName());
                    getForPurchaseDto.setBarcode(product.getBarcode());
                    getForPurchaseDto.setBuyPrice(product.getBuyPrice());
                    getForPurchaseDto.setSalePrice(product.getSalePrice());
                    getForPurchaseDto.setMinQuantity(product.getMinQuantity());
                    getForPurchaseDto.setExpiredDate(product.getExpireDate());
                    if (product.getMeasurement() != null)
                        getForPurchaseDto.setMeasurementName(product.getMeasurement().getName());
                    if (product.getBrand() != null) getForPurchaseDto.setBrandName(product.getBrand().getName());
                    if (product.getPhoto() != null) getForPurchaseDto.setPhotoId(product.getPhoto().getId());
                    Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductId(branch_id, product.getId());
                    getForPurchaseDto.setAmount(optionalWarehouse.map(Warehouse::getAmount).orElse(0d));
                    getForPurchaseDtoList.add(getForPurchaseDto);
                }
            }
            return new ApiResponse("FOUND", true, getForPurchaseDtoList);
        }
    }

    public ApiResponse getByBusiness(UUID businessId) {
        List<ProductViewDto> productViewDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);
        if (productList.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        } else {
            for (Product product : productList) {
                ProductViewDto productViewDto = new ProductViewDto();
                productViewDto.setProductId(product.getId());
                productViewDto.setProductName(product.getName());
                if (product.getBrand() != null)
                    productViewDto.setBrandName(product.getBrand().getName());
                productViewDto.setBarcode(product.getBarcode());
                productViewDto.setBuyPrice(product.getBuyPrice());
                productViewDto.setSalePrice(product.getSalePrice());
                productViewDto.setMinQuantity(product.getMinQuantity());
                productViewDto.setBranch(product.getBranch());
                productViewDto.setExpiredDate(product.getExpireDate());
                if (product.getPhoto() != null) {
                    Optional<Attachment> attachmentOptional = attachmentRepository.findById(product.getPhoto().getId());
                    attachmentOptional.ifPresent(attachment -> productViewDto.setPhotoId(attachment.getId()));
                }
                Optional<Measurement> optionalMeasurement = measurementRepository.findById(product.getMeasurement().getId());
                optionalMeasurement.ifPresent(measurement -> productViewDto.setMeasurementId(measurement.getName()));
                List<Warehouse> allByByProductId = warehouseRepository.findAllByProduct_Id(product.getId());
                if (allByByProductId.isEmpty()) {
                    productViewDto.setAmount(0);
                } else {
                    double totalAmount = 0;
                    for (Warehouse warehouse : allByByProductId) {
                        totalAmount += warehouse.getAmount();
                    }
                    productViewDto.setAmount(totalAmount);
                }
                productViewDtoList.add(productViewDto);
            }
            return new ApiResponse("FOUND", true, productViewDtoList);
        }
    }

    public ApiResponse getByBranchProduct(UUID branchId) {
        List<ProductViewDto> productViewDtoList = new ArrayList<>();
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found");
        }

        List<Product> productList = productRepository.findAllByBranchIdAndActiveIsTrue(branchId);
        if (productList.isEmpty()) {
            return new ApiResponse("NOT FOUND", false);
        } else {
            for (Product product : productList) {
                ProductViewDto productViewDto = new ProductViewDto();
                productViewDto.setProductId(product.getId());
                productViewDto.setProductName(product.getName());
                if (product.getBrand() != null)
                    productViewDto.setBrandName(product.getBrand().getName());
                productViewDto.setBarcode(product.getBarcode());
                productViewDto.setBuyPrice(product.getBuyPrice());
                productViewDto.setSalePrice(product.getSalePrice());
                productViewDto.setMinQuantity(product.getMinQuantity());
                productViewDto.setBranch(product.getBranch());
                productViewDto.setExpiredDate(product.getExpireDate());
                if (product.getPhoto() != null)
                    productViewDto.setPhotoId(product.getPhoto().getId());
                if (product.getMeasurement() != null) productViewDto.setMeasurementId(product.getMeasurement().getName());
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductId(branchId, product.getId());
                optionalWarehouse.ifPresent(warehouse -> productViewDto.setAmount(warehouse.getAmount()));
                productViewDtoList.add(productViewDto);
            }
            return new ApiResponse("FOUND", true, productViewDtoList);
        }
    }


    public ApiResponse deleteProducts(List<UUID> ids) {
        for (UUID id : ids) {
            Optional<Product> optional = productRepository.findById(id);
            if (optional.isEmpty()) {
                return new ApiResponse("not found", false);
            }
            Product product = optional.get();
            product.setActive(false);
            productRepository.save(product);
        }
        return new ApiResponse("DELETED", true);
    }


}
