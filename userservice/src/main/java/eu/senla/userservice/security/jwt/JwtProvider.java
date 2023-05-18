package eu.senla.userservice.security.jwt;


import eu.senla.userservice.entity.User;

public interface JwtProvider {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    String getLoginFromAccessToken(String token);

    String getLoginFromRefreshToken(String token);
}
