package series;

import java.util.ArrayList;

/**
 * Esta clase incorpora una forma de devolver un resultado
 * cuando una DataBaseTask consiste en un SELECT.
 *
 * 1) Se crea un ArrayList para guardar el resultado.
 * 2) Al ejecutar la consulta el ResultSet se recorre y se genera
 *    un objeto de tipo `E` por cada fila
 * 3) Ese objeto se coloca en el ArrayList `resultado`
 * 4) Posteriormente se puede acceder al resultado mediante `get`
 */
public abstract class ConsultaConResultado<E> implements DataBaseTask {
    protected ArrayList<E> resultado;

    public ArrayList<E> get() {
        return resultado;
    }
}
