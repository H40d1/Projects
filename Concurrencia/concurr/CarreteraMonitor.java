package cc.carretera;

/*  Made with classmate
 *  Haodi, Lin Sun.
 */

import es.upm.babel.cclib.Monitor;
import es.upm.aedlib.map.*;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Implementación del recurso compartido Carretera con Monitores
 */
public class CarreteraMonitor implements Carretera {
    private int segmentos;
    private int carriles;

    private Monitor mutex;
    private Monitor.Cond mEntrar; //Condition queue para entrar. 

    private Map<String, CocheInfo> cochesMap; // Mapa de coches por id.
	private boolean[][] cocheOcup;	//Indica si esa pos esta ocupada. 
	
	private LinkedList<PetAvanzar> petAvaList; //Lista de peticiones aplazadas para Avanzar. 
	private LinkedList<PetCircular> petCirList; //Lista de peticiones aplazadas para Circulando. 
	
	// Clase para almacenar info de los coches.
    private static class CocheInfo {
    	private Pos pos;
    	private int tks;

        CocheInfo(Pos pos, int tks) {
            this.pos = pos;
            this.tks = tks;
        }
    }
    
    //Clase para peticiones de avanzar.
    class PetAvanzar {
    	public String cocheId;
    	public Monitor.Cond mon;
    	public CocheInfo coche;
    	
    	public PetAvanzar(Monitor m, String cocheId, CocheInfo coche) {
    		this.mon = m.newCond();
    		this.cocheId = cocheId;
    		this.coche = coche;
    	}
    }
    
    //Clase para peticiones de circulando. 
    class PetCircular {
    	public String cocheId;
    	public Monitor.Cond mon;
    	public CocheInfo coche;
    	
    	public PetCircular(Monitor m, String cocheId, CocheInfo coche) {
    		this.mon = m.newCond();
    		this.cocheId = cocheId;
    		this.coche = coche;
    	}
    }
    
    public CarreteraMonitor(int segmentos, int carriles) {
        this.segmentos = segmentos;
        this.carriles = carriles;

        mutex = new Monitor();
        mEntrar = mutex.newCond();

        cochesMap = new HashTableMap<>();
        cocheOcup = new boolean[segmentos][carriles];
        petAvaList = new LinkedList<>();
        petCirList = new LinkedList<>();
    }

    public Pos entrar(String id, int tks) {
        mutex.enter();
        
      //Espero si no hay carriles libre. 
        if (numCarrilesLibres(1) == 0) {
            mEntrar.await();
        }
        
        //Ocupo el primer carril libre. 
        int carril = carrilLibre(1);
        Pos pos = new Pos(1, carril);
        cochesMap.put(id, new CocheInfo(pos, tks));
        
        //Esa pos esta ocupada ahora. 
        cocheOcup[0][carril-1] = true;
        
        //Desbloqueo generico con false porque no he desbloqueado a ninguno. 
        desbGen(false);
        
        mutex.leave();
        return pos;
    }

    public Pos avanzar(String id, int tks) {
        mutex.enter();
        
        CocheInfo coche = cochesMap.get(id);
        int segm = coche.pos.getSegmento();
        
      //Bloqueo si no hay carriles libres en el siguiente semento. 
        if (numCarrilesLibres(segm+1) == 0) {
  		  PetAvanzar p = new PetAvanzar(mutex, id, coche);
  		  petAvaList.addLast(p);
  		  p.mon.await();
        }

        int carril = coche.pos.getCarril();
        int carrilNew = carril;
        // Si siguiente carril esta ocupado, ocupo el primero libre.
        if (cocheOcup[segm][carril-1]) {
        	carrilNew = carrilLibre(segm+1);
        }
        
        //Actualizo la posicion y el estado.  
        Pos nuevaPos = new Pos(segm+1, carrilNew);
        cocheOcup[segm-1][carril-1] = false;
  	  	cocheOcup[segm][carrilNew-1] = true;
        coche.pos = nuevaPos;
        coche.tks = tks;
        //No hace falta actualizar el map porque CocheInfo se pasa por referencia. 
        
        desbGen(false);
        
        mutex.leave();
        return nuevaPos;
    }

    public void circulando(String id) {
        mutex.enter();
        
        //Actualizo el estado a que circula. 
        CocheInfo coche = cochesMap.get(id);
        
        //Bloqueo si no ha llegado al final del segmento. 
        if (coche.tks > 0) {
            PetCircular pet = new PetCircular(mutex, id, coche);
            petCirList.addLast(pet);
            pet.mon.await();
        }
        //Se desbloqueo, su tks = 0. 
        //Desbloqueo generico con false porque no he desbloqueado a ninguno. 
	    desbGen(false);
        
        mutex.leave();
    }

    public void salir(String id) {
        mutex.enter();
        
        CocheInfo coche = cochesMap.get(id);
        Pos pos = coche.pos;
        int segm = pos.getSegmento();
        int carril = pos.getCarril();

  	  //Libero la pos que ocupaba y lo quito del map. 
        cocheOcup[segm - 1][carril - 1] = false;
        cochesMap.remove(id);

        //Desbloqueo generico con false porque no he desbloqueado a ninguno. 
        desbGen(false);
        
        mutex.leave();
    }

    public void tick() {
        mutex.enter();
        
        Iterator<String> it = cochesMap.keys().iterator();
        boolean senal = false;
        String id;
        CocheInfo coche;
       //Decremento tks a todos los coches. 
        while (it.hasNext()) {
            id = it.next();
            coche = cochesMap.get(id);
            
            if (coche.tks > 0) {
            	// Decremento tks y lo actualizo. 
                coche.tks--;
                
                //Si al actualizar el tks es 0, desbloqueo. 
                if(!senal && coche.tks == 0) {
                	senal = desbCircular(true, id);
                }
            }
        }
        
        //Desbloqueo generico, senal como param por si no se ha desbloqueado ningun proceso. 
        desbGen(senal);
        
        mutex.leave();
    }

    //Devuelve el primer carril libre del segmento segm. 
    private int carrilLibre(int segm) {
    	//Si el segmento no esta en el rango, devuelvo -1.
    	if(segm > segmentos) {
    		return -1;
    	}
  	  int i = 0;
  	  while(i < carriles && cocheOcup[segm-1][i]) {
  		  i++;
  	  }
  	  //Si es el mismo num de carriles, como empezaba desde 0, se sale. 
  	  return (i == carriles)?-1:i+1;
    }
    
  //Devuelve el numero de carriles libres del segmento segm. 
    private int numCarrilesLibres(int segm) {
    	//Si el segmento no esta en el rango, no hay carril libre.
    	if(segm > segmentos) {
    		return 0;
    	}
    	
  	  int libres = 0;
  	  for(int i = 0; i < carriles; i++) {
  		  if(!cocheOcup[segm-1][i]) {
  			  libres++;
  		  }
  	  } 
  	  return libres;
    }
    
    //Desbloqueo del primer coche que pueda avanzar, devuelvo true si se desbloquea alguno. 
    private boolean desbAvanzar() {
    	Iterator<PetAvanzar> itAv = petAvaList.iterator();
    	boolean senal = false;
    	PetAvanzar p = null;
    	CocheInfo c;
    	int cSegm;
    	while(!senal && itAv.hasNext()) {
    		p = itAv.next();
    		c = cochesMap.get(p.cocheId);
    		cSegm = c.pos.getSegmento();
    		//Si hay carriles libres en el sig segmento, puede avanzar. 
    		if(numCarrilesLibres(cSegm+1) > 0) {
    			itAv.remove();
    			senal = true;
    		}
    	}
    	if(senal) {
			p.mon.signal();
    	}
    	return senal;
    }
    
    //Desbloqueo de circulando por id o no, devuelve true si se desbloqueo alguno. 
    private boolean desbCircular(boolean porId, String id) {
    	Iterator<PetCircular> it = petCirList.iterator();
    	boolean senal = false;
    	PetCircular p = null;
    	CocheInfo c;
    	while(it.hasNext() && !senal) {
    		p = it.next();
    		c = cochesMap.get(p.cocheId);
    		//true si coincide. 
    		if((porId && id.equals(p.cocheId)) || (!porId && c.tks == 0)) {
    			senal = true;
    			it.remove();
    		}
    	}
    	//Signal si encontre al indicado. 
    	if(senal) {
    		p.mon.signal();
    	}
    	//Si no, devuelvo false. 
    	return senal;
    }
    
    //Desbloqueo generico. 
    private void desbGen(boolean senal) {
        boolean sen = senal; 
        //Desbloqueo si hay coches pendientes con tks a 0. 
        if(!sen && petCirList.size() > 0) {
        	sen = desbCircular(false, "");
        }
        //Desbloqueo si hay coches que quieren avanzar. 
        if(!sen && petAvaList.size() > 0) {
        	sen = desbAvanzar();
        }
        //Desbloqueo si hay coches que quieren entrar. 
        if(!sen && mEntrar.waiting() > 0 && numCarrilesLibres(1) > 0) {
        	mEntrar.signal();
        }
    }
}