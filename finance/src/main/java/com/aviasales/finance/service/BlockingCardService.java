package com.aviasales.finance.service;

import com.aviasales.finance.controller.PaymentController;
import com.aviasales.finance.dto.BlockCarDto;
import com.aviasales.finance.entity.BlockedCard;
import com.aviasales.finance.entity.BlockedCountry;
import com.aviasales.finance.exception.BlockedCardException;
import com.aviasales.finance.repository.BlockedCardsRepository;
import com.aviasales.finance.repository.BlockedCountryRepository;
import com.aviasales.finance.service.external.BinInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BlockingCardService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final BlockedCardsRepository blockedCardsRepository;
    private final BlockedCountryRepository blockedCountryRepository;
    private final BinInfoService binInfoService;

    @Autowired
    public BlockingCardService(BlockedCardsRepository blockedCardsRepository, BlockedCountryRepository blockedCountryRepository, BinInfoService binInfoService) {
        this.blockedCardsRepository = blockedCardsRepository;
        this.blockedCountryRepository = blockedCountryRepository;
        this.binInfoService = binInfoService;
    }

    public void blockCard(BlockCarDto blockCarDto) {
        if (isCardBlocked(blockCarDto.getCardNumber())) {
            throw new BlockedCardException("Card is already blocked");
        }

        BlockedCard blockedCard = new BlockedCard();
        blockedCard.setCardNumber(blockCarDto.getCardNumber());
        blockedCard.setBlockedDate(LocalDate.now());
        blockedCardsRepository.save(blockedCard);
    }

    public void unblockCard(BlockCarDto blockCarDto) {
        BlockedCard blockedCard = blockedCardsRepository.findByCardNumber(blockCarDto.getCardNumber())
                .orElseThrow(() -> new BlockedCardException("Such Card Number not blocked"));
        blockedCardsRepository.delete(blockedCard);
    }

    public boolean isCardBlocked(String cardNumber) {
        return blockedCardsRepository.findByCardNumber(cardNumber).isPresent();
    }

    public void blockCountry(BlockCarDto blockCarDto) {
        if (isCountryBlocked(blockCarDto.getCountryCode())) {
            throw new BlockedCardException("This country is already blocked");
        }

        BlockedCountry blockedCountry = new BlockedCountry();
        blockedCountry.setCountryCode(blockCarDto.getCountryCode());
        blockedCountry.setBlockedDate(LocalDate.now());
        blockedCountryRepository.save(blockedCountry);
    }

    public void unblockCountry(BlockCarDto blockCarDto) {
        BlockedCountry blockedCountry = blockedCountryRepository.findByCountryCode(blockCarDto.getCountryCode())
                .orElseThrow(() -> new BlockedCardException("Such country not blocked"));
        blockedCountryRepository.delete(blockedCountry);
    }

    public void validateCardDetails(String cardNumber) {
        logger.info("Checking card number not in the block list");
        if (isCardBlocked(cardNumber)) {
            logger.info("card number is blocked " + cardNumber);
            throw new BlockedCardException("Card number is blocked by server");
        }

        Optional<String> country = binInfoService.getCountryCodeByBinNumber(cardNumber);
        logger.info("Checking card country not in the block list");
        if (country.isEmpty()) {
            logger.warn("Error to look up bank card country, country check will be ignored");
        } else if (isCountryBlocked(country.get())) {
            throw new BlockedCardException("Card country is blocked by server - " + country.get());
        }
    }

    public boolean isCountryBlocked(String countryCode) {
        return blockedCountryRepository.findByCountryCode(countryCode).isPresent();
    }

    public List<BlockedCard> getBlockedCards() {
        return blockedCardsRepository.findAll();
    }

    public List<BlockedCountry> getBlockedCountries() {
        return blockedCountryRepository.findAll();
    }
}
