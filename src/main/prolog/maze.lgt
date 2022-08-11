
:- object(maze,
	instantiates(class),
	specializes(object)
	).

	:- use_module(library(clpfd)).
	:- use_module(library(lists)).
	:- use_module(library(apply)).
	:- use_module(library(lambda)).
	:- use_module(library(aggregate)).

	:- private(edge_/2).

	:- public(edge/2).
	edge(P1, P2) :- ::edge_(P1, P2).
	edge(P1, P2) :- ::edge_(P2, P1).

	:- public(add_edge/2).
	% Dont add if already exists.
	add_edge(P1,P2) :- ::edge(P1, P2).
	add_edge(P1,P2) :- ::assertz(edge_(P1, P2)).

	:- public(remove_edge/2).
	remove_edge(P1,P2) :- ::retract(edge_(P1, P2)).
	remove_edge(P1,P2) :- ::retract(edge_(P2, P1)).

	:- public(remove_all/0).
	remove_all :-
		::retractall(edge_(_,_)).

	:- private(add_edges/1).
	add_edges([]).
	add_edges([e(P1,P2) |T]) :-
		add_edge(P1,P2),
		add_edges(T).

	default_init_option(edges-[]).
	default_init_option(Default) :-
		^^default_init_option(Default).

	process_init_option(edges-Edge) :-
		::add_edges(Edge).
	process_init_option(Option) :-
		^^process_init_option(Option).

:- end_object.
