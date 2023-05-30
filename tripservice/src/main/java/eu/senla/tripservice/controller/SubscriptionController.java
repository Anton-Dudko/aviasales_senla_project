package eu.senla.tripservice.controller;

import eu.senla.tripservice.entity.Subscription;
import eu.senla.tripservice.exeption.subscription.SubscriptionException;
import eu.senla.tripservice.request.CanceledFlightSubscriptionRequest;
import eu.senla.tripservice.request.NewFlightsSubscriptionRequest;
import eu.senla.tripservice.service.SubscriptionService;
import eu.senla.tripservice.util.error.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create-new-flight")
    public ResponseEntity<Subscription> createNewFlightSubscription(@RequestBody @Valid NewFlightsSubscriptionRequest request,
                                                                    BindingResult bindingResult,
                                                                    @RequestHeader("userDetails") String userDetails) {
        validate(bindingResult);
        if (userDetails != null && !userDetails.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.createNewFlightSubscription(request,
                    userDetails));
        }
        throw new SubscriptionException("Header userDetails not found");
    }

    @PostMapping("/create-flight-canceled")
    public ResponseEntity<Subscription> createFlightCanceledSubscription(@RequestBody @Valid CanceledFlightSubscriptionRequest request,
                                                                         BindingResult bindingResult,
                                                                         @RequestHeader("userDetails") String userDetails) {
        validate(bindingResult);
        if (userDetails != null && !userDetails.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.createCanceledFlightSubscription(request,
                    userDetails));
        }
        throw new SubscriptionException("Header userDetails not found");
    }

    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new SubscriptionException(errorMessage);
        }
    }
}
