package cc.carretera;

import org.jcsp.lang.*;
import es.upm.aedlib.map.*;
import java.util.LinkedList;
import java.util.Iterator;

public class CarreteraCSP implements Carretera, CSProcess {
  // Declaración de canales
	private Any2OneChannel cEntrar, cAvanzar, cSalir, cCirculando, cTick;

  // Configuración de la carretera
  private final int segmentos;
  private final int carriles;

  public CarreteraCSP(int segmentos, int carriles) {
    this.segmentos = segmentos;
    this.carriles = carriles;
    
    cEntrar = Channel.any2one();
    cAvanzar = Channel.any2one();
    cSalir = Channel.any2one();
    cCirculando = Channel.any2one();
    cTick = Channel.any2one();
    
    // Puesta en marcha del servidor: alternativa sucia (desde el
    // punto de vista de CSP) a Parallel que nos ofrece JCSP para
    // poner en marcha un CSProcess
    new ProcessManager(this).start();
  }

  private static class CocheInfo {
  	private Pos pos;
  	private int tks;

      CocheInfo(Pos pos, int tks) {
          this.pos = pos;
          this.tks = tks;
      }
  }
  
  class LlamadaGen{
	  public int numAcc; //Que se va a hacer. 
	  public String car; 
	  public int tks;
	  public ChannelOutput chRes;
	  
	  //Para enter, avanzar. 
	  public LlamadaGen(int numAcc, String car, int tks, ChannelOutput chRes) {
		  this.numAcc = numAcc;
		  this.car = car;
		  this.tks = tks;
		  this.chRes = chRes;
	  }
	  
	  //Para circulando, salir.
	  public LlamadaGen(int numAcc, String car, ChannelOutput chRes) {
		  this.numAcc = numAcc;
		  this.car = car;
		  this.chRes = chRes;
	  }
	  
	  //Para tick. 
	  public LlamadaGen(int numAcc, ChannelOutput chRes) {
		  this.numAcc = numAcc;
		  this.chRes = chRes;
	  }
	  
  }
  
  class LlamadaEnt{
	  public String car;
	  public int tks;
	  public ChannelOutput cRes;
	  
	  public LlamadaEnt(String car, int tks, ChannelOutput cRes) {
		  this.car = car;
		  this.tks = tks;
		  this.cRes = cRes;
	  }
  }
  
  class LlamadaAva{
	  public String car;
	  public int tks;
	  public ChannelOutput cRes;
	  
	  public LlamadaAva(String car, int tks, ChannelOutput cRes) {
		  this.car = car;
		  this.tks = tks;
		  this.cRes = cRes;
	  }
  }
  
  class LlamadaCir {
	  public String car;
	  public ChannelOutput ackCir;
	  
	  public LlamadaCir(String car, ChannelOutput ackCir) {
		  this.car = car;
		  this.ackCir = ackCir;
	  }
  }
  
  public Pos entrar(String car, int tks) {
	  One2OneChannel petCEnt = Channel.one2one();
	  LlamadaEnt petEnt = new LlamadaEnt(car, tks, petCEnt.out());
	  cEntrar.out().write(petEnt);
	  Pos pos = (Pos)petCEnt.in().read();
    return pos;
  }
 
  public Pos avanzar(String car, int tks) {
	  One2OneChannel petCAva = Channel.one2one();
	  LlamadaAva petAva = new LlamadaAva(car, tks, petCAva.out());
	  cAvanzar.out().write(petAva);
	  Pos pos = (Pos)petCAva.in().read();
    return pos;
  }

  public void salir(String car) {
	  cSalir.out().write(car);
  }

  //Envia peticion y se queda bloqueado esperando una respuesta. 
  public void circulando(String car) {
	  //Envio un mensaje con ack para cuando el tks del coche llegue a 0. 
	  One2OneChannel ackCir = Channel.one2one();
	  LlamadaCir llaCir = new LlamadaCir(car, ackCir.out());
	  cCirculando.out().write(llaCir);
	  //Hasta que tks no llegue a 0, no sale. 
	  ackCir.in().read();
  }

  //Envia una peticion para bajar tks, no recibe ack. 
  public void tick() {
	  cTick.out().write("");
  }
  
  // Código del servidor
  public void run() {
    //declaración e inicialización del estado del recurso
	  Map<String, CocheInfo> cochesMap = new HashTableMap<>(); //Dado el id, devuelve un array con un objeto CocheInfo. 
	  boolean[][] carretera = new boolean[segmentos][carriles]; //Posiciones que estan ocupadas a true. 

    //declaración e inicialización de estructuras de datos para almacenar peticiones de los clientes.
      LinkedList<LlamadaAva> petLAva = new LinkedList<>();
      LinkedList<LlamadaCir> petLCir = new LinkedList<>();
      LinkedList<LlamadaEnt> petLEnt = new LinkedList<>();
      
      boolean[] sincCond = new boolean[5];
      final int ENTRAR = 0, AVANZAR = 1, SALIR = 2, CIRCULANDO = 3, TICK = 4;

    //declaración e inicialización de arrays necesarios para poder hacer la recepción no determinista (Alternative)
      AltingChannelInput[] entradas = new AltingChannelInput[] {cEntrar.in(), cAvanzar.in(), cSalir.in(), 
    		  													cCirculando.in(), cTick.in()};
      Alternative servicios = new Alternative(entradas);

    // Bucle principal del servicio	
    while(true){
      //declaración de variables auxiliares
      int servicio;

      //cálculo de las guardas
      //Condicion de enter. 
      sincCond[ENTRAR] = true;
      //Condicion de avanzar. 
      sincCond[AVANZAR] = true;
      //Condicion de salir.
      sincCond[SALIR] = true;
      //Condicion de circulando. 
      sincCond[CIRCULANDO] = true;
      //Condicion de tick. No hay. 
      sincCond[TICK] = true;
      
      // cambiar null por el array de guardas
      servicio = servicios.fairSelect(sincCond);

      // ejecutar la operación solicitada por el cliente
      switch (servicio){
      //Entrar
      case ENTRAR:
        // ejecutar operación 0 o almacenar la petición y responder al cliente si es posible
    	  
    	  //Almaceno la peticion y lo respondo cuando sea posible. 
    	  LlamadaEnt llamaEnt = (LlamadaEnt)cEntrar.in().read();
    	  if(numCarriLibres(carretera, 1) > 0) {
    		  ejecEntrar(cochesMap, carretera, llamaEnt);
    	  }
    	  else {
    		  petLEnt.addLast(llamaEnt);
    	  }
    	  
    	  //ejecEntrar(cochesMap, carretera, llamadaEnt);
    
    	  break;
    	  
      case AVANZAR:
    	  
    	  LlamadaAva llamaAva = (LlamadaAva)cAvanzar.in().read();
    	  CocheInfo coAva = cochesMap.get(llamaAva.car);
    	  int segmAva = coAva.pos.getSegmento();
    	  //Compruebo si puedo atenderlo
    	  if(numCarriLibres(carretera, segmAva+1) > 0) {
    		  ejecAvanzar(cochesMap, carretera, llamaAva);
    	  }
    	  else {
    		  //Si no, la guardo y lo miro al salir. 
    		  petLAva.addLast(llamaAva);
    	  }
    	  break;
    	  
      case SALIR:

    	  //Extraigo peticion. 
    	  String idSal = (String)cSalir.in().read();
    	  CocheInfo cocheSal = cochesMap.get(idSal);
    	  
    	  //Saco el coche de la carretera.
    	  Pos posCSal = cocheSal.pos;
    	  int segmSal = posCSal.getSegmento();
    	  int carrSal = posCSal.getCarril();
    	  cochesMap.remove(idSal);
    	  carretera[segmSal-1][carrSal-1] = false;

    	  break;
    	  
      case CIRCULANDO:
    	  //Leo peticion y la guardo. 
    	  LlamadaCir llamaCir = (LlamadaCir)cCirculando.in().read();
    	  CocheInfo cCir = cochesMap.get(llamaCir.car);
    	  if(cCir.tks == 0) {
    		  llamaCir.ackCir.write(null);
    	  }
    	  else {
        	  petLCir.addLast(llamaCir);
    	  }
    	  break;
    	  
      case TICK:
    	  
    	  cTick.in().read();
    	  Iterator<String> itTik = cochesMap.keys().iterator(); 
          String idTik; 
          CocheInfo cocheTik; 
         //Decremento tks a todos los coches.  
          while (itTik.hasNext()) { 
              idTik = itTik.next(); 
              cocheTik = cochesMap.get(idTik); 
              if (cocheTik.tks > 0) { 
              	// Decremento tks y lo actualizo. 
            	  cocheTik.tks--;
              }
          }
    	  break;
      }
      
      boolean depende = true;
      
      while(depende) {
    	  
    	  depende = false;
	      //Miro si hay peticiones de circular. 
	      if(!petLCir.isEmpty()) {
	    	  Iterator<LlamadaCir> itCir = petLCir.iterator();
	    	  LlamadaCir llamaCir;
	    	  String idCir;
	    	  CocheInfo coCir;
	    	  ChannelOutput chOutCir;
	    	  while(itCir.hasNext()) {
	    		  llamaCir = itCir.next();
	    		  idCir = llamaCir.car;
	    		  coCir = cochesMap.get(idCir);
	    		  if(coCir.tks == 0) {
	    			  //Si tks es 0, respondo y elimino. 
	    			  chOutCir = llamaCir.ackCir;
	    			  chOutCir.write("");
	    			  itCir.remove();
	    			  depende = true;
	    		  }
	    	  }
	      }
	      
	      //Miro si hay peticiones para avanzar. 
	      if(!petLAva.isEmpty()) {
	    	  Iterator<LlamadaAva> itAva = petLAva.iterator();
	    	  LlamadaAva llamaAva;
	    	  String idAva;
	    	  Pos posAva;
	    	  int segmAva;
	    	  while(itAva.hasNext()) {
	    		  llamaAva = itAva.next();
	    		  idAva = llamaAva.car;
	    		  posAva = cochesMap.get(idAva).pos;
	    		  segmAva = posAva.getSegmento();
	    		  
	    		  //Si hay carriles libres, avanzo al primero libre. 
	    		  if(numCarriLibres(carretera, segmAva+1) > 0) {
	    			  ejecAvanzar(cochesMap, carretera, llamaAva);
	    			  itAva.remove();
	    			  depende = true;
	    		  }
	    	  }
	      }
	      
	      //Compruebo si hay peticiones de entrar. 
	      if(!petLEnt.isEmpty() && numCarriLibres(carretera, 1) > 0) {
	    	  Iterator<LlamadaEnt> itEnt = petLEnt.iterator();
	    	  LlamadaEnt datEnt;
	    	  while(itEnt.hasNext() && numCarriLibres(carretera, 1) > 0) {
	    		  datEnt = itEnt.next();
	    		  datEnt = petLEnt.getFirst();
	    		  ejecEntrar(cochesMap, carretera, datEnt);
	    		  itEnt.remove();
    			  depende = true;
	    	  }
	      }
      }
    }
  }

  //Devuelve el primer carril libre del segm. 
  private int numCarriLibres(boolean[][] carretera, int segm) {
	//Si el segmento no esta en el rango, no hay carril libre.
  	if(segm > segmentos) {
  		return 0;
  	}
  	//Calculo de carriles libres. 
	  int libres = 0;
	  for(int i = 0; i < carriles; i++) {
		  if(!carretera[segm-1][i]) {
			  libres++;
		  }
	  } 
	  return libres;
  }
  
  //Devuelve el primer carril libre. 
  private int carrilLibre(boolean[][] carre, int segm) {
  	//Si el segmento no esta en el rango, devuelvo -1.
  	if(segm > segmentos) {
  		return -1;
  	}
  	//Calculo del carril libre. 
	  int i = 0;
	  while(i < carriles && carre[segm-1][i]) {
		  i++;
	  }
	  //Si es el mismo num de carriles, como empezaba desde 0, se sale. 
	  return (i == carriles)?-1:i+1;
  }
  
  private void ejecEntrar(Map<String, CocheInfo> cochesMap, boolean[][] carretera, LlamadaEnt llamaEnt) {
	  String idEnt = llamaEnt.car;
	  int tksEnt = llamaEnt.tks;
	  ChannelOutput chOutEnt = llamaEnt.cRes;
	  
	  //Calculo carril al que entra. 
	  int carrilEnt = carrilLibre(carretera, 1);
	  Pos posEnt = new Pos(1, carrilEnt);
	  
	  //Anado coche a la carretera. 
	  cochesMap.put(idEnt, new CocheInfo(posEnt, tksEnt));
	  carretera[0][carrilEnt-1] = true;
	  
	  //Respondo la peticion. 
	  chOutEnt.write(posEnt);
  }
  
  private void ejecAvanzar(Map<String, CocheInfo> cochesMap, boolean[][] carretera, LlamadaAva llamaAva) {
	  String idAva = llamaAva.car;
	  int tksAva = llamaAva.tks;
	  ChannelOutput chOutAva = llamaAva.cRes;
	  CocheInfo cocheAva = cochesMap.get(idAva);
	  Pos posAva = cocheAva.pos;
	  int segmAva = posAva.getSegmento();
	  int carrAva = posAva.getCarril();
	  
	  //Calculo carril al que avanza.
	  int carrAvaNew = carrilLibre(carretera, segmAva+1);
	  Pos posAvaNew = new Pos(segmAva+1, carrAvaNew);
	  
	  //Actualizo pos en carretera. 
	  carretera[segmAva-1][carrAva-1] = false; 
	  carretera[segmAva][carrAvaNew-1] = true;
	  cochesMap.put(idAva, new CocheInfo(posAvaNew, tksAva));
	  
	  //Devuelvo la nueva Pos.
	  chOutAva.write(posAvaNew);
  } 
}
