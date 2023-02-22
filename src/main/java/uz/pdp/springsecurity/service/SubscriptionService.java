package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Subscription;
import uz.pdp.springsecurity.enums.StatusTariff;
import uz.pdp.springsecurity.mapper.SubscriptionMapper;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.SubscriptionGetDto;
import uz.pdp.springsecurity.payload.SubscriptionPostDto;
import uz.pdp.springsecurity.repository.SubscriptionRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    private final SubscriptionMapper mapper;

    public ApiResponse create(SubscriptionPostDto subscriptionPostDto) {
        //business va tariff id xatosiz berilishi kerak chunki tekshirilib ketmadi
        Subscription subscription = mapper.toEntity(subscriptionPostDto);
        subscription.setActive(false);
//        subscription.setStatusTariff(StatusTariff.WAITING);
        repository.save(subscription);
        return new ApiResponse("Added", true);
    }

    public ApiResponse getAllSubscription() {
        List<Subscription> subscriptionList = repository.findAll();
        return new ApiResponse("All Subscription", true, mapper.toDtoList(subscriptionList));
    }

    public ApiResponse confirmSubscription(UUID subscriptionId, String statusTariff) {
        Optional<Subscription> optionalSubscription = repository.findById(subscriptionId);
        if (optionalSubscription.isEmpty()) new ApiResponse("not found subscription", false);
        Subscription subscription = optionalSubscription.get();
        if (statusTariff.equalsIgnoreCase(StatusTariff.CONFIRMED.name())) {

            LocalDate date = LocalDate.now().plusMonths(1);
            Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());

            subscription.setStartDay(new Timestamp(System.currentTimeMillis()));
            subscription.setStatusTariff(StatusTariff.CONFIRMED);
            subscription.setEndDay(timestamp);
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        } else if (statusTariff.equalsIgnoreCase(StatusTariff.REJECTED.name())) {
            subscription.setStatusTariff(StatusTariff.REJECTED);
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        } else if (statusTariff.equalsIgnoreCase(StatusTariff.WAITING.name())) {
            subscription.setStatusTariff(StatusTariff.WAITING);
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        }
        return new ApiResponse("wrong statusTariff", false);
    }
}
