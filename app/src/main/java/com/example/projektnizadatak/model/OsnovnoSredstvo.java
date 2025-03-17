package com.example.projektnizadatak.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "OsnovnoSredstvo",
        foreignKeys = {
                @ForeignKey(entity = Osoba.class,
                        parentColumns="id",
                        childColumns = "from_osoba_id"),
                @ForeignKey(entity = Lokacija.class,
                        parentColumns = "id",
                        childColumns = "from_lokacija_id"
                ),
                @ForeignKey(entity = Lista.class,
                        parentColumns = "id",
                        childColumns = "lista_id"
                )


        })
public class OsnovnoSredstvo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public String naziv;
    public String opis;
    public String barkod;
    public double cijena;
    public Date datum;
    public int from_osoba_id;
    public int to_osoba_id;
    public int from_lokacija_id;

    public int to_lokacija_id;

    public byte[] slika;

    public  Integer lista_id;


    public OsnovnoSredstvo() {
    }

    public OsnovnoSredstvo(int id, String naziv, String opis, String barkod, double cijena, Date datum, int from_osoba_id, int to_osoba_id, int from_lokacija_id, int to_lokacija_id, byte[] slika, Integer lista_id) {
        this.id = id;
        this.naziv = naziv;
        this.opis = opis;
        this.barkod = barkod;
        this.cijena = cijena;
        this.datum = datum;
        this.from_osoba_id = from_osoba_id;
        this.to_osoba_id = to_osoba_id;
        this.from_lokacija_id = from_lokacija_id;
        this.to_lokacija_id = to_lokacija_id;
        this.slika = slika;
        this.lista_id = lista_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarkod() {
        return barkod;
    }

    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public byte[] getSlika() {
        return slika;
    }

    public void setSlika(byte[] slika) {
        this.slika = slika;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getNaizv() {
        return naziv;
    }

    public void setNaizv(String naizv) {
        this.naziv = naizv;
    }

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public int getFrom_osoba_id() {
        return from_osoba_id;
    }

    public void setFrom_osoba_id(int from_osoba_id) {
        this.from_osoba_id = from_osoba_id;
    }

    public int getTo_osoba_id() {
        return to_osoba_id;
    }

    public void setTo_osoba_id(int to_osoba_id) {
        this.to_osoba_id = to_osoba_id;
    }

    public int getFrom_lokacija_id() {
        return from_lokacija_id;
    }

    public void setFrom_lokacija_id(int from_lokacija_id) {
        this.from_lokacija_id = from_lokacija_id;
    }

    public int getTo_lokacija_id() {
        return to_lokacija_id;
    }

    public void setTo_lokacija_id(int to_lokacija_id) {
        this.to_lokacija_id = to_lokacija_id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Integer getLista_id() {
        return lista_id;
    }

    public void setLista_id(Integer lista_id) {
        this.lista_id = lista_id;
    }
}
