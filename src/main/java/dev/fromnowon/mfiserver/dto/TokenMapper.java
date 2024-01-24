package dev.fromnowon.mfiserver.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TokenMapper {

    TokenDataDTO1 testConvert(TokenDataDTO tokenDataDTO);

}