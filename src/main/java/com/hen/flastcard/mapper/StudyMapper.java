package com.hen.flastcard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.entity.FlashCard;

@Mapper(componentModel = "spring")
public interface StudyMapper {
    @Mapping(target = "cardId", source = "id")
    StudyCardResponse toStudyCardResponse(FlashCard card);
}
