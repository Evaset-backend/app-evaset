package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.pdp.springsecurity.entity.Subscription;
import uz.pdp.springsecurity.repository.SubscriptionRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleTaskService {

    private final SubscriptionRepository subscriptionRepository;

//    @Scheduled(cron = "0 0/51 12 * * *")
//    public void execute() {
//        List<Subscription> allByDeleteIsFalse = subscriptionRepository.findAllByDeleteIsFalse();
//        Date today = new Date(System.currentTimeMillis());
//
//        for (Subscription subscription : allByDeleteIsFalse) {
//            Date dateEndDay = new Date(subscription.getEndDay().getTime());
////            if () {
////
////            }
//            subscription.setActive(true);
//            subscriptionRepository.save(subscription);
//        }
//    }
}

