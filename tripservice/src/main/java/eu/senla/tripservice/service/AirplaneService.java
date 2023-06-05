package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.exeption.airplane.AirplaneNotFoundException;
import eu.senla.tripservice.repository.AirplaneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AirplaneService {
    private final AirplaneRepository airplaneRepository;

    @Autowired
    public AirplaneService(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    public Airplane findById(long id) {
        log.info("AirplaneService-findById: " + id);
        return airplaneRepository.findById(id).
                orElseThrow(() -> new AirplaneNotFoundException("Airplane with id: " + id + " not found"));
    }
}
