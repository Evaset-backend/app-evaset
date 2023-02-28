package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.payload.InfoOutlayDto;
import uz.pdp.springsecurity.repository.*;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final BusinessRepository businessRepository;
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;
    private final PaymentRepository paymentRepository;
    private final PayMethodRepository payMethodRepository;
    @Autowired
    OutlayRepository outlayRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    TradeProductRepository tradeProductRepository;
    private final static LocalDateTime TODAY = LocalDate.now().atStartOfDay();

    private final static Date date = new Date(System.currentTimeMillis());
    private final static Timestamp currentDay = new Timestamp(date.getTime());
    private final static Timestamp enDate = new Timestamp(date.getTime());
    private final static LocalDateTime dateTime = enDate.toLocalDateTime();
    private final static LocalDateTime LAST_MONTH = dateTime.minusMonths(1);
    private final static LocalDate localDate = LocalDate.now();
    private final static LocalDateTime THIS_MONTH = localDate.withDayOfMonth(1).atStartOfDay();
    private final static LocalDate TEMP_START_OF_YEAR = LocalDate.of(localDate.getYear() - 1, 1, 1);
    private final static LocalDate TEMP_FOR_THIS_START_OF_YEAR = LocalDate.of(localDate.getYear(), 1, 1);
    private final static LocalDate TEMP_START_OF_DAY = localDate.minusDays(1);
    private final static LocalDate TEMP_END_OF_DAY = LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
    private final static LocalDate TEMP_END_OF_YEAR = LocalDate.of(localDate.getYear() - 1, 12, 31);
    private final static LocalDate TEMP_START_OF_MONTH_ONE = LocalDate.of(localDate.getYear(), localDate.getMonth().getValue(), 1);
    private final static LocalDate TEMP_START_OF_MONTH = TEMP_START_OF_MONTH_ONE.minusMonths(1);
    private final static LocalDate TEMP_END_OF_MONTH = LocalDate.of(localDate.getYear(), TEMP_START_OF_MONTH.getMonth(), TEMP_START_OF_MONTH.lengthOfMonth());
    private final static LocalDateTime START_OF_YEAR = TEMP_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_YEAR_FOR_THIS = TEMP_FOR_THIS_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime END_OF_YEAR = TEMP_END_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_MONTH = TEMP_START_OF_MONTH.atStartOfDay();
    private final static LocalDateTime END_OF_MONTH = TEMP_END_OF_MONTH.atStartOfDay();
    private final static LocalDateTime START_OF_DAY = TEMP_START_OF_DAY.atStartOfDay();
    private final static LocalDateTime END_OF_DAY = TEMP_END_OF_DAY.atStartOfDay();

    public ApiResponse getInfoByBusiness(UUID businessId) {

        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }

        return getInfoHelper(businessId,
                purchaseRepository.findAllByBranch_BusinessId(businessId),
                tradeRepository.findAllByBranch_BusinessId(businessId),
                outlayRepository.findAllByBusinessId(businessId),
                paymentRepository.findAllByPayMethod_BusinessId(businessId)
        );
    }

    public ApiResponse getInfoByBranch(UUID branchId, String date, Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }

        Timestamp from = Timestamp.valueOf(TODAY);
        Timestamp to = new Timestamp(System.currentTimeMillis());
        if (startDate != null && endDate != null) {
            from = new Timestamp(startDate.getTime());
            to = new Timestamp(endDate.getTime());
        }
        if (Objects.equals(date, "LAST_DAY") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(START_OF_DAY);
            to = Timestamp.valueOf(END_OF_DAY);
        } else if (Objects.equals(date, "LAST_WEEK") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(LocalDateTime.now().minusDays(7));
            to = Timestamp.valueOf(LocalDateTime.now());
        } else if (Objects.equals(date, "LAST_THIRTY_DAY") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(LAST_MONTH);
            to = Timestamp.valueOf(LocalDateTime.now());
        } else if (Objects.equals(date, "LAST_MONTH") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(START_OF_MONTH);
            to = Timestamp.valueOf(END_OF_MONTH);
        } else if (Objects.equals(date, "THIS_MONTH") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(THIS_MONTH);
            to = Timestamp.valueOf(LocalDateTime.now());
        } else if (Objects.equals(date, "LAST_YEAR") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(START_OF_YEAR);
            to = Timestamp.valueOf(END_OF_YEAR);
        } else if (Objects.equals(date, "THIS_YEAR") && startDate == null && endDate == null) {
            from = Timestamp.valueOf(START_OF_YEAR_FOR_THIS);
            to = Timestamp.valueOf(LocalDateTime.now());
        } else if (startDate != null && endDate != null && date == null) {
            from = new Timestamp(startDate.getTime());
            to = new Timestamp(endDate.getTime());

        }
        UUID businessId = optionalBranch.get().getBusiness().getId();

        if (Objects.equals(date, "ALL") && startDate == null && endDate == null) {
            return getInfoHelper(
                    businessId,
                    purchaseRepository.findAllByBranch_Id(branchId),
                    tradeRepository.findAllByBranch_Id(branchId),
                    outlayRepository.findAllByBranch_Id(branchId),
                    paymentRepository.findAllByTrade_BranchId(branchId)
            );
        }

        return getInfoHelper(
                businessId,
                purchaseRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                tradeRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                outlayRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                paymentRepository.findAllByCreatedAtBetweenAndTrade_BranchId(from, to, branchId)
        );

    }

    private ApiResponse getInfoHelper(UUID businessId, List<Purchase> purchaseList, List<Trade> tradeList, List<Outlay> outlayList, List<Payment> paymentList) {


        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        double allTrade = 0;
        double allTradeDebt = 0;
        double totalProfit = 0;
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
            totalProfit += trade.getTotalProfit();
        }
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);
        infoDto.setTotalProfit(totalProfit);


        double totalOutlay = 0;
        for (Outlay outlay : outlayList) {
            totalOutlay += outlay.getTotalSum();
        }
        infoDto.setMyOutlay(totalOutlay);

        HashMap<String, Double> byPayMethods = new HashMap<>();
        List<PaymentMethod> paymentMethodList = payMethodRepository.findAllByBusiness_Id(businessId);
        for (PaymentMethod paymentMethod : paymentMethodList) {
            byPayMethods.put(paymentMethod.getType(), 0d);
        }
        for (Payment payment : paymentList) {
            String type = payment.getPayMethod().getType();
            byPayMethods.put(type, byPayMethods.getOrDefault(type, 0d) + payment.getPaidSum());
        }
        infoDto.setByPayMethods(byPayMethods);

        return new ApiResponse("FOUND", true, infoDto);
    }

    public ApiResponse getInfoByOutlayAndTrade(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

        LocalDateTime now = LocalDateTime.now();
        HashMap<Integer, Double> outlayMap = new HashMap();
        HashMap<Integer, Double> tradeMap = new HashMap<>();
        List<Timestamp> timestampList = new ArrayList<>();
        timestampList.add(Timestamp.valueOf(now));
        for (int i = 0; i < 6; i++) {
            Timestamp from = Timestamp.valueOf(now.minusDays(5 * (i + 1)));
            Timestamp to = Timestamp.valueOf(now.minusDays(5 * i));
            List<Outlay> outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId);
            List<Trade> tradeList = tradeRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId);
            double totalOutlay = 0;
            double totalTrade = 0;

            if (!outlayList.isEmpty()) {
                for (Outlay outlay : outlayList) {
                    if (outlay != null) {
                        totalOutlay += outlay.getTotalSum();
                    }
                }
            }
            if (!tradeList.isEmpty()) {
                for (Trade trade : tradeList) {
                    if (trade != null) {
                        totalTrade += trade.getTotalSum();
                    }
                }
            }
            outlayMap.put(6 - i, totalOutlay);
            tradeMap.put(6 - i, totalTrade);
            timestampList.add(from);
        }

        InfoOutlayDto infoDto = new InfoOutlayDto(
                outlayMap,
                tradeMap,
                timestampList
        );
        return new ApiResponse(true, infoDto);
    }

    public ApiResponse getInfoByOutlayTradeByBusiness(UUID businessId) {
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (optionalBusiness.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }
        LocalDateTime now = LocalDateTime.now();
        HashMap<Integer, Double> outlayMap = new HashMap();
        HashMap<Integer, Double> tradeMap = new HashMap<>();
        List<Timestamp> timestampList = new ArrayList<>();
        timestampList.add(Timestamp.valueOf(now));
        for (int i = 0; i < 6; i++) {
            Timestamp from = Timestamp.valueOf(now.minusDays(5 * (i + 1)));
            Timestamp to = Timestamp.valueOf(now.minusDays(5 * i));
            List<Outlay> outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranch_BusinessId(from, to, businessId);
            List<Trade> tradeList = tradeRepository.findAllByCreatedAtBetweenAndBranch_BusinessId(from, to, businessId);
            double totalOutlay = 0;
            double totalTrade = 0;

            if (!outlayList.isEmpty()) {
                for (Outlay outlay : outlayList) {
                    if (outlay != null) {
                        totalOutlay += outlay.getTotalSum();
                    }
                }
            }
            if (!tradeList.isEmpty()) {
                for (Trade trade : tradeList) {
                    if (trade != null) {
                        totalTrade += trade.getTotalSum();
                    }
                }
            }
            outlayMap.put(6 - i, totalOutlay);
            tradeMap.put(6 - i, totalTrade);
            timestampList.add(from);
        }

        InfoOutlayDto infoDto = new InfoOutlayDto(
                outlayMap,
                tradeMap,
                timestampList
        );
        return new ApiResponse(true, infoDto);
    }
}
