package eu.senla.aviasales.service.validator;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface ValidatorService<T> {
    void validate(T t) throws Exception;
}
