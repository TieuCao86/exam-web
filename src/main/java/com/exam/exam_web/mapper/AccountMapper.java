package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.AccountDTO;
import com.exam.exam_web.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDTO(Account account);

    Account toEntity(AccountDTO dto);
}