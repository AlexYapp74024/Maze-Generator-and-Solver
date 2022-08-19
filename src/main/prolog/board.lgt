
:- object(board(_Width_, _Height_, _Start_, _End_)).

	:- use_module(library(clpfd)).
	:- use_module(library(lists)).
	:- use_module(library(apply)).
	:- use_module(library(aggregate)).

	:- public([width/1, height/1, start/1, end/1]).
		width(_Width_).
		height(_Height_).
		start(_Start_).
		end(_End_).

	:- public(neighbours/2).
	neighbours(Point, Neighbours) :-
		moves(Moves),
		add_neighbour(Neighbours, Point, [], Moves).

	add_neighbour(Neighbours, _Point, Neighbours, []).	
	add_neighbour(Neighbours, Point, Acc, [Move| Moves]) :-
		call(Move, Point, N),
		add_neighbour(Neighbours, Point, [N | Acc], Moves).
	add_neighbour(Neighbours, Point, Acc, [Move| Moves]) :-
		\+(call(Move, Point, _N)),
		add_neighbour(Neighbours, Point, Acc, Moves).
	
	:- public([moves/1, move/3]).
	moves([up,down,left,right]).
	move(Move, P0, P1):-
		moves(Moves),
		member(Move, Moves),
		call(Move, P0, P1).
	
	:- public([up/2, down/2, left/2, right/2]).
	% 2d Cords
	up(p(X0,Y0), p(X0,Y1)) :-
		Y1 #= Y0 + 1,
		in_board(X0, Y1).

	down(p(X0,Y0), p(X0,Y1)) :-
		Y1 #= Y0 - 1,
		in_board(X0, Y1).

	left(p(X0,Y0), p(X1,Y0)) :-
		X1 #= X0 - 1,
		in_board(X1, Y0).

	right(p(X0,Y0), p(X1,Y0)) :-
		X1 #= X0 + 1,
		in_board(X1, Y0).

	in_board(X, Y) :- 
		H1 #= _Height_ - 1,
		W1 #= _Width_ - 1,
		Y in 0..H1, 
		X in 0..W1,
		label([X,Y]).

:- end_object.
