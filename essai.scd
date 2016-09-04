// =====================================================================
// SuperCollider Workspace
// =====================================================================




z=PlazyEnvirN {arg k, t=4, f=0, set=Pwhite(0,5);
	Pspawn(
		Pbind(
			\key, k,
			\seed, f,
			\temps, t,
			\method, \seq,
			\dur, 0, 
			\pattern, Pfunc{ arg in;
				Pfindur(in.temps,
					Pbind(
						in.key, Pseed(in.seed, set.postln)
					)
				)}
		)
	);
};
a=(
	k:
	(degree: [[0,1].iter.repeat, 4, Pwhite(0,5)],
		dur: [[0,1].iter.repeat, 4, Prand([0.2,0.5,1], inf)]),
);
b=a.k.collect({|v,k|
	v.postln;
	z<>(k:k)<>v.collect({|x, i|
		[\f, \t, \set][i].asArray++x
	}).postln.flat.as(Event)
});
c=Pchain(b.degree, b.dur);
c.trace.play
