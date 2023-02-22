package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Subscription;
import uz.pdp.springsecurity.enums.Lifetime;
import uz.pdp.springsecurity.enums.StatusTariff;
import uz.pdp.springsecurity.mapper.SubscriptionMapper;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.SubscriptionPostDto;
import uz.pdp.springsecurity.repository.SubscriptionRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    private final SubscriptionMapper mapper;

    public ApiResponse create(SubscriptionPostDto subscriptionPostDto) {
        //business va tariff id xatosiz berilishi kerak chunki tekshirilib ketmad
        Subscription subscription = mapper.toEntity(subscriptionPostDto);
        subscription.setActive(false);
        subscription.setActiveNewTariff(subscriptionPostDto.isActiveNewTariff());
        repository.save(subscription);
        return new ApiResponse("Added", true);
    }

    public ApiResponse getAllSubscription() {
        List<Subscription> subscriptionList = repository.findAllByDeleteIsFalse();
        return new ApiResponse("All Subscription", true, mapper.toDtoList(subscriptionList));
    }

    public ApiResponse confirmSubscription(UUID subscriptionId, String statusTariff) {
        Optional<Subscription> optionalSubscription = repository.findById(subscriptionId);
        if (optionalSubscription.isEmpty()) new ApiResponse("not found subscription", false);
        Subscription subscription = optionalSubscription.get();
        Lifetime lifetime = subscription.getTariff().getLifetime();
        int interval = subscription.getTariff().getInterval();
        if (statusTariff.equalsIgnoreCase(StatusTariff.CONFIRMED.name())) {
            Optional<Subscription> optional = repository.findByBusinessIdAndActiveTrue(subscription.getBusiness().getId());
            if (subscription.isActiveNewTariff()) {
                if (optional.isPresent()) {
                    Subscription oldSubscription = optional.get();
                    oldSubscription.setActive(false);
                    oldSubscription.setDelete(true);
                    repository.save(oldSubscription);
                }
                if (lifetime.equals(Lifetime.MONTH)) {
                    LocalDate date = LocalDate.now().plusMonths(interval);
                    Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
                    subscription.setEndDay(timestamp);
                } else if (lifetime.equals(Lifetime.YEAR)) {
                    LocalDate date = LocalDate.now().plusYears(interval);
                    Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
                    subscription.setEndDay(timestamp);
                }
                subscription.setStartDay(new Timestamp(System.currentTimeMillis()));
                subscription.setStatusTariff(StatusTariff.CONFIRMED);
                subscription.setActiveNewTariff(false);
                subscription.setActive(true);
            } else {
                if (optional.isPresent()) {
                    Subscription subscriptionBusiness = optional.get();
                    Timestamp activeTariffEndDay = subscriptionBusiness.getEndDay();

                    if (lifetime.equals(Lifetime.MONTH)) {
                        LocalDate localDate = activeTariffEndDay.toLocalDateTime().toLocalDate().plusMonths(interval);
                        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
                        subscription.setEndDay(timestamp);
                    } else if (lifetime.equals(Lifetime.YEAR)) {
                        LocalDate localDate = activeTariffEndDay.toLocalDateTime().toLocalDate().plusYears(interval);
                        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
                        subscription.setEndDay(timestamp);
                    }
                    subscription.setStartDay(activeTariffEndDay);
                    subscription.setStatusTariff(StatusTariff.CONFIRMED);
                    subscription.setActive(false);
                }
            }
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        } else if (statusTariff.equalsIgnoreCase(StatusTariff.REJECTED.name())) {
            subscription.setActive(false);
            subscription.setStatusTariff(StatusTariff.REJECTED);
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        } else if (statusTariff.equalsIgnoreCase(StatusTariff.WAITING.name())) {
            subscription.setActive(false);
            subscription.setStatusTariff(StatusTariff.WAITING);
            repository.save(subscription);
            return new ApiResponse("SUCCESS", true);
        }
        return new ApiResponse("wrong statusTariff", false);
    }

    public ApiResponse active(UUID subscriptionId) {
        Optional<Subscription> optionalSubscription = repository.findById(subscriptionId);
        if (optionalSubscription.isEmpty()) {
            return new ApiResponse("not found subscription", false);
        }
        Subscription subscription = optionalSubscription.get();
        if (subscription.getStatusTariff().equals(StatusTariff.CONFIRMED) && !subscription.isActive()) {
            Optional<Subscription> optional = repository.findByBusinessIdAndActiveTrue(subscription.getBusiness().getId());
            if (optional.isPresent()) {
                Subscription oldSubscription = optional.get();
                oldSubscription.setActive(false);
                oldSubscription.setDelete(true);
                repository.save(oldSubscription);
            }
            Lifetime lifetime = subscription.getTariff().getLifetime();
            int interval = subscription.getTariff().getInterval();

            if (lifetime.equals(Lifetime.MONTH)) {
                LocalDate date = LocalDate.now().plusMonths(interval);
                Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
                subscription.setEndDay(timestamp);
            } else if (lifetime.equals(Lifetime.YEAR)) {
                LocalDate date = LocalDate.now().plusYears(interval);
                Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
                subscription.setEndDay(timestamp);
            }
            subscription.setStartDay(new Timestamp(System.currentTimeMillis()));
            subscription.setActiveNewTariff(false);
            subscription.setActive(true);
            repository.save(subscription);
            return new ApiResponse("successfully active", true);
        }
        return new ApiResponse("not verified by super admin or while your tariff is active", false);
    }
}