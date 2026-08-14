package series ;
import java.sql.*;

/* *
* Clase para gestionar las conexiones .
*
* Cuando se construye se fijan los datos de las conexiones : user ,
* password y URL .
*
* - La base de datos se llama Series .
* - La conexion se establece con el localhost
* - En el puerto por defecto (3306)
*
* Cuando se le pide que ejecute tareas ( runTask ) crea una conexion
* y se la pasa a todas ellas , gestionando todas las excepciones
* que se puedan producir .
*/
public class ConnectionManager {
// Declara los atributos necesarios .
	
	private String host = "localhost:3306";
	private String db = "series";
	private String user;
	private String pass;
	private String url;
	
/* *
* Construye un gestor de conexion .
*
* @param user , el usuario a emplear en la conexion
* @param password , su password
*/
	
	
public ConnectionManager (String user, String password) {
	this.user = user;
	this.pass = password;
}
/* *
* Devuelve la URL que se emplea en la conexion .
*
* @return La url completa para conectar
*/
public String url() {
	this.url = "jdbc:mysql://" + this.host + "/" + this.db;
	return url;
 }
/* *
* Ejecuta una secuencia de tareas dada por tasks utilizando
* para cada una los datos en dataArray .
*
* 1) Abrir una conexion ( con la url , el usuario y el passwd ) ,
* 2) Se ejecutan todas las tareas cada una con su correspondiente
* String en dataArray
* 3) Es obligatorio cerrar la conexion
*
* Se deben capturar las excepciones de manera que :
*
* a ) Si se produce un error SQLException al hacer la conexion se
* retorna el String "SQL:" + e.getMessage ()
* b ) Si se produce una SeriesException al realizar la tarea
* se retorna el String : "Task:" + e.when() + "\ t" + e . getMessage ()
* c ) En cualquier otro caso se retorna el String " Otro :" + e . getMessage ()
*
* Las String que se retornan NO TIENEN ESPACIOS ADICIONALES .
*
* Este metodo no debe lanzar _ninguna_ Exception
*
* @param tasks , un array con la tareas a realizar
* @param dataArray , un array con los datos para las tareas
* @return " OK " si todo va bien , en caso contrario
* el mensaje correspondiente a la Exception capturada segun a ) b ) y c )
* indicadas mas arriba .
*/
public String runTask(DataBaseTask[] tasks, String[] dataArray) {
	try {
		Connection conn = DriverManager.getConnection(this.url, this.user, this.pass);
		for(int i = 0; i < tasks.length; i++) {
				tasks[i].run(conn, dataArray[i]);
		}
		conn.close();
	}
	catch(SQLException e) {
		System.err.print("SQL:" + e.getMessage());
	}
	catch(SeriesException e){
	System.err.print("Task:" + e.when() + "\t" + e.getMessage());
	}
	catch(Exception e) {
		System.err.print("Otro:" + e.getMessage());
	}
	return "OK";
 }
}