package uk.gov.justice.laa.crime.hardship.mapper;

public interface RequestMapper<T, D> {

    T fromDto(final D dto);
}
