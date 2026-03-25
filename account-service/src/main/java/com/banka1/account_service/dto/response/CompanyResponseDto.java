package com.banka1.account_service.dto.response;

import com.banka1.account_service.domain.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyResponseDto {
    private Long id;
    private String naziv;
    private String maticniBroj;
    private String poreskiBroj;
    private String sifraDelatnosti;
    private String adresa;
    private Long vlasnik;

    public CompanyResponseDto(Company company) {
        this.id = company.getId();
        this.naziv = company.getNaziv();
        this.maticniBroj = company.getMaticni_broj();
        this.poreskiBroj = company.getPoreski_broj();
        this.sifraDelatnosti = company.getSifraDelatnosti() != null ? company.getSifraDelatnosti().getSifra() : null;
        this.adresa = company.getAdresa();
        this.vlasnik = company.getVlasnik();
    }
}
