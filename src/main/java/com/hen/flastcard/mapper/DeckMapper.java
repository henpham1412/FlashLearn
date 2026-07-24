package com.hen.flastcard.mapper;

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.request.UserUpdationRequest;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.entity.Deck;
import com.hen.flastcard.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeckMapper {
    Deck toDeck(DeckRequest request);
    @Mapping(target = "id", source = "id")
    DeckResponse toDeckResponse(Deck deck);
    void updateDeck(@MappingTarget Deck deck, DeckRequest request);
}
