package uk.gov.justice.laa.crime.hardship.mapper;

public interface ResponseMapper<T, D> {

    void toDto(final T model, final D dto);
}
