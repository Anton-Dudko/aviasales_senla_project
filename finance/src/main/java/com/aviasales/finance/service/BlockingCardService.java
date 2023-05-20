package com.aviasales.finance.service;

import com.aviasales.finance.dto.BlockCarDto;
import com.aviasales.finance.entity.BlockedCard;
import com.aviasales.finance.entity.BlockedCountry;
import com.aviasales.finance.repository.BlockedCardsRepository;
import com.aviasales.finance.repository.BlockedCountryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BlockingCardService {

    private final BlockedCardsRepository blockedCardsRepository;
    private final BlockedCountryRepository blockedCountryRepository;

    public BlockingCardService(BlockedCardsRepository blockedCardsRepository, BlockedCountryRepository blockedCountryRepository) {
        this.blockedCardsRepository = blockedCardsRepository;
        this.blockedCountryRepository = blockedCountryRepository;
    }

    public void blockCard(BlockCarDto blockCarDto) {
        BlockedCard blockedCard = new BlockedCard();
        blockedCard.setCardNumber(blockCarDto.getCardNumber());
        blockedCard.setBlockedDate(LocalDate.now());
        blockedCardsRepository.save(blockedCard);
    }

    public void unblockCard(BlockCarDto blockCarDto) {
        //ToDo add exception in case card not found
        BlockedCard blockedCard = blockedCardsRepository.findByCardNumber(blockCarDto.getCardNumber())
                .orElseThrow();
        blockedCardsRepository.delete(blockedCard);
    }

    public boolean isCardBlocked(String cardNumber) {
        return blockedCardsRepository.findByCardNumber(cardNumber).isPresent();
    }

    public void blockCountry(BlockCarDto blockCarDto) {
        BlockedCountry blockedCountry = new BlockedCountry();
        blockedCountry.setCountryCode(blockCarDto.getCountryCode());
        blockedCountry.setBlockedDate(LocalDate.now());
        blockedCountryRepository.save(blockedCountry);
    }

    public void unblockCountry(BlockCarDto blockCarDto) {
        //ToDo add exception in case card not found
        BlockedCountry blockedCountry = blockedCountryRepository.findByCountryCode(blockCarDto.getCountryCode())
                .orElseThrow();
        blockedCountryRepository.delete(blockedCountry);
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
