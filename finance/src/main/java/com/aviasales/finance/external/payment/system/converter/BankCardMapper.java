package com.aviasales.finance.external.payment.system.converter;

import com.aviasales.finance.external.payment.system.dto.BankCardDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankCardMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public BankCardMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BankCard convertToEntity(BankCardDto bankCardDto) {
        BankCard bankCard = new BankCard();
        modelMapper.map(bankCardDto, bankCard);
        return bankCard;
    }

}
