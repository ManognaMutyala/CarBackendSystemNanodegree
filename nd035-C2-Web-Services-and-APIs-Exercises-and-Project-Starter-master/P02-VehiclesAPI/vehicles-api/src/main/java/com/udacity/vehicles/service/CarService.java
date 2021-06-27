package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;


    @Autowired
    PriceClient priceClient;

    @Autowired
    MapsClient mapsClient;
 /*
    @Autowired
    WebClient webClientMaps;

    @Autowired
    WebClient webClientPricing;

  */
    public CarService(CarRepository repository,PriceClient priceClient ,MapsClient mapsClient ) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.repository = repository;
        this.priceClient=priceClient;
        this.mapsClient=mapsClient;
    }
/*
   public CarService(CarRepository repository,WebClient webClientMaps ,WebClient webClientPricing ) {

        this.repository = repository;
        this.webClientMaps=webClientMaps;
        this.webClientPricing=webClientPricing;
    }

 */

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id)  {
        /**
         * TODO: Implemented.
         */
        Car car=this.repository.findById(id).orElseThrow(() -> new CarNotFoundException("car not found"));
        // https://www.youtube.com/watch?v=F3uJyeAyv5g -for learning webclients concepts
        // https://howtodoinjava.com/spring-webflux/webclient-get-post-example/

        /**
         * TODO: Implemented
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
/*       Price price=webClientPricing.get().uri("/services/price?vehicleId="+car.getId())
               .retrieve()
               .bodyToMono(Price.class).block(); */
        String price=priceClient.getPrice(id);

        /**
         * TODO: Implemented
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
    /*   Address address=webClientMaps.get().uri(uriBuilder -> uriBuilder
                .path("/maps")
                .queryParam("lat",car.getLocation().getLat())
                .queryParam("lon",car.getLocation().getLon())
                .build())
        .retrieve().bodyToMono(Address.class)
                .block(); */
        Location address=mapsClient.getAddress(car.getLocation());
    car.setPrice(price);
    car.setLocation(address);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setCreatedAt(car.getCreatedAt());
                        carToBeUpdated.setModifiedAt(car.getModifiedAt());
                        carToBeUpdated.setPrice(car.getPrice());

                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Implemented
         */
        Car car=this.repository.findById(id).orElseThrow(() -> new CarNotFoundException("car not found"));

       // Car car=this.findById(id).orElse;
        this.repository.deleteById(id);




    }
}
