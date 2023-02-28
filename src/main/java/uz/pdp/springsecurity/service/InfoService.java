package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.payload.OutlayTrade;
import uz.pdp.springsecurity.repository.*;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final BusinessRepository businessRepository;
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;
    private final PaymentRepository paymentRepository;
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

        return getInfoHelper(
                purchaseRepository.findAllByBranch_BusinessId(businessId),
                tradeRepository.findAllByBranch_BusinessId(businessId),
                outlayRepository.findAllByBusinessId(businessId),
                tradeProductRepository.findAllByProduct_BusinessId(businessId),
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

        if (Objects.equals(date, "ALL") && startDate == null && endDate == null) {
            return getInfoHelper(
                    purchaseRepository.findAllByBranch_Id(branchId),
                    tradeRepository.findAllByBranch_Id(branchId),
                    outlayRepository.findAllByBranch_Id(branchId),
                    tradeProductRepository.findAllByProduct_BranchId(branchId),
                    paymentRepository.findAllByTrade_BranchId(branchId)
            );
        }

        return getInfoHelper(
                purchaseRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                tradeRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                outlayRepository.findAllByCreatedAtBetweenAndBranchId(from, to, branchId),
                tradeProductRepository.findAllByCreatedAtBetweenAndTrade_BranchId(from, to, branchId),
                paymentRepository.findAllByCreatedAtBetweenAndTrade_BranchId(from, to, branchId)
        );

    }

    private ApiResponse getInfoHelper(List<Purchase> purchaseList, List<Trade> tradeList, List<Outlay> outlayList, List<TradeProduct> tradeProductList, List<Payment> paymentList) {


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
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);

        double totalProfit = 0;
        for (TradeProduct tradeProduct : tradeProductList) {
            totalProfit += (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getSalePrice()) - (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getBuyPrice());
        }
        infoDto.setTotalProfit(totalProfit);

        double totalOutlay = 0;
        for (Outlay outlay : outlayList) {
            totalOutlay += outlay.getTotalSum();
        }
        infoDto.setMyOutlay(totalOutlay);

        HashMap<String, Double> byPayMethods = new HashMap<>();
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
        LocalDate one = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate two  = LocalDate.now().minusMonths(1).withDayOfMonth(1).plusDays(4);
        LocalDate tree  = two.plusDays(5);
        LocalDate four  = tree.plusDays(5);
        LocalDate five  = four.plusDays(5);
        LocalDate six  = five.plusDays(5);
        LocalDate seven  = LocalDate.of(six.getYear(),six.getMonth(), six.lengthOfMonth());
        List<Outlay> outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranchId(Timestamp.valueOf(one.atStartOfDay()),Timestamp.valueOf(two.atStartOfDay()),branchId);
        OutlayTrade outlayTrade=new OutlayTrade();


        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByTrade_BranchId(branchId);

        return new ApiResponse("Found",true);
    }
}
