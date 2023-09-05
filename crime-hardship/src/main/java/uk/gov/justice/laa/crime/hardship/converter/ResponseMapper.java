package uk.gov.justice.laa.crime.hardship.converter;

public interface ResponseMapper<T, D> {

    void toDto(final T model, final D dto);
}
