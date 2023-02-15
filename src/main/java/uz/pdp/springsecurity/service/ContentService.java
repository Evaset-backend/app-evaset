package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ContentDto;
import uz.pdp.springsecurity.payload.ContentProductDto;
import uz.pdp.springsecurity.payload.GetOneContentProductionDto;
import uz.pdp.springsecurity.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final ContentProductRepository contentProductRepository;
    private final ProductRepository productRepository;
    private final ProductTypePriceRepository productTypePriceRepository;

    public ApiResponse add(Business business, ContentDto contentDto){
        Content content = new Content();
        content.setBusiness(business);
        return createOrEdit(content, contentDto);
    }

    public ApiResponse edit(UUID contentId, ContentDto contentDto) {
        Optional<Content> optionalContent = contentRepository.findById(contentId);
        if (optionalContent.isEmpty())return new ApiResponse("NOT FOUND", false);
        return createOrEdit(optionalContent.get(), contentDto);
    }

    private ApiResponse createOrEdit(Content content, ContentDto contentDto) {
        if (contentDto.getProductId() != null) {
            Optional<Product> optional = productRepository.findById(contentDto.getProductId());
            if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT", false);
            content.setProduct(optional.get());
        } else {
            Optional<ProductTypePrice> optional = productTypePriceRepository.findById(contentDto.getProductTypePriceId());
            if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT TYPE PRICE", false);
            content.setProductTypePrice(optional.get());
        }

        content.setQuantity(contentDto.getQuantity());
        content.setCostForPerQuantity(contentDto.getCostForPerQuantity());
        content.setTotalPrice(contentDto.getTotalPrice());
        contentRepository.save(content);

        List<ContentProductDto> contentProductDtoList = contentDto.getContentProductDtoList();
        for (ContentProductDto contentProductDto : contentProductDtoList) {
            if (contentProductDto.getProductId() != null) {
                Optional<Product> optional = productRepository.findById(contentProductDto.getProductId());
                if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT", false);
                content.setProduct(optional.get());
            } else {
                Optional<ProductTypePrice> optional = productTypePriceRepository.findById(contentProductDto.getProductTypePriceId());
                if (optional.isEmpty())return new ApiResponse("NOT FOUND PRODUCT TYPE PRICE", false);
                content.setProductTypePrice(optional.get());
            }
        }
        return new ApiResponse();
    }

    public ApiResponse getAll(Business business) {
        List<Content> contentList = contentRepository.findAll();
        if (contentList.isEmpty())return new ApiResponse("NOT FOUND", false);
        return new ApiResponse(true, contentList);
    }

    public ApiResponse getOne(UUID contentId) {
        Optional<Content> optionalContent = contentRepository.findById(contentId);
        if (optionalContent.isEmpty())return new ApiResponse("NOT FOUND", false);
        Content content = optionalContent.get();
        List<ContentProduct> contentProductList = contentProductRepository.findAllByContentId(contentId);
        if (contentProductList.isEmpty())return new ApiResponse("NOT FOUND CONTENT PRODUCTS", false);
        GetOneContentProductionDto getOneContentProductionDto = new GetOneContentProductionDto(
                content,
                contentProductList
        );
        return new ApiResponse(true, getOneContentProductionDto);
    }

    public ApiResponse deleteOne(UUID contentId) {
        if (!contentRepository.existsById(contentId))return new ApiResponse("NOT FOUND", false);
        contentRepository.deleteById(contentId);
        return new ApiResponse("SUCCESS", true);
    }
}
