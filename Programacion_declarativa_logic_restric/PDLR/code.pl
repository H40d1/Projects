:- module(code,_,[pure,assertions,regtypes]).

author_data('Lin','Sun','Haodi','230255').

%Probando librería lpdoc

:- doc(title, "Práctica Autómatas Celulares Reversibles").
:- doc(author, "Lin Sun, Haodi").
:- doc(module, "Se quiere implementar un autómata celular para cintas de células blancas y negras.").


:- doc(cells/3, "cells(Estado, Regla, Salida)
         
         Aplica un paso de evolución al autómata celular.
         
         'Estado' es la lista que representa el estado actual.
         Debe empezar y terminar con el símbolo 'o'.
         
         'Regla' estará basada en la función rule(), de aridad 7
         
         'Salida' es el estado resultante con dos células más.").

:- doc(analizador/3, "analizador(Lista, Regla, Salida)
         
         Procesa la lista de tres en tres. Se aplicará la regla correspondiente a cada tripleta para generar la nueva lista.").

:- doc(lista/1, "lista(L)
         
         Verifica que L es una lista.").

:- doc(append/3, "append(Lista1, Lista2, Lista3)
         
         Concatena dos listas, y se devolverá el resultado en Lista3").

:- doc(color/1, "color(C)
         Dada por el enunciado:
         Se define el color dependiendo de C (Si es 'x' o 'o')").

:- doc(rule/5, "rule(Izquierda, Centro, Derecha, Reglas, Nuevo)
         Dada por el enunciado:
         Consulta el color resultante para la configuración de tres células.").

:- doc(evol/3, "evol(N, RuleSet, Cells)
         Dado una regla RuleSet, se evoluciona mediante función cells() el estado [o,x,o] las veces que sean N, y el resultado se almacena en Cells").
         
:- doc(aux_evol/4, "aux_evol(N, RuleSet, EstadoInicial, Cells)
         Función auxiliar de evol(). 
         Almacena el EstadoInicial [o,x,o] y devolverá el resultado en Cells en función de ese mismo estado y la regla RuleSet").

:- doc(steps/2, "steps(Cells, N)
        Función auxiliar a implementar para ruleset()
        Indica el número de pasos necesarios N para llegar a Cells a partir de
        una configuración de tres células").

%---Fin Lpdoc-------


%Ejercicio 1

%Dado por el enunciado
color(o).
color(x).
rule(o,o,o,_,o). % regla nula
rule(x,o,o,r(A,_,_,_,_,_,_),A) :- color(A).
rule(o,x,o,r(_,B,_,_,_,_,_),B) :- color(B).
rule(o,o,x,r(_,_,C,_,_,_,_),C) :- color(C).
rule(x,o,x,r(_,_,_,D,_,_,_),D) :- color(D).
rule(x,x,o,r(_,_,_,_,E,_,_),E) :- color(E).
rule(o,x,x,r(_,_,_,_,_,F,_),F) :- color(F).
rule(x,x,x,r(_,_,_,_,_,_,G),G) :- color(G).
%Fin dado por enunciado ---

is_ruleset(r(A,B,C,D,E,F,G)) :-
    color(A), color(B), color(C), color(D), color(E), color(F), color(G).

lista([]).
lista([_X|Y]) :- lista(Y).


append([], Ys,Ys) :- lista(Ys).
append([X|Xs], Ys, [X|Zs]) :- append(Xs, Ys, Zs).

% Primero, se tienen en cuenta las condiciones para las células: Estado con 'o' a la izquierda y derecha
% Además, las células tienen 'o' en ambos extremos. Queremos expandir, por lo que el resultado final debe tener 2 'o' más. 
% Empezamos añadiendo 2 'o' en cada extremo, para después de haber analizado de 3 en 3, nos queden justo 2 más que el estado que teníamos original.



cells(Estado, Regla, Salida) :- Estado = [o|_], append(_, [o], Estado), append([o,o], Estado, P1), append(P1, [o,o], Analisis),
    analizador(Analisis, Regla, Salida).

%Aquí, quiero que el analizador sólo dé solución cuando haya tres elementos mínimo para el estado de la célula.
analizador([], _, []).
analizador([_], _, []).
analizador([_, _], _, []). %Con dos elementos para el estado, no se pueden formar más salidas.


% 'El' de elemento perteneciente al estado, podremos saber cuál serán los primeros elementos de nuestra lista.
% El ColorP obtenido se pondrá en primer lugar de solución: Lista Resto2
% Se moverá de tres en tres, poniendo [El2, El3 | Resto] como siguiente lista de estados a analizar para darnos siguiente elemento de la célula.

analizador([El1, El2, El3 | Resto], Regla, [ColorP | Resto2]) :- rule(El1, El2, El3, Regla, ColorP), analizador([El2, El3 | Resto], Regla, Resto2).

%% Predicado 2

evol(N, RuleSet, Cells) :- 
    aux_evol(N, RuleSet, [o,x,o], Cells). %Queremos guardar valor inicial

aux_evol(0, _, Cells, Cells). %Si N es 0, no se hace nada, y se devuelve Cells

%Y si no es 0
aux_evol(s(N), RuleSet, Cells, Cola):-
    cells(Cells, RuleSet, Resultado),
    aux_evol(N, RuleSet, Resultado, Cola).


%% Predicado 3

steps([o,_,o], 0).

% El número N dependerá de cuál sea la longitud de dicho estado
steps([_, _|Resto], s(N)) :-
    steps(Resto, N).

ruleset(r(_,_,_,_,_,_,_), [o,x,o]).

%Con steps(), acotamos el espacio de búsqueda. Al calcular el número de pasos, 
% vamos a confirmar de que existe tal estado
ruleset(RuleSet, Cells) :-
    steps(Cells, N), 
    is_ruleset(RuleSet),
    evol(N, RuleSet, Cells).
%% Conjunto de tests

%%% Test Predicado 1

:- test cells(Estado, Regla, Salida)
    : (Estado = [o,x,x,o], Regla = r(o,x,o,x,o,x,o))
    => (Salida = [o,o,x,o,o,o]).

:- test cells(Estado, Regla, Salida)
    : (Estado = [o,o,o], Regla = r(x,x,x,x,x,x,x))
    => (Salida = [o,o,o,o,o]).

:- test cells(Estado, Regla, Salida)
    : (Estado = [o,x,o,o,o], Regla = r(o,x,x,x,x,x,x))
    => (Salida = [o,x,x,o,o,o,o]).

%%% Test Predicado 2

:- test evol(N, RuleSet, Cells)
    : (N = 0, RuleSet = r(x,x,x,o,o,x,o))
    => (Cells = [o,x,o]).

:- test evol(N, RuleSet, Cells)
    : (N = s(0), RuleSet = r(x,x,x,o,o,x,o))
    => (Cells = [o,x,x,x,o]).


%%% Test Predicado 3

:- test steps(Cells, N)
    : (Cells = [o,x,o])
    => (N = 0).

:- test steps(Cells, N)
    : (Cells = [o,x,x,x,o])
    => (N = s(0)).

:- test steps(Cells, N)
    : (Cells = [o,x,x,o,o,o,o])
    => (N =  s(s(0))).

:- test ruleset(RuleSet, Cells)
    : (Cells = [o,x,x,o,o,x,o,o,o,o,x,o,o,x,o])
    => (RuleSet = r(x,x,x,o,o,x,o)).

:- test ruleset(RuleSet, Cells)
    : (Cells = [o,o,x,x,o])
    => (RuleSet = r(x,x,o,x,o,o,x)).

:- test ruleset(RuleSet, Cells)
    : (Cells = [o,x,x,o,o,x,o,o,o,o,x])
    + fails.

:- test ruleset(RuleSet, Cells)
    : (Cells = [o,x,x,o,o,x,o,o,o,o,x,o,x,x,o])
    + fails.

