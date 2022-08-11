:- use_module(library(clpfd)).

:- initialization(
	logtalk_load([
		roots(loader),
		tutor(loader),
		debugger(loader),
		maze,
		board
	])
).