Prog	Start	50
	Using	*,15
	SR	1,1
	L	1,ONE
	A	1,TWO
	ST	1,RES
	A	2,=F'34'
	LTORG
	L	4,=F'99'
ONE	DC	F'1'
TWO	DC	F'2'
RES	DS	1F
	End
