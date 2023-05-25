package eu.senla.aviasales.model.constant;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface Topic {
    String USER_REGISTERED_EVENT = "user_registered_event";
    String USER_RESET_PASSWORD_EVENT = "user_reset_password_event";
    String NEW_TRIP_EVENT = "new_trip_event";
    String TRIP_CANCELED_EVENT = "trip_canceled_event";
    String NEW_TICKET_RESERVATION_EVENT = "new_ticket_reservation_event";
    String CANCELED_TICKET_RESERVATION_EVENT = "canceled_ticket_reservation_event";
    String PAYMENT_SUCCESS_EVENT = "payment_success_event";
    String PAYMENT_ERROR_EVENT = "payment_error_event";

}
