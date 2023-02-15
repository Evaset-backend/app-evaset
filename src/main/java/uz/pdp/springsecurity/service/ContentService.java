package uz.pdp.springsecurity.service;

import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ContentDto;

import java.util.List;
import java.util.UUID;

@Service
public class ContentService {

    public ApiResponse add(Business business, ContentDto contentDto){

        return new ApiResponse();
    }

    public ApiResponse edit(UUID contentId, ContentDto contentDto) {
        return new ApiResponse();
    }

    public ApiResponse getAll(Business business) {
        return new ApiResponse();
    }

    public ApiResponse getOne(UUID contentId) {
        return new ApiResponse();
    }

    public ApiResponse deleteOne(UUID contentId) {
        return new ApiResponse();
    }

    public ApiResponse deleteFew(List<UUID> contentIdList) {
        return new ApiResponse();
    }
}
