 :- module(code,_,[classic,assertions,regtypes]).

author_data('Lin','Sun','Haodi','230255').

%Probando librería lpdoc


%--- Documentación lpdoc ---

:- doc(title, "Trípticos y Sumaciones").
:- doc(author, "Lin Sun, Haodi").
:- doc(module, "Grafos dinámicos y sumaciones de grado G.").

:- doc(save_graph/1, "save_graph(G) 
         
         Agarro el grafo G, limpio el anterior sin piedad
         (retract + fail hasta que no quede ni uno)
         y guardo cada arista como edge/2.
         Solo guardo la orientación canónica (A @< B).
         G es una lista de estructuras edge/2.
         El predicado edge/2 debe ser dinámico.").

:- doc(triptych/0, "triptych 
         
         Examino el grafo guardado y sentencio: sí o no.
         Una sola vez. Sin marearme.
         Un tríptico es un subgrafo que toca todos los nodos,
         es un árbol (E = N - 1), y tiene UN solo nodo con grado > 2.
         Si lo encuentro, éxito. Si no, fallo finito.").

:- doc(pow_list/3, "pow_list(I, G, Ps) 
         
         Devuelve en Ps las potencias de G <= I,
         ordenadas de mayor a menor.").

:- doc(summ_g/3, "summ_g(I, G, S) 

         Devuelve por backtracking todas las sumaciones de I de grado G.
         De más corta a más larga, sin permutaciones absurdas.").

:- doc(n_summ/3, "n_summ(I, G, NSum) 
         
         Devuelve en NSum el número total de sumaciones de grado G para I.").


%---Fin Lpdoc-------


%Ejercicio 1

:- dynamic edge/2. %librería para retract y assert

% Predicado 1.1

save_graph(G) :-
    borrar_grafo,          % Borro el grafo, y después guardo uno nuevo
    guardar_edges(G).  

% Borra todos los edge/2 usando retract + fail
borrar_grafo :- %Bucle de borrado, si falla el borrado, es que está vacío edges/2
    retract(edge(_, _)),
    fail.
borrar_grafo. %Si no falla, segunda alternativa, volver a ejecutar


% Recorre la lista guardando cada arista
guardar_edges([]).
guardar_edges([edge(A, B) | Resto]) :- %Se guarda de cada componente
    guardar_arista(A, B),
    guardar_edges(Resto). %Inicio de bucles

% Ahora guarda la arista canónica, solo para A < B, siendo edge(A, B).
guardar_arista(A, B) :-
    A @< B,
    !,
    assert(edge(A, B)). %Assert es clave para que funcione el guardado
guardar_arista(A, B) :-
    B @< A,
    !,
    assert(edge(B, A)).
guardar_arista(_, _).   % A == B, se ignora


%Predicado 1.2

% Ya tengo el grafo. Ahora tengo que recolectar los nodos




% Predicado 1.2


triptych :-
    findall(N, (edge(N,_);edge(_,N)), Resultado),
    sort(Resultado, ListaNodos),
    length(ListaNodos, N),
    member(Candidato, ListaNodos),
    construir_arbol([Candidato], [Candidato], AristasArbol),
    length(AristasArbol, E),
    E =:= N - 1,
    findall(Nodo, (member(Nodo, ListaNodos), grado_arbol(Nodo, AristasArbol, G), G > 2), Excedido),
    length(Excedido, 1).

% Predicados auxiliares (FUERA de triptych)
vecino(Nodo, Vecino) :- edge(Nodo, Vecino).
vecino(Nodo, Vecino) :- edge(Vecino, Nodo).

construir_arbol([], _, []).

construir_arbol([Actual | Pendientes], Visitados, [edge(Actual, V) | Aristas]) :-
    vecino(Actual, V),
    \+ member(V, Visitados),
    construir_arbol([V, Actual | Pendientes], [V | Visitados], Aristas).

construir_arbol([_Actual | Pendientes], Visitados, Aristas) :-
    construir_arbol(Pendientes, Visitados, Aristas).

grado_arbol(Nodo, Aristas, G) :-
    findall(_, (member(edge(Nodo, _), Aristas); member(edge(_, Nodo), Aristas)), Conexiones),
    length(Conexiones, G).

%Ejercicio 2

% Predicado 2.1

pow_list(I, G, Ps) :- A = 1, pow_list_aux(I, G, A, [], Ps).

% Dos casos

pow_list_aux(I, _G, A, AuxL, AuxL) :-  I < A.

pow_list_aux(I, G, A, AuxL, Ps) :- 
    AuxL2 = [A|AuxL], 
    I >= A, 
    A2 is A * G, 
    pow_list_aux(I, G, A2, AuxL2, Ps).


% Predicado 2.2

summ_g(0, _, []).

summ_g(I, G, S) :-
    pow_list(I, G, Ps),
    summ_g_aux(I, Ps, [], I, S).       % Ultimo = I (cualquier P es <= I)

summ_g_aux(0, _, Acum, _, S) :-
    reverse(Acum, S).

summ_g_aux(I, Ps, Acum, Ultimo, S) :-
    I > 0,
    member(P, Ps),
    P =< I,
    P =< Ultimo,
    Resto is I - P,
    summ_g_aux(Resto, Ps, [P | Acum], P, S).

% Predicado 2.3

n_summ(I, G, NSum) :-
    findall(S, summ_g(I, G, S), Sol),
    length(Sol, NSum).


%--- Tests ---

% save_graph/1
:- test save_graph(G) : (G = [edge(a,b), edge(b,c)]) 
    => (edge(a,b), edge(b,c)).

:- test save_graph(G) : (G = [edge(a,b), edge(b,a), edge(a,c)]) 
    => (edge(a,b), edge(a,c)).

% triptych/0
:- test triptych : (save_graph([edge(a,b), edge(a,c), edge(a,d)])) 
    + (not_fails).

:- test triptych : (save_graph([edge(a,b), edge(b,c), edge(c,d)])) 
    + (fails).

:- test triptych : (save_graph([edge(a,b), edge(b,c), edge(b,d), edge(d,e)])) 
    + (not_fails).

:- test triptych : (save_graph([edge(a,b), edge(a,c), edge(a,d), edge(b,e), edge(b,f)])) 
    + (fails).

% pow_list/3
:- test pow_list(I, G, Ps) : (I = 7, G = 2) 
    => (Ps = [4,2,1]).

:- test pow_list(I, G, Ps) : (I = 10, G = 3) 
    => (Ps = [9,3,1]).

:- test pow_list(I, G, Ps) : (I = 1, G = 5) 
    => (Ps = [1]).

% summ_g/3
:- test summ_g(I, G, S) : (I = 13, G = 2) 
    + (not_fails).

:- test summ_g(I, G, S) : (I = 21, G = 3) 
    + (not_fails).

:- test summ_g(I, G, S) : (I = 100, G = 7) 
    + (not_fails).

% n_summ/3
:- test n_summ(I, G, NSum) : (I = 7, G = 2) 
    => (NSum = 6).

:- test n_summ(I, G, NSum) : (I = 1, G = 2) 
    => (NSum = 1).

:- test n_summ(I, G, NSum) : (I = 52, G = 3) 
    => (NSum = 81).