package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Notification;
import uz.pdp.springsecurity.mapper.NotificationMapper;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.repository.NotificationRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final NotificationMapper mapper;

    public ApiResponse getAll() {
        List<Notification> notificationList = new ArrayList<>();
        List<Notification> allByReadIsFalse = repository.findAllByReadIsFalse();
        List<Notification> allByReadIsTrue = repository.findAllByReadIsTrue();

        allByReadIsFalse.sort(Comparator.comparing(Notification::getCreatedAt));
        allByReadIsTrue.sort(Comparator.comparing(Notification::getCreatedAt));

        notificationList.addAll(notificationList);

        return new ApiResponse("all notification", true, mapper.toDtoGetAll(notificationList));
    }

    public ApiResponse getById(UUID id) {
        Optional<Notification> byId = repository.findById(id);
        if (byId.isEmpty()) {
            return new ApiResponse("not found", false);
        }

        Notification notification = byId.get();
        notification.setRead(true);

        return new ApiResponse("found", true, mapper.toDtoGetById(notification));
    }

    public ApiResponse delete() {
        List<Notification> allByReadIsTrue = repository.findAllByReadIsTrue();
        if (!allByReadIsTrue.isEmpty()){
            repository.deleteAll(allByReadIsTrue);
        }

        return new ApiResponse("deleted",true);
    }
}
