package onlinespielepartner.data;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class OnlineSpieleTreffen extends AbstractEntity {

    private String titel;
    private String beschreibung;
    private LocalDateTime datum;
    private String link;

    public String getTitel() {
        return titel;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }
    public String getBeschreibung() {
        return beschreibung;
    }
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
    public LocalDateTime getDatum() {
        return datum;
    }
    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

}
