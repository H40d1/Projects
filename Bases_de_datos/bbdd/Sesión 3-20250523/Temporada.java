package series;
import java.sql.Date;

public class Temporada {
    private final int idSerie;
    private final int nTemporada;
    private final int nCapitulos;
    private final Date fechaEstreno;

    public Temporada(int idSerie, int nTemporada, int nCapitulos, Date fechaEstreno) {
        this.idSerie = idSerie;
        this.nTemporada = nTemporada;
        this.nCapitulos = nCapitulos;
        this.fechaEstreno = fechaEstreno;
    }

    public int getIdSerie() {
        return idSerie;
    }

    public int getNTemporada() {
        return nTemporada;
    }

    public int getNCapitulos() {
        return nCapitulos;
    }

    public Date getFechaEstreno() {
        return fechaEstreno;
    }

    @Override
    public String toString() {
        return "{ " + idSerie + ", " + nTemporada + ", " + nCapitulos + ", " + fechaEstreno + " }";
    }
}
