package com.hen.flastcard.mapper;

import com.hen.flastcard.dto.request.FlashCardRequest;
import com.hen.flastcard.dto.response.FlashCardResponse;
import com.hen.flastcard.entity.FlashCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FlashCardMapper {
    FlashCard toFlashCard(FlashCardRequest request);
    @Mapping(target = "id", source = "id")
    FlashCardResponse toFlashCardResponse(FlashCard flashCard);
    void updateFlashCard(@MappingTarget FlashCard flashCard, FlashCardRequest request);
}
