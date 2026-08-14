package series;
import java.sql.*;

public class CreateTableValora implements DataBaseTask {
	
	void run(Connection conn, String data) throws SeriesException{

		try (Statement st = conn.createStatement()){
			String sql = "CREATE TABLE valora(" + "Fecha DATE," + "valor INT," + "PRIMARY KEY (fecha, id_usuario, n_orden, n_temporada, id_serie)" + "FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario)"
							+ "FOREIGN KEY(id_serie) REFERENCES serie(id_serie)" + "FOREIGN KEY(n_orden, n_temporada, id_serie) REFERENCES capitulo(n_orden, n_temporada, n_Serie)" + "FOREIGN KEY(n_temporada) REFERENCES temporada(n_temporada));";
			int filas = st.executeUpdate(sql);
			System.out.println("Filas afectadas: " + filas);
		}
		catch (SQLException e){
			throw new SeriesException(e,"valora");
			System.err.println("Error: " + SeriesException.getMessage());
		}
		//Ya me hace el close try-catch
	}
}

// Foreign key (id_socio) reereces socio (ID_socio)
	//ON DELETE CASCADE ON UPODATE CASCADE,