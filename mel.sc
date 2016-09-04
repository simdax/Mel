Mel{
	classvar basePattern;
	*initClass{
		basePattern=
		PlazyEnvirN
		{
			arg k, t=4, f=0, set=Pwhite(0,5);
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
		}
	}
	
	*new{ arg ... kv;
		var b=kv.asDict.collect({|v,k|
			v.postln;
			basePattern<>(k:k)<>v.collect({|x, i|
				// var r=
				[\f, \t, \set][i].asArray++x;
				// this.perform(
				// 	[\checkForme, \checkTemps, \checkSet][i],
				// 	r
				// )
			}).flat.as(Event)
		});
		var durTmp=b[\dur];
		b.removeAt(\dur);
		^Pchain(*(b.values++durTmp));
	}

	*checkForme{
		arg f;
		f.class.postln.switch(
			Array, {^f.iter.repeat},
			String, {^f.convert.iter.repeat},
			{^f}
		)
	}
	*checkTemps{}
	*checkSet{}
	
}