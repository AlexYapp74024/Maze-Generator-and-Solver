:- use_module(library(theme/dark)).
:- use_module(library(logtalk)).
:- use_module(library(dcg/basics)).
:- use_module(library(reif)).
:- use_module(library(lambda)).

:- {loader}.

call_t(Pred, true) :- call(Pred).
call_t(Pred, false) :- not(call(Pred)).

methods([dfs,bfs,a_star]).

height(Maze, Height) :- length(Maze, Height). 
width([First | _Rest], Width) :- length(First, Width). 

solve_maze(_Maze, _Moves, _Start, _End, Method) :- 
    methods(Methods),
    not(member(Method, Methods)),
    format('Invalid Method').

solve_maze(Maze, Path, Start, End, Method) :- 
    height(Maze, Height),
    width(Maze, Width),
    call(Method, Maze, Moves, board(Width, Height, Start, End)),
    flatten(Moves, History),
    trace_history(History, Start, End, Path).


trace_history(History, Start, End, Path) :- 
phrase(trace_history_(History, Start, End),Path).

trace_history_(_History, Start, Start) --> [Start].
trace_history_(History, Start, End) --> {
        member(Last-End, History),
        draw_point(End, 'PATH')
    }, [End],
    trace_history_(History, Start, Last).


neighbours(Board, Current, Maze, Closed, Vaild_Neighbours):-
    Board::neighbours(Current, Neighbours),
    filter_neighbours(Maze, Closed, Neighbours, Vaild_Neighbours).

filter_neighbours(Maze, Closed, Neighbours0, Neighbours1) :- 
    filter_neighbours_(Maze, Closed, Neighbours0, [], Neighbours1).

filter_neighbours_(_Maze, _Closed, [], Acc, Acc).
filter_neighbours_(Maze, Closed, [N| Ns], Acc, Neighbours1) :-
    N = p(C,R),
    nth0(R, Maze, Row),
    nth0(C, Row , Cell),
    if_(call_t((Cell \= w, not(member(N, Closed)))), 
        filter_neighbours_(Maze, Closed, Ns, [N | Acc], Neighbours1),
        filter_neighbours_(Maze, Closed, Ns, Acc, Neighbours1)
    ).

dfs(Maze, Moves, Board) :- 
    Board::start(Start),
    phrase(dfs_(Maze, Board, [Start], []), Moves).

dfs_(_Maze, Board, [Current | _Open], _Closed) --> { Board::end(Current)}.
dfs_(Maze, Board, [Current | Open0], Closed) --> {
        neighbours(Board, Current, Maze, Closed, Neighbours),
        append(Neighbours, Open0, Open1),
        foldl(\L^Ls^Pair^(Pair=[Current-L|Ls]),Neighbours,[],Paths),
        draw_point(Current, 'SEARCH')
    }, [Paths],
    dfs_(Maze, Board, Open1, [Current| Closed]).


bfs(Maze, Moves, Board) :- 
    Board::start(Start),
    phrase(bfs_(Maze, Board, [Start], []), Moves).

bfs_(_Maze, Board, [Current | _Open], _Closed) --> { Board::end(Current)}.
bfs_(Maze, Board, [Current | Open0], Closed) --> {
        neighbours(Board, Current, Maze, Closed, Neighbours),
        append(Open0, Neighbours, Open1),
        foldl(\L^Ls^Pair^(Pair=[Current-L|Ls]),Neighbours,[],Paths),
        draw_point(Current, 'SEARCH')
    }, [Paths],
    bfs_(Maze, Board, Open1, [Current| Closed]).


a_star(Maze, Moves, Board) :-    
    Board::start(Start),
    phrase(a_star_(Maze, Board, [s(Start, 0, 0)], []), Moves).

a_star_(_Maze, Board, [s(P, _D, _H) | _Open], _Closed) --> { Board::end(P)}.
a_star_( Maze, Board, [s(P, D, _H) | Open], Closed) --> {
        Board::end(End),
        neighbours(Board, P, Maze, Closed, Neighbours0),
        % perform distance and heuristic calculation for each neighbours
        foldl(\L^Ls^Pair^((
                D1 #= D + 1,
                distance(End, L, Dist),
                H1 #= Dist + D1,
                Pair = [s(L,D1, H1)|Ls]
                ))
            ,Neighbours0,[],Neighbours1
        ),
        foldl(
            \P0^O0^O1^sorted_insert(
                O0,
                P0,
                \S1^S2^M^((
                    S1 = s(_,_,H1),
                    S2 = s(_,_,H2),
                    if_(call_t(H1<H2), M=S1, M=S2)
                )),
                O1
            ),
            Neighbours1,Open,Open1
        ),
        foldl(\L^Ls^Pair^(Pair=[P-L|Ls]),Neighbours0,[],Paths),
        draw_point(P, 'SEARCH')
    }, [Paths],
    a_star_( Maze, Board, Open1, [P| Closed]).


distance(p(X1,Y1), p(X2,Y2), Distance) :-
    Distance #= abs(X2-X1) + abs(Y2-Y1).

sorted_insert(List0, Elem, Pred_3, List1) :-
    phrase(sorted_insert_(List0, Elem, Pred_3), List1).

sorted_insert_([], Elem, _MinPred_3) --> [Elem].
sorted_insert_([L| Ls], Elem, MinPred_3) --> {
        call(MinPred_3, L,Elem, Min),
        if_( call_t(Min = L), Max = Elem, Max = L)
    }, [Min],
    sorted_insert_(Ls, Max, MinPred_3).


draw_point(p(C,R), Mode) :- output("~w ~w ~w~n",[C,R,Mode]).

output(Format, Args):-
    open('file.txt', append, I),
    format(I, Format, Args),
    close(I).


