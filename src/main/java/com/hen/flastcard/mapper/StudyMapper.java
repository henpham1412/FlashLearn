package com.hen.flastcard.mapper;

import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.entity.FlashCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyMapper {
    @Mapping(target = "cardId", source = "id")
    StudyCardResponse toStudyCardResponse(FlashCard card);
}
