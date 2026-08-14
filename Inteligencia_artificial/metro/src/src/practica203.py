from tkinter import *
from PIL import ImageTk, Image
import math
from math import *
import time

class Parada:
	def __init__(self, id, coordX, coordY,realX, realY, nom, linea, transbordo, conexiones=None):
		self.id = id 
		self.coordX = coordX
		self.coordY = coordY
		self.realX = realX
		self.realY = realY
		self.nom = nom
		self.linea = linea
		self.transbordo = transbordo #0 = No; 1 = Sí
		self.conexiones = conexiones or {}



root = Tk() #Activo entorno de trabajo
root.title("Functioning")
root.iconbitmap('res/icon1.ico')
root.configure(bg="black")
root.geometry("+0+5") #Fijar ventana en posición en específico
root.resizable(False, False) #No quiero que se me modifique la ventana
#boton de ayuda---VM
ayuda = None #Para ver si la ventana de ayuda realmente está 

def salir_Ayuda(): #Para que funcione el botón de cerrar
	global ayuda

	if ayuda != None:
		ayuda.destroy()
		ayuda = None


def mostrar_ayuda():
	global  ayuda

	salir_Ayuda()

	ayuda = Toplevel(root)
	ayuda.title("Ayuda")
	ayuda.configure(bg="black")
	ayuda.resizable(False, False)
	ayuda.geometry("+0+1")
	boton_salir = Button(ayuda, text = "Cerrar", fg = "red", command = salir_Ayuda)

	texto_ayuda = (
		"1. Introduce el nombre de la estación de origen en el primer campo de texto.\n"
		"2. Introduce el nombre de la estación de destino en el segundo campo de texto.\n"
		"3. Haz clic en el botón 'Calcular ruta A*' para iniciar el cálculo de la ruta óptima. O bien presionar la tecla ""Enter"" del teclado.\n"
		"4. La ruta óptima se mostrará en una nueva ventana junto con el coste total y la heurística inicial.\n\n"
		"Nota: A la hora de escribir los nombres de las estaciones, asegúrate de escribirlos con las mayúsuculas, minúsuculas, puntos y espacios como se ve reflejado en el mapa para evitar errores.\n"
    )
	label_tit = Label(ayuda, text = "Instrucciones de uso:", font = ("Times New Roman", 19, "bold", "underline"), bg = "black", fg = "white", justify = LEFT)
	label_tit.pack(padx = (20, 0), pady = (20, 10), anchor = "w") # Primero meter titulo

	label_cont = Label(
        ayuda,
        text=texto_ayuda,
        bg="black",
        fg="white",
        justify=LEFT,
        wraplength=450,
        font=("Times New Roman", 11)
    )
	label_cont.pack(padx=20, pady = (0, 0), anchor = "w") #Se inserta párrafo 1

	label_tit2 = Label(ayuda, text = "Otra manera de uso:", font = ("Times New Roman",13, "bold"), bg = "black", fg = "white", justify = LEFT)
	label_tit2.pack(padx = (20, 0), pady = (5, 0), anchor = "w") #Meteos título 2

	texto_ayuda2 = ("1. Haz clic en el mapa para seleccionar la estación de origen y destino.\n"
		"2. El primer clic establecerá la estación de origen y el segundo clic la estación de destino.\n"
		"3. Si deseas cambiar la estación de origen o destino, simplemente haz clic nuevamente en el mapa restableciendose los dos valores.\n"
		)
	label_cont2 = Label(
        ayuda,
        text=texto_ayuda2,
        bg="black",
        fg="white",
        justify=LEFT,
        wraplength=450,
        font=("Times New Roman", 11)
    )
	label_cont2.pack(padx=20, pady = (0, 0), anchor = "w") #Se inserta párrafo 2

	label_tit3 = Label(ayuda, text = "MODO ACCESIBLE", font = ("Times New Roman",13, "bold"), bg = "black", fg = "white", justify = LEFT)
	label_tit3.pack(padx = (20, 0), pady = (5, 5), anchor = "w") #Meteos título 2

	texto_ayuda3 = ("El botón MODO ACCESIBLE facilita a la persona con necesidades especiales a evitar las estaciones que no dispongan de servicio de ascensor.\n"
		"Al activarse (botón ON) y después de pulsar 'Calcular ruta A*', saltará un aviso en caso de que la estación destino no tuviera ascensor."
		"\n\nNota: Al poner la fecha de viaje, el algoritmo lo tendrá en cuenta solo si el formato es correcto y el día de viaje fuera un día de vacaciones, aumentando el coste en metros\n"
		"Nota: Para casos extremos, si tiene problema durante la ejecución, considere pulsar el botón de reinicio. \n\n"
		"\nFechas de vacaciones: 20/12 a 7/1, 14/4 a 20/4 y del 15/7 a 25/8\n\n"
    )
	label_cont3 = Label(
        ayuda,
        text=texto_ayuda3,
        bg="black",
        fg="white",
        justify=LEFT,
        wraplength=450,
        font=("Times New Roman", 11)
    )
	label_cont3.pack(padx=20, pady = (5, 0), anchor = "w") #Se inserta párrafo 3
	boton_salir.pack(pady = (0, 15))
  #fin del boton de ayuda---VM

#======================================================================

# Nota, las imágenes se ponen en un directorio carpeta llamado "res". Todas las imágenes png están allí

#======================================================================
my_img_original = Image.open("res/MapaMetroCDMX.png")
my_img_minusvalido = Image.open("res/MapaMetroCDMXminus.png")

#Recortar porque es muy grande
area_recorte = (0, 400, 600, 1050) #left up right bottom, mas de cuanto

my_img1 = my_img_original.crop(area_recorte)
my_img = ImageTk.PhotoImage(my_img1)

my_img2 = my_img_minusvalido.crop(area_recorte) #Hago lo mismo para el caso de imagen minusválido
my_img_minus = ImageTk.PhotoImage(my_img2)
#my_label = Label(image=my_img)
#my_label.pack()

#-----Creando nodos-------------------------

ancho_img = my_img1.width
alto_img = my_img1.height
#550x650 es tamaño de imagen
#print(f"Image: {ancho_img} x {alto_img}")

	###Crear canvas
conjunto = Canvas(root, width=ancho_img, height=alto_img)
#conjunto.place(x=0, y=0) #Coordenadas para fijar canvas con imagen (x,y=0 por pack())

imagenId = conjunto.create_image(0, 0, anchor=NW, image=my_img)

conjunto.pack(side=RIGHT, padx=(5, 15), pady=5)
## Buscar nodo en imagen, método temporal, utilizado durante el desarrollo
groupLabel = Label(root, text = "Grupo 203", font = ("Times New Roman", 20, "bold", "underline"), bg = "black", fg = "white", justify = CENTER)
groupLabel.pack(padx = (20,0), pady = (40, 0),side = TOP, anchor = "w")
auth = Label(root, text = "Autores:  ", font = ("Times New Roman", 14, "underline"), bg = "black", fg = "white")
auth.pack(pady = (20, 5), padx = (0, 90), side = TOP)
auth2 = Label(root, text = " Haodi Lin Sun \n Iván Arias de Dios \n David Sancho Romera \n Víctor Manuel Vergara Martínez\n Diana Moslemi Baghermanesh \n Renata Ramazanova"
, font = ("Times New Roman", 10), bg = "black", fg = "white",justify = LEFT)

auth2.pack(pady = (0, 5))

textoDate = Label(root, text="Introduce la fecha de viaje (Opcional)", bg = "yellow") #Parte de fecha
textoDate.pack(pady=(50,5), padx=(15, 10))
entradaD = Entry(root, fg="gray")
entradaD.pack()
entradaD.insert(0, "dd/mm/aaaa") #Formato día mes y año

def fecha_click(self):
    if entradaD.get() == "dd/mm/aaaa":
        entradaD.delete(0, END)
        entradaD.config(fg="black")

def fecha_fuera(self):
    if entradaD.get() == "":
        entradaD.insert(0, "dd/mm/aaaa")
        entradaD.config(fg="gray")


# Vincular eventos
entradaD.bind('<FocusIn>', fecha_click)
entradaD.bind('<FocusOut>', fecha_fuera)


texto = Label(root, text="Introduce la estación de origen", bg = "yellow")
texto.pack(pady=(20,5), padx=(15, 10))

entrada = Entry(root)
entrada.pack()

texto2 = Label(root, text="Introduce la estación de destino", bg = "yellow")
texto2.pack(pady=(20,5), padx=(15, 10))
entrada2 = Entry(root)
entrada2.pack()

botones2 = Frame(root, bg = "black")
botones2.pack(side=TOP, padx=10, pady=10)
boton1 = Button(botones2, text="Calcular ruta A*", width=20, command=lambda: algoritmo(entrada.get(), entrada2.get()), fg="blue")
boton1.pack(pady=(5,0))
botonReset = Button(botones2, text="Reiniciar", width = 20, command=lambda: reset(), fg="green")
botonReset.pack(pady=(5,0))
botonExit = Button(botones2, text = "Salir", width = 20, command=lambda: exitRoot(), fg="red")
botonExit.pack(pady=(5,0))
respuesta = Label(botones2, text="", bg = "black", fg = "white")
respuesta.pack(pady = (15,0))
respuestaV = Label(botones2, text="", bg = "black", fg = "white") #Para introducir cuando se inserta una fecha de vacaciones 
respuestaV.pack(pady = (10,0))

##Creación de botón palanca on-off, para buscar estaciones con ascensores
on1 = Image.open("res/on.png")
on2 = Image.open("res/off.png")

tamtoggle = (70, 50) #Redimensionamiento

on11 = on1.resize(tamtoggle, Image.Resampling.LANCZOS)
on21 = on2.resize(tamtoggle, Image.Resampling.LANCZOS)

on = ImageTk.PhotoImage(on11)
off = ImageTk.PhotoImage(on21)

#botón
boton_on = 0
def switch():
	global boton_on, imagenId
	if boton_on == 1:
		on_button.config(image=off)
		conjunto.itemconfig(imagenId, image=my_img)
		boton_on = 0
	else:
		on_button.config(image=on)
		conjunto.itemconfig(imagenId, image=my_img_minus)
		boton_on = 1

respuesta2 = Label(botones2, text="MODO ACCESIBLE", bg="black", fg="white")
respuesta2.pack(side=LEFT, pady=0, ipady=5)  # ipady añade padding interno

on_button = Button(botones2, image=off, command=switch, bg="black", activebackground="black", bd=0)
on_button.pack(side=LEFT, pady=0, ipady=5)

'''
def encontrar_coordenadas(event):
    x = event.x
    y = event.y
    print(f"Coordenadas: ({x}, {y})")
    
    # Dibujar un punto rojo temporal para verificar
    conjunto.create_oval(x-3, y-3, x+3, y+3, fill='red', outline='') #izq, arr,der,abj
    
conjunto.bind("<Button-1>", encontrar_coordenadas)
'''
## Crear nodos: Prefiero utilizar una clase

paradas = {}
abierto = {}
cerrado = {}
padres = {}


#forma: linea + identificador. Ejemplo, linea 7, id 1 --> 71
#En caso de transbordo, 0 + identificador. Ejemplo, transbordo 1 --> 10
#Línea de transbordo: Ejemplo, linea 7 y 12 --> 712 (cifra menor primero)

#Longitud de interestación en metros. Datos reales
#for clave, valor in mi_dict.items():
#    print(f"Clave: {clave}, Valor: {valor}")
paradas[71] =Parada(71, 152, 450, 19.36280, -99.18926, "Barranca del Muerto", 7, 0, {20:1476})
paradas[72] =Parada(72, 152, 346, 19.38603, -99.18660, "San Antonio", 7, 0, {73:606, 20:788})
paradas[73] =Parada(73, 152, 313, 19.39209, -99.18614, "San Pedro de los Pinos", 7, 0, {72:606, 10:1084})
paradas[74] =Parada(74, 152, 192, 19.41252, -99.19172, "Constituyentes", 7, 0, {10:1005, 75:1430})
paradas[75] =Parada(75, 152, 131, 19.42609, -99.19171, "Auditorio", 7, 0, {74:1430, 76:812})
paradas[76] =Parada(76, 152, 64, 19.43457, -99.19125, "Polanco", 7, 0, {75:812})

paradas[11] =Parada(11, 113, 293, 19.39827, -99.19962,"Observatorio", 1, 0, {10:1262})
paradas[12] =Parada(12, 187.9, 217, 19.41361, -99.18216, "Juanacatlan", 1, 0, {10:1158, 13:973})
paradas[13] =Parada(13, 221, 184, 19.42115, -99.17688, "Chapultepec", 1, 0, {12:973, 14:501})
paradas[14] =Parada(14, 257.1, 147.9, 19.42189, -99.17104,"Sevilla", 1, 0, {13:501, 15:645})
paradas[15] =Parada(15, 295, 119, 19.42484, -99.16343,"Insurgentes", 1, 0, {14:645, 16:793})
paradas[16] =Parada(16, 335, 119, 19.42659, -99.15462, "Cuauhtemoc", 1, 0, {15:793, 30:409})

paradas[31] =Parada(31, 379, 565, 19.32499, -99.17397, "Universidad", 3, 0, {32:1306})
paradas[32] =Parada(32, 379, 531, 19.33662, -99.17704,"Copilco", 3, 0, {31:1306, 33:1295})
paradas[33] =Parada(33, 379, 497, 19.34628, -99.18085, "M. A. De Quevedo", 3, 0, {32:1295, 34:824})
paradas[34] =Parada(34, 379, 463, 19.35413, -99.17560,"Viveros", 3, 0, {33:824, 35:908})
paradas[35] =Parada(35, 379, 427, 19.36169, -99.17077,"Coyoacan", 3, 0, {34:908, 50:1153})
paradas[36] =Parada(36, 379, 358, 19.37942, -99.15942,"Division del Norte", 3, 0, {50:794, 37:715})
paradas[37] =Parada(37, 379, 322, 19.38620, -99.15722,"Eugenia", 3, 0, {36:715, 38:950})
paradas[38] =Parada(38, 379, 288, 19.39603, -99.15612,"Etiopia", 3, 0, {37:950, 40:1119})
paradas[39] =Parada(39, 379, 188, 19.41372, -99.15331,"Hospital General", 3, 0, {40:653, 310:559})
paradas[310] =Parada(310, 379, 152, 19.41950, -99.15046, "Niños Heroes", 3, 0, {39:559, 30:665})
paradas[311] =Parada(311, 379, 54, 19.43344, -99.14763,"Juarez", 3, 0, {30:659})

paradas[91] =Parada(91, 231, 254, 19.40581, -99.17738,"Patriotismo", 9, 0, {10:1133, 92:955})
paradas[92] =Parada(92, 322, 254, 19.40590, -99.16849,"Chilpancingo", 9, 0, {91:955, 40:1152})
paradas[93] =Parada(93, 472, 254, 19.40730, -99.14463,"Lazaro Cardenas", 9, 0, {40:1059})

paradas[121] =Parada(121, 215, 393, 19.37456, -99.17880,"Insurgentes Sur", 12, 0,{20:651, 122:725})
paradas[122] =Parada(122, 291, 393, 19.37258, -99.17136,"Hospital 20 de Noviembre", 12, 0, {121:725, 50:450})
paradas[123] =Parada(123, 466, 393, 19.37115, -99.15865,"Parque de los Venados", 12, 0,{50:563, 124:1280})
paradas[124] =Parada(124, 520, 474, 19.36146, -99.15131,"Eje Central", 12, 0, {123:1280})

###Nodos transbordo
paradas[10] =Parada(10, 152, 253.9, 19.40264, -99.18781,"Tacubaya", 19, 1, {74:1005, 11:1262, 73:1084, 91:1133, 12:1158})
paradas[20] =Parada(20, 152, 393, 19.37609, -99.18790,"Mixcoac", 712, 1, {71:1476, 72:788, 121:651})
paradas[30] =Parada(30, 379, 119, 19.42718, -99.14907,"Balderas", 13, 1, {16:409, 311:659, 310:665})
paradas[40] =Parada(40, 379, 254, 19.40661, -99.15539, "Centro Medico", 39, 1, {92:1152, 39:653, 93:1059, 38:1119})
paradas[50] =Parada(50, 379, 393, 19.37091, -99.16502,"Zapata", 312, 1, {122:450, 36:794, 123:563, 35:1153})
##Adicional

#Diccionario de estaciones para minusválidos
diccMinus = {
	#Línea 3
	"Universidad" : "Universidad", 
	"Copilco" : "Copilco",
	"Zapata" : "Zapata",
	"Etiopia" : "Etiopia",
	"Centro Medico" : "Centro Medico",
	"Hospital General" : "Hospital General",
	"Balderas" : "Balderas",
	"Juarez" : "Juarez",
	#Linea 7
	"Mixcoac" : "Mixcoac",
	"Polanco" : "Polanco",
	"Barranca del Muerto" : "Barranca del Muerto",

	#Linea 1
	"Observatorio" : "Observatorio",
	"Sevilla" : "Sevilla",
	"Insurgentes" : "Insurgentes",
	"Cuauhtemoc" : "Cuauhtemoc",

	#Linea12
	"Hospital 20 de Noviembre" : "Hospital 20 de Noviembre",
	"Insurgentes Sur" : "Insurgentes Sur",
	"Parque de los Venados" : "Parque de los Venados",
	"Eje Central" : "Eje Central"
} 

def quitarBarras(event): #Funciona si pulso en la ventana principal, no en la imagen ni en cuadros de texto
	if event.widget == root: #Si se pulsa en la ventana principal
		root.focus_set()

#botón de ratón izquierdo es <Button-1>
root.bind("<Button-1>", quitarBarras) #Quitar barras de cursor en los cuadros de texto
#Recuerdo que bind siempre pasa el parámetro event

#boton invisible para añadir---VM
def click_en_mapa(event): 

	x_click = event.x #Nos da coordenada x del punto clickeado
	y_click = event.y #igual con la y
	R = 10 
	estacion_cercana = None #Inicializo estacion cercana
	min_dist2 = R * R + 1 #Distancia mínima con margen 1

	for parada in paradas.values():
		dx = x_click - parada.coordX
		dy = y_click - parada.coordY
		dist2 = dx*dx + dy*dy #Se calcula la distancia euclideana entre el punto clickeado y punto real de estacion
		if dist2 < min_dist2: #Si se cumple que la distancia está dentro del perímetro establecido
			estacion_cercana = parada #Estación deja de ser None
			break #Si cumple la condición, dejo de buscar
	
	if estacion_cercana is not None: #Y se cumple esta condición con estacion_cercana != none
		nombre_estacion = estacion_cercana.nom

		# Se añade el nombre de estacion a la casilla que esté vacía
		if entrada.get() == "": 
			entrada.delete(0, END)
			entrada.insert(0, nombre_estacion)

		elif entrada2.get() == "":  
			entrada2.delete(0, END)
			entrada2.insert(0, nombre_estacion)

		else: #Si ambas están llenas y quiero meter nuevo origen destino, se mete nuevo origen
			entrada.delete(0, END)
			entrada2.delete(0, END)
			entrada.insert(0, nombre_estacion)

# fin boton invisible para añadir---VM

def get_id(diccionario, nombre):
	for id_parada, x in diccionario.items():
		if x.nom == nombre:
			return id_parada
	return None

#=======================================================
##Código algorítmico--------------------------------

#Cálculo de heurística: Distancia entre un nodo y otro
#sqrt((n1x - n2x)**2 + (n1y - n2y)**2)


#Probado con dos estaciones adyacentes

## Ciudad de México: latitud 19.4
## Buscada en internet
## Latitud en toda la tierra: 111.11km/grado (posicion norte-sur)
## Longitud = 104.5 (posicion este-oeste) en CDMX


def heuristica(lat1, lon1, lat2, lon2):
	lat_kmPorGrado = 111.319 #Valores generales buscadas en internet
	lon_kmPorGrado = lat_kmPorGrado * math.cos(19.4)

	df_lat_km = abs(lat2 - lat1) * lat_kmPorGrado
	df_lon_km = abs(lon2 - lon1) * lon_kmPorGrado

	h = math.sqrt(df_lat_km**2 + df_lon_km**2)
	
	if h < 3.0:
		h = h * 0.7075
	elif h < 4.0:
		h = h * 0.8244 #0.82
	elif h < 6.0:
		h = h * 0.8527
	elif h < 8.0:
		h = h * 0.8465
	elif h < 10:
		h = h * 0.877
	elif h < 12:
		h = h * 0.8821
	else:
		h = h * 0.9409

	return h * 1000

# Orden latitud longitud predefinido en coordenadas de google maps

solucion = [] #Meter al final la solución
solucionAnimar = [] #Para animarlo
g1 = 0
pesos = {}
fSave = 0 #Guardar coste al final
primerId = 0 #Para la animacion
hInit = 0 #Heurística inicial

#mFF = menos Frecuencia Factor
mFF = 1 #Si estamos en vacaciones, la frecuencia de los trenes disminuye, y aumentamos coste, el factor
calculandoActive = False #Estado de calculando o no

def calculando(id_estacion, id_estacion2, g):
	#Estacion 2 es nuestra meta
	global paradas, abierto, cerrado, g1, padres, primerId, fSave, hInit, mFF, calculandoActive
	#update, pop, get son los métodos a poder utilizar

	if primerId == 0: #Solo se ejecuta una vez, para hallar heurística inicial
		primerId = id_estacion
		x1 = paradas[id_estacion].realX
		x2 = paradas[id_estacion2].realX  
		y1 = paradas[id_estacion].realY
		y2 = paradas[id_estacion2].realY
			
		hInit = (math.ceil(heuristica(x1, y1, x2, y2) * 100 * mFF) / 100)


	#Termino si ya he llegado a la meta
	if id_estacion == id_estacion2:
		fSave = (math.ceil(g * 100 * mFF) / 100 ) #aumenta coste final en vacaciones
		solucion.append(paradas.get(id_estacion).nom)
		solucionAnimar.append(id_estacion)
		id_padre = padres.get(id_estacion)
		while id_padre != None: #Si es None, no añado nodos padre
			solucion.append(paradas.get(id_padre).nom)
			solucionAnimar.append(id_padre)
			id_padre = padres.get(id_padre)
		 #No necesito añadir la heurística anterior

		respuesta.config(text="Mostrando trayecto --->", fg="white")
		abierto.clear()
		cerrado.clear()
		pesos.clear()
		padres.clear()
		solucion.reverse()
		solucionAnimar.reverse()
		return mostrar_Solucion(solucion)


# Expansión
	for id_sig, subpeso in paradas[id_estacion].conexiones.items():
		if id_sig in cerrado: #Si estuviera en cerrado, se ignora
			continue

		g1 = (g + subpeso)

		#Tengo latitud y longitud de cada estación
		x1 = paradas[id_sig].realX
		x2 = paradas[id_estacion2].realX
		y1 = paradas[id_sig].realY
		y2 = paradas[id_estacion2].realY

		h1 = math.ceil(heuristica(x1, y1, x2, y2) * 100) / 100 #Euclideana, heurística
		f = g1 + h1

		#Buscando nodos mejores o update si es necesario
		if id_sig not in abierto or g1 < pesos.get(id_sig):

			padres.update({id_sig : id_estacion}) #Añadir padre de cada nodo o actualizarlo si hay mejor nodo
			abierto.update({id_sig : f}) #Añadir o actualizar
			pesos.update({id_sig : g1}) #Para comparar después y decidir proximo nodo padre

	#Cambiar nodo actual a cerrado
	cerrado.update({id_estacion:abierto.get(id_estacion)})
	if id_estacion in abierto:
		abierto.pop(id_estacion)
	#Si hay nodos abiertos
	if abierto:
	## Añadir aquí para garantizar búsqueda en anchura
	## Sacamos el mejor nodo, minimo f. Utilizo clear() para vaciar el diccionario
		minimo = min(abierto.items(), key=lambda x: x[1])[0]  # Busca mínimo por valor f(n)#devuelve id con peso minimo
		#[0] devuelve id, x[1] de buscar por coste minimo
		pesoM = pesos.get(minimo)

		return calculando(minimo, id_estacion2, pesoM)

	padres.pop(id_estacion)
	return None

ventanaSolucion = 0 #Si hay ventana (valor 1), destruirla en la siguiente iteración
ventana = 0

## Variables para el tren durante la animación. El tren indicará el recorrido tomado  por el algoritmo
tren_base = Image.open("res/tren.png").resize((50, 50)) #Ajustamos la imagen al tamaño de las vías
tren_img = ImageTk.PhotoImage(tren_base)
tren_icono = None

ventAviso = None #inicializar ventana de aviso
resetA = False
def algoritmo(nombre, nombre2):
	global paradas, boton_on, diccMinus, ventAviso, mFF, calculandoActive, resetA
	

	if calculandoActive == True: #No voy a calcular el algoritmo con Enter si se está calculando
		return
	calculandoActive = True # Se está calculando
	boton1.config(state = DISABLED) 
	on_button.config(state = DISABLED)#El botón no funcinará temporalmente
	resetA = False #Preparando Reset. Indica que aún no se ha pulsado botón de inicio
	#Ya tengo las ids de las dos estaciones
	id_estacion = get_id(paradas, nombre) #Origen
	id_estacion2 = get_id(paradas, nombre2) #Destino

	#Obtener valores de fecha:
	fechaObtenida = entradaD.get().strip() #Quito espacios en blanco en fechas. Diferente a nombres de estaciones, ya que algunas necesitan espacio
	fechaPartes = fechaObtenida.split("/")

	anio = 0
	dia = 0
	mes = 0
	hayFecha = False #Si se pone una fecha, se vuele true
	if("/" in fechaObtenida and fechaObtenida != "dd/mm/aaaa"):
		hayFecha = True
		if (len(fechaPartes) == 3 and fechaPartes[0].isdigit() and fechaPartes[1].isdigit() and fechaPartes[2].isdigit()):
			dia = int(fechaPartes[0]) #Por ejemplo, 01 --> 1
			mes = int(fechaPartes[1])
			anio = int(fechaPartes[2])
			

	else:
		dia = 0
		mes = 0

	#Clasificando días y meses. Suposiciones de vacaciones de MEXICO
	if dia >= 1 and dia <= 31: #Los días deben estar en este intervalos
		#Vacaciones Navidad suponiendo 20/12 --> 7/1
		if (mes == 12 and dia >= 20) or (mes == 1 and dia <= 7):
			mFF = 1.1
			respuestaV.config(text="Día de vacaciones de Navidad")
		#Vacaciones Semana Santa suponiendo 14/4 --> 20/4
		elif (mes == 4 and dia >= 14 and dia <= 20):
			mFF = 1.05
			respuestaV.config(text="Día de vacaciones de Semana Santa")
		#Vacaciones de Verano suponiendo 15/7 --> 25/8
		elif (mes == 7 and dia >=15 and dia <=30) or(mes == 8 and dia <= 25):
			mFF = 1.15 #suponiendo que se ausentan más en verano
			respuestaV.config(text="Día de vacaciones de Verano")
		else:
			mFF = 1
			respuestaV.config(text="")
	#Como es opcional, si pones otra cosa en la fecha, al algoritmo le dará igual
	else:
		mFF = 1
		respuestaV.config(text="")

	if hayFecha and anio != 2025:
		respuesta.config(text="Escribe año actual si vas a poner la fecha")
		respuestaV.config(text="Recuerda seguir el formato especificado")
		entradaD.delete(0,END)
		boton1.config(state = NORMAL)
		on_button.config(state = NORMAL)
		calculandoActive = False #Se deja de calcular, para dar otra oportunidad

	elif id_estacion == None:
		respuesta.config(text="Escribe una estación origen existente", fg="white")

		entrada.delete(0, END)
		boton1.config(state = NORMAL)
		on_button.config(state = NORMAL)
		calculandoActive = False #Se deja de calcular, para dar otra oportunidad
		
	elif id_estacion2 == None:
		respuesta.config(text="Escribe una estación destino existente", fg="white")
		boton1.config(state = NORMAL)
		on_button.config(state = NORMAL)
		calculandoActive = False #Se deja de calcular, dando otra oportunidad
		entrada2.delete(0, END)

	else:	

		borrarVent()

		permit = 1 #Permiso para continuar si no es minusválido / lo es
		##En el caso de que el botón de minusválidos estuviera encendido y se fuera a una estación sin servicio para minusválidos
		if nombre != nombre2 and diccMinus.get(nombre2) is None and boton_on == 1:
			permit = 0
			boton1.config(state = DISABLED)
			on_button.config(state = DISABLED)#El botón no funcinará temporalmente
			def permitir(): #Para dejar pasar si se quiere calcular la ruta optima
				global ventAviso
				permit = 1
				if ventAviso is not None:
					ventAviso.destroy()
					ventAviso = None
				continuar()
			
			def cancelar(): #Por si el usuario dice que no quiere calcular
				global ventAviso
				reset()
				switch()
				if ventAviso is not None:
					ventAviso.destroy()
					ventAviso = None
			
			if ventAviso is not None:
				ventAviso.destroy()
				ventAviso = None
			
			ventAviso = Toplevel(root)
			ventAviso.configure(bg = "black")
			ventAviso.geometry("+200+200")
			ventAviso.grab_set() #Fijo la ventana hasta que se cierre


			avisoLabel = Label(ventAviso, text = "Aviso", font = ("Times New Roman", 24, "bold"), bg = "black", fg = "white")
			avisoLabel.pack(side = TOP, pady=(20, 15))

			avisoLabel4 = Label(ventAviso, text = "    La estación '" + nombre2 + "' no dispone de ascensores destinados para personas con discapacidad    \n\n Aun así, ¿quiere calcular la ruta óptima?", font = ("Times New Roman", 16), bg = "black", fg = "white")
			avisoLabel4.pack(side = TOP, pady= 2)

			frame1 = Frame(ventAviso, bg = "black")
			frame1.pack(side = TOP)
			boton_salir = Button(frame1, width = 5, text = "No", fg = "red", command = cancelar)
			boton_salir.pack(side = LEFT, padx = 15, pady = (10, 5), anchor = "s")
			boton_salir2 = Button(frame1, width = 5, text = "Sí", fg = "green", command = permitir)
			boton_salir2.pack(side = LEFT, padx = 15, pady = (10, 5), anchor = "s")
		
		hayFecha = False	

		def continuar():	
			global tren_icono

			borrar_lineas()
			borrar_circulos()

			conjunto.unbind("<Button-1>") #Temporalmente no funciona el click a estaciones
			if tren_icono: #Quitamos el icono del tren anterior
				conjunto.delete(tren_icono)
				tren_icono = None
			respuesta.config(text="Calculando ruta óptima")
			respuesta.after(500, lambda: calculando(id_estacion, id_estacion2, 0))

		if permit == 1:
			continuar()
		
			
conjuntoLineas = [] #Para poder después borrar las líneas
conjuntoCirculos = [] #Para poder después borrar los círculos
conjuntoAnimaciones = [] #Para poder después borrar las animaciones en reinicio
salirRoot = True #Por si quiero salir del Root en ejecución

specialBool = False #En caso de encontrarme con caso especial de recorrido no recto
specialBool2 = False
circulo2 = None
circulo = None


## Función para rotar el tren cuando vaya a girar
def rotar_tren(dx, dy):

    angulo = math.degrees(math.atan2(dy, dx))
    imagen_rotada = tren_base.rotate(-angulo, resample=Image.BICUBIC, expand=True)
    return ImageTk.PhotoImage(imagen_rotada)

def animar_linea(x1, y1, x2, y2): #Se anima cada segmento
	global conjuntoLineas, conjuntoCirculos, specialBool, specialBool2, circulo, conjuntoAnimaciones, tren_icono, tren_img
	root.protocol("WM_DELETE_WINDOW", exitRoot)

	linea = conjunto.create_line(x1, y1, x1, y1, fill="red", width=4)
	conjuntoLineas.append(linea)
	
	if specialBool == False and specialBool2 == False: #Quiero que vaya más rápido los segmentos con caso especial
		pasos = 30 #7 para rapido
	else:
		pasos = 15 #5 para rapido
	dx = (x2 - x1) / pasos
	dy = (y2 - y1) / pasos

	#Si hay circulos. Propósito: para que el primer circulo sea azul
	if specialBool == False and specialBool2 == False: #No dibujar circulo con caso especial 
		if conjuntoCirculos: 
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="green")
		else: #Primer circulo azul
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="blue")
		conjuntoCirculos.append(circulo)
	
	
	tren_img = rotar_tren(dx, dy) #Se actualiza la posición del tren, y rotamos cuando es necesario

	if tren_icono is None: #Si es la primera vez que se crea el tren
		tren_icono = conjunto.create_image(x1, y1, image=tren_img) #Se coloca en la posición de inicio
	else:
		conjunto.itemconfig(tren_icono, image=tren_img) #configuramos la posición del tren para que se adapte cuando la trayectoria no es recta
		conjunto.coords(tren_icono, x1, y1) #Establecer coordenadas iniciales del tren

	def actualizar(paso=0):  # Valor por defecto
		global specialBool, specialBool2, circulo2, conjuntoAnimaciones
		if paso <= pasos:
			nuevox = x1 + dx * paso
			nuevoy = y1 + dy * paso
			conjunto.coords(linea, x1, y1, nuevox, nuevoy) #anim1 es animacion 1
			conjunto.coords(tren_icono, nuevox, nuevoy) #Actualizamos

			if tren_icono is not None: #Verifico si existe o no. If robusto para evitar problemas extremos críticos
				conjunto.tag_raise(tren_icono, 'all') #Mantener el tren encima de todo

			anim1 = root.after(30, lambda: actualizar(paso + 1))  # ← Pasa paso + 1
			conjuntoAnimaciones.append(anim1)
		#40 * 30 = 1200ms para la animación
		else:
			# Dibujar círculo al final y borrar después
			if specialBool == False and specialBool2 == False: #No dibujar circulo con caso especial 
				circulo2 = conjunto.create_oval(x2-5, y2-5, x2+5, y2+5, outline="red", width = 2, fill="yellow")
				conjunto.tag_raise(tren_icono, 'all') #Mantener el tren encima de las animaciones de circulo
				conjuntoCirculos.append(circulo2)
			
	actualizar()  

#Manejar caso en el que la ruta no sea en linea recta


def especial1(x1, y1, x2, y2): #ir a 484,393 <--> 499, 474 Parque Venados <--> EjeCentral
	global specialBool, conjuntoCirculos, circulo, circulo2, conjuntoAnimaciones
	specialBool = True

	if x1 == 466: #Si origen es Parque Venados
		aux1 = 484 #Coordenadas que no son estaciones, sino puntos de inflexión intermedias
		aux2 = 393 #Por esa razón, no ponemos círculos en los puntos intermedios
		animar_linea(x1, y1, aux1, aux2) #Trozo 1

		if conjuntoCirculos:  #Creo circulo verde si no es nodo final. Lo pongo aquí para que no se solape con la línea en la ejecución
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="green")
		else: #Primer circulo azul, en caso de ser primera estación
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="blue")
		conjuntoCirculos.append(circulo)

		aux11 = 499
		aux12 = 474 #100, 200, 300 para rapido
		anim2 = root.after(500, lambda: animar_linea(aux1, aux2, aux11, aux12)) #Trozo2
		conjuntoAnimaciones.append(anim2)
		anim3 = root.after(1000, lambda: animar_linea(aux11, aux12, x2, y2)) #Trozo3
		conjuntoAnimaciones.append(anim3)
		anim4 = root.after(1500, lambda: quitarSp1(x2, y2))
		conjuntoAnimaciones.append(anim4)
	
	else: #Si origen es Eje Central
		aux1 = 499 #Coordenadas que no son estaciones, sino puntos de inflexión intermedias
		aux2 = 474 #Por esa razón, no ponemos círculos en los puntos intermedios
		animar_linea(x1, y1, aux1, aux2) #Trozo 1

		if conjuntoCirculos:  #Creo circulo verde si no es nodo final. Lo pongo aquí para que no se solape con la línea en la ejecución
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="green")
		else: #Primer circulo azul, en caso de ser primera estación
			circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="blue")
		conjuntoCirculos.append(circulo)

		aux11 = 484
		aux12 = 393
		#100, 200, 300 para rapido
		anim5 = root.after(500, lambda: animar_linea(aux1, aux2, aux11, aux12)) #Trozo2
		conjuntoAnimaciones.append(anim5)
		anim6 = root.after(1000, lambda: animar_linea(aux11, aux12, x2, y2)) #Trozo3
		conjuntoAnimaciones.append(anim6)
		anim7 = root.after(1500, lambda: quitarSp1(x2, y2))
		conjuntoAnimaciones.append(anim7)

	
def quitarSp1(x2, y2): #Quito Special 1, fin de caso especial 1
	global specialBool, circulo2, conjuntoCirculos
	specialBool = False
	

def especial2(x1, y1, x2, y2): #ir a 288, 119 Sevilla <--> Insurgentes
	global specialBool2, conjuntoCirculos, circulo, circulo2, conjuntoAnimaciones
	specialBool2 = True

	#Se trata de un solo punto intermedio, por lo que el if en este caso no es necesario a diferencia del caso especial anterior
	aux1 = 286 #Coordenadas que no son estaciones, sino puntos de inflexión intermedias
	aux2 = 119 #Por esa razón, no ponemos círculos en los puntos intermedios
	animar_linea(x1, y1, aux1, aux2) #Trozo 1

	if conjuntoCirculos:  #Creo circulo verde si no es nodo final. Lo pongo aquí para que no se solape con la línea en la ejecución
		circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="green")
	else: #Primer circulo azul, en caso de ser primera estación
		circulo = conjunto.create_oval(x1-5, y1-5, x1+5, y1+5, outline="red", width = 2, fill="blue")
	conjuntoCirculos.append(circulo)
	#100 y 300 para rapido
	anim8 = root.after(500, lambda: animar_linea(aux1, aux2, x2, y2)) #Trozo2
	conjuntoAnimaciones.append(anim8)
	anim9 = root.after(1000, lambda: quitarSp2(x2, y2))
	conjuntoAnimaciones.append(anim9)

def quitarSp2(x2, y2): #Quito Special 1, fin de caso especial 1
	global specialBool2, circulo2, conjuntoCirculos
	specialBool2 = False

def exitRoot(): #Manejo salida de ventana mediante funcion
	global salirRoot
	if salirRoot:
		root.quit()

def borrar_lineas():
	global conjuntoLineas
	if conjuntoLineas:
		for x in conjuntoLineas:
			conjunto.delete(x)
		conjuntoLineas = []

def borrar_circulos():
	global conjuntoCirculos
	if conjuntoCirculos:
		for y in conjuntoCirculos:
			conjunto.delete(y)
		conjuntoCirculos = []
def borrar_animaciones():
	global conjuntoAnimaciones
	if conjuntoAnimaciones:
		for x in conjuntoAnimaciones:
			try: #Manejar error de ids de animaciones
				root.after_cancel(x)
			except ValueError: #Variable por defecto para manejar en Python
				continue
		conjuntoAnimaciones = []

id_origen = 0
sig_id = 0
ventana = None
def mostrar_Solucion(lista):
	global primerId, solucionAnimar, fSave, hInit, ventana, solucion, salirRoot, id_origen, conjuntoAnimaciones
	print(lista)
	
	#Ventana para mostrar ruta óptima
	ventana = Tk()
	ventana.title("Ruta óptima. Algoritmo A*")
	ventana.config(bg = "black")
	ruta = "\n--> ".join(lista)
	ventana.protocol("WM_DELETE_WINDOW", borrarVent) #para saber si he cerrado o no. WM de window manager
	ventana.withdraw() #Ocultar temporalmente
	x1 = paradas[primerId].coordX
	y1 = paradas[primerId].coordY
	id_origen = solucionAnimar.pop(0)
    # Esperar 1 segundo antes de empezar

	anim10 = root.after(500, lambda: animar_todo(x1, y1))
	conjuntoAnimaciones.append(anim10)

	textoAdd = Label(ventana, text = "El coste total mínimo es: " + str(fSave) + " metros", font=("Times New Roman", 15), padx=20, pady = 5, bg = "black", fg = "white")
	textoAdd.pack(side = LEFT)
	textoAdd2 = Label(ventana, text = "Heurística inicial: " + str(hInit) + " metros", font=("Times New Roman", 15), padx=20, pady = 5, bg = "black", fg = "white")
	textoAdd2.pack(side = LEFT)
	ventana.geometry("+0+10")

	solucion.clear()
	primerId = 0
	hInit = 0
	fSave = 0

	rutaText = Label(ventana, text = "Recorrido óptimo:", font=("Times New Roman", 15, "bold"), padx=20, bg = "black", fg = "white", wraplength = 400)
	rutaText.pack(side = TOP, pady = (10, 5))
	rutaText = Label(ventana, text = " " + ruta, font=("Times New Roman", 15), padx=20, bg = "black", fg = "white", wraplength = 400, justify = LEFT)
	rutaText.pack(side = TOP, pady = (10, 5))
	respuesta.config(text="Fin del algoritmo", fg="white")
	
def borrarVent():
	global ventanaSolucion, ventana
	if ventanaSolucion == 1:
		ventanaSolucion = 0
		if ventana is not None:
			ventana.destroy()
			ventana = None


def desbloquear_interfaz(): #Función de desbloqueo
	global calculandoActive, ventana, ventanaSolucion, resetA

	if resetA == False: #Si no se ha reseteado, desbloqueo la interfaz
		if ventana is not None:
			ventana.deiconify() 
		ventanaSolucion = 1	
		calculandoActive = False
		anim14 = boton1.config(state=NORMAL)
		anim15 = on_button.config(state=NORMAL)
		conjuntoAnimaciones.append(anim14)
		conjuntoAnimaciones.append(anim15)
		conjunto.bind("<Button-1>", click_en_mapa)

def animar_todo(x1, y1):
	global solucionAnimar, id_origen, sig_id, specialBool, calculandoActive, specialBool2, conjuntoAnimaciones, boton_on

	if solucionAnimar: #En caso de que la estación origen y destino sea el mismo
		sig_id = solucionAnimar.pop(0)
		x2 = paradas[sig_id].coordX
		y2 = paradas[sig_id].coordY   
		# Animamos este segmento. Manejando caso especial diagonal
		if (id_origen == 123 and sig_id == 124) or (id_origen == 124 and sig_id == 123):
			
			especial1(x1, y1, x2, y2) #Animar linea diferente
			
		elif (id_origen == 14 and sig_id == 15) or (id_origen == 15 and sig_id == 14):
			
			especial2(x1, y1, x2, y2) #Animar linea diferente 2
			
		else:
			animar_linea(x1, y1, x2, y2) #Animar en linea recta

		id_origen = sig_id
		
        
    # Preparamos siguiente segmento
	if solucionAnimar: #Si llegaramos al nodo final, nos saltaríamos este if
		if specialBool == True or specialBool == True:
			#500 para rapido
			anim11 = root.after(2000, lambda:animar_todo(x2,y2)) #El programa especial se ejecuta en menos de 0.8 segundos
			conjuntoAnimaciones.append(anim11)
		else:
			#300 para rapido
			anim12 = root.after(1400, lambda: animar_todo(x2, y2)) #El programa normal se ejecuta en menos de 0.4 segundos
			conjuntoAnimaciones.append(anim12)
	else:

		anim13 = root.after(3000, desbloquear_interfaz)
		conjuntoAnimaciones.append(anim13)
		

		
	
def reset(): #Función de reinicio
	global specialBool, ventana, specialBool2, abierto, cerrado, pesos, padres, solucion, calculandoActive, solucionAnimar, tren_icono, ayuda, resetA
	if ayuda != None: #Si existe ventana de ayuda
		ayuda.destroy()
		ayuda = None #Reestablecer valor original

	if ventana != None:
		ventana.destroy()
		ventana = None

	calculandoActive = False
	borrar_animaciones()
	borrar_lineas()
	borrar_circulos()
	borrarVent()
	specialBool = False
	specialBool2 = False
	entrada.delete(0, END)
	entrada2.delete(0, END)	
	respuestaV.config(text = "")	
	abierto.clear()
	cerrado.clear()
	pesos.clear()
	padres.clear()
	solucion.clear()
	solucionAnimar.clear()
	conjunto.delete(tren_icono) #Quitar el tren al reiniciar
	tren_icono = None
	if boton_on == 1: #Cambiar botón a off cuando se reinicie
		switch()
	entradaD.delete(0, END)
	entradaD.config(fg="gray")
	entradaD.insert(0, "dd/mm/aaaa")
	entradaD.bind('<FocusIn>', fecha_click)
	entradaD.bind('<FocusOut>', fecha_fuera)
	
	on_button.config(state = NORMAL)
	respuesta.config(text="",fg="white")
	boton1.config(state = NORMAL)
	conjunto.bind("<Button-1>", click_en_mapa) #Volver a permitir el clickeo en mapa
	desbloquear_interfaz()
	resetA = True #Ya se ha reseteado. No hace falta volver a desbloquear interfaz hasta que empiece el algoritmo
	root.focus_set() #El root no tendrá barras de cursor en los cuadros de texto


#Importante las indentaciones
#Cuando se está ejecutando una función después de root.after, lo que está abajo no espera, y se ejecuta inmediatamente
#=======================================================
#-------------------------------------------------------


#Contenedor de etiqueta invisible
frame_inferior = Frame(root, bg="black")
frame_inferior.pack(side=RIGHT)

barra_estado = Label(frame_inferior, text="Instrucciones de uso", font=("Times New Roman", 12), bg="black", fg="white")
barra_estado.pack(side=RIGHT, padx = (0, 90))

#boton de ayuda---VM
tam_boton = 40
help_canvas = Canvas(root, width=tam_boton, height=tam_boton,bg="black", highlightthickness=0, bd=0)
help_canvas.pack(side=BOTTOM, anchor="w", padx=(20, 10), pady=20)
margen = 2

circuloAyuda = help_canvas.create_oval(
    margen, margen,
    tam_boton - margen, tam_boton - margen,
    fill="white",
    outline="black",
    width=2,
)

textoAyuda = help_canvas.create_text(
    tam_boton // 2, #Hace división entera y devuelve Int. Coordenada X
    tam_boton // 2, #Igual, Coordenada Y
    text="?",
    fill="black",
    font=("Times New Roman", 18, "bold")
)
help_canvas.bind("<Button-1>", lambda event: mostrar_ayuda())
# ---------------------VM

#fin del boton de ayuda

root.bind('<Return>', lambda event: algoritmo(entrada.get(), entrada2.get())) #Esto es para que pueda pulsar botón Calcular Ruta con "Enter"

#boton de añadir de manera invisible---VM
conjunto.bind("<Button-1>", click_en_mapa)
#fin boton de añadir de manera invisible---VM

root.mainloop()

