package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.UserDTO;
import com.exam.exam_web.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO dto);
}