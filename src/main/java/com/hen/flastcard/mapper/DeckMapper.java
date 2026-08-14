package com.hen.flastcard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.entity.Deck;

@Mapper(componentModel = "spring")
public interface DeckMapper {
    Deck toDeck(DeckRequest request);

    @Mapping(target = "id", source = "id")
    DeckResponse toDeckResponse(Deck deck);

    void updateDeck(@MappingTarget Deck deck, DeckRequest request);
}
