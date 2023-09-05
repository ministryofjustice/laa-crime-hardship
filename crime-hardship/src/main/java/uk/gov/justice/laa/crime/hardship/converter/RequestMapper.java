package uk.gov.justice.laa.crime.hardship.converter;

public interface RequestMapper<T, D> {

    T fromDto(final D dto);
}
