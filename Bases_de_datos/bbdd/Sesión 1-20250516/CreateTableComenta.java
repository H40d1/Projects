package series ;
import java.sql.*;
/* *
* CreateTableComenta es una tarea que permite crear la tabla
* ` Comenta ` de acuerdo con el diagrama entidad - relacion proporcionado .
*
* Se recuerda que `run `:
* a ) Solamente puede lanzar SeriesException ( como se indica en DataBaseTask ).
* b ) NO cierra la conexion
* c ) Si debe cerrar el resto de recursos
* d ) En este caso el param data no es necesario y se puede ignorar .
* e ) Se puede suponer que las tablas necesarias al crear esta ya
* existen .
*/
public class CreateTableComenta implements DataBaseTask {
/* Escribe toda la clase */
	public void run(Connection conn, String data) throws SeriesException{
		try(Statement st = conn.createStatement()){
			String sql = "CREATE TABLE comenta (" +
						 " ID_Usuario INT NOT NULL, " + 
					     " N_Temporada INT NOT NULL" + 
						 " ID_Serie INT NOT NULL" +
		                 " Fecha DATE NOT NULL, " + 
		                 " Texto VARCHAR(500), " +
		                 " PRIMARY KEY (ID_Usuario, N_Temporada, ID_Serie, Fecha), " + 
		                 " FOREIGN KEY (ID_Usuario) references Usuario (ID_Usuario), " +
		                 " FOREIGN KEY (N_Temporada) references Temporada (N_Temporada), " + 
		                 " FOREIGN KEY (ID_Serie) references Serie (ID_Serie)), ";
			st.execute(sql);
			st.close();
		}
		catch(SQLException e) {
			throw new SeriesException(e,"when");
		}
	}
}