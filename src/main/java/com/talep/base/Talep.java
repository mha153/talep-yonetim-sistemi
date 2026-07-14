package com.talep.base;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MUSTAFA_REQUESTS")
public class Talep {

    @Id
    @GeneratedValue
    private Long id;
    private String musteri;
    private String baslik;
    private String aciklama;
    private String durum;
    
    private Integer aciliyet;
    private Integer etki;
    private Integer skor;
    
    // PO için eklendi
    private String sprint; 

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMusteri() { return musteri; }
    public void setMusteri(String musteri) { this.musteri = musteri; }
    
    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) { this.baslik = baslik; }
    
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
    
    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }
    
    public Integer getAciliyet() { return aciliyet; }
    public void setAciliyet(Integer aciliyet) { this.aciliyet = aciliyet; }
    
    public Integer getEtki() { return etki; }
    public void setEtki(Integer etki) { this.etki = etki; }
    
    public Integer getSkor() { return skor; }
    public void setSkor(Integer skor) { this.skor = skor; }
    
    public String getSprint() { return sprint; }
    public void setSprint(String sprint) { this.sprint = sprint; }
}