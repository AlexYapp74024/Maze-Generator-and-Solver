
:- object(board(_Width_, _Height_)).

	:- use_module(library(clpfd)).
	:- use_module(library(lists)).
	:- use_module(library(apply)).
	:- use_module(library(aggregate)).

	:- public(index_xy/3).
	index_xy(Index, X, Y) :-                              
		Width1 #= _Width_ - 1,
		Index #= _Width_ * Y + X,
		X in 0..Width1.

	:- public([width/1, height/1]).
		width(_Width_).
		height(_Height_).

	:- public(neighbours/2).
	neighbours(p(X,Y), Neighbours) :-
		abs(X - X1) #=< 1, 
		abs(Y - Y1) #=< 1, 
		H1 #= _Height_ - 1,
		W1 #= _Width_ - 1,
		Y1 in 0..H1, 
		X1 in 0..W1,
		findall(X1, label([X1]), Xs),
		findall(Y1, label([Y1]), Ys),
		findall(p(X_,Y_), (
			member(X_,Xs), member(Y_,Ys),
			abs((X - X_) + (Y - Y_)) #= 1
		),Neighbours).
		
	neighbours(p(I), Neighbours) :-
		::index_xy(I, X, Y),
		neighbours(p(X,Y), N),
		findall(I_,(
			member(p(X_,Y_),N),
			::index_xy(I_, X_, Y_)
		),Neighbours).

	:- public([moves/1, up/2, down/2, left/2, right/2]).
	moves([up,down,left,right]).
	
	% 2d Cords and 1d index
	up(p(X0,Y0), p(X0,Y1)) :-
		Y1 #= Y0 + 1,
		in_board(X0, Y1).
	up(p(I0), p(I1)) :-
		::index_xy(I0, X0, Y0),
		::index_xy(I1, X1, Y1),
		up(p(X0,Y0), p(X1,Y1)).

	down(p(X0,Y0), p(X0,Y1)) :-
		Y1 #= Y0 - 1,
		in_board(X0, Y1).
	down(p(I0), p(I1)) :-
		::index_xy(I0, X0, Y0),
		::index_xy(I1, X1, Y1),
		down(p(X0,Y0), p(X1,Y1)).

	left(p(X0,Y0), p(X1,Y0)) :-
		X1 #= X0 - 1,
		in_board(X1, Y0).
	left(p(I0), p(I1)) :-
		::index_xy(I0, X0, Y0),
		::index_xy(I1, X1, Y1),
		left(p(X0,Y0), p(X1,Y1)).

	right(p(X0,Y0), p(X1,Y0)) :-
		X1 #= X0 + 1,
		in_board(X1, Y0).
	right(p(I0), p(I1)) :-
		::index_xy(I0, X0, Y0),
		::index_xy(I1, X1, Y1),
		right(p(X0,Y0), p(X1,Y1)).

	:- public([in_board/2]).
	in_board(X, Y) :- 
		H1 #= _Height_ - 1,
		W1 #= _Width_ - 1,
		Y in 0..H1, 
		X in 0..W1,
		label([X,Y]).

:- end_object.
