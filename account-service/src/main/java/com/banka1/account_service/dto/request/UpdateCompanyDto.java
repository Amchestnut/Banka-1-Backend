package com.banka1.account_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyDto {
    @NotBlank(message = "Naziv ne sme biti prazan")
    private String naziv;
    @NotBlank(message = "Sifra delatnosti ne sme biti prazna")
    private String sifraDelatnosti;
    private String adresa;
    private Long vlasnik;
}
