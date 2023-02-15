package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Tariff;
import uz.pdp.springsecurity.mapper.TariffMapper;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.TariffDto;
import uz.pdp.springsecurity.repository.TariffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository repository;
    private final TariffMapper mapper;

    public ApiResponse getAll() {
        List<Tariff> allByDeletedIsFalse = repository.findAll();
        return new ApiResponse("all tariff", true, mapper.toDtoList(allByDeletedIsFalse));
    }

    public ApiResponse getById(UUID id) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            return new ApiResponse("not found tariff", false);
        }
        Tariff tariff = optionalTariff.get();

        return new ApiResponse("found", true, mapper.toDto(tariff));
    }


    public ApiResponse create(TariffDto tariffDto) {
        Tariff tariff = mapper.toEntity(tariffDto);
        repository.save(tariff);
        return new ApiResponse("successfully saved tariff", true);
    }


    public ApiResponse edit(UUID id, TariffDto tariffDto) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            return new ApiResponse("not found tariff", false);
        }

        Tariff tariff = optionalTariff.get();
        mapper.update(tariffDto, tariff);
        repository.save(tariff);

        return new ApiResponse("successfully edited", true);
    }

    public ApiResponse delete(UUID id) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            return new ApiResponse("not found tariff", false);
        }

        Tariff tariff = optionalTariff.get();
        tariff.setDelete(true);
        repository.save(tariff);
        return new ApiResponse("successfully deleted", true);
    }
}
