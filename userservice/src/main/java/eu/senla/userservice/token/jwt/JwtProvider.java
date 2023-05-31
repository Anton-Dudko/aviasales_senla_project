package eu.senla.userservice.token.jwt;


import eu.senla.userservice.entity.User;

public interface JwtProvider {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateAccessToken(String token);

    String getLoginFromAccessToken(String token);

    boolean validateRefreshToken(String accessToken);

    String getLoginFromRefreshToken(String token);
}
