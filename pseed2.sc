Pseed2 : Pseed {
	var <>rep;
	*new{arg seeds, pat, rep;
		^super.new(seeds, pat).rep_(rep)
	}
	embedInStream { arg inval;
		var seedStream, repStream;
		var seed, thread;
		seedStream = randSeed.asStream;
		repStream = rep.asStream;

		while {
			seed = seedStream.next(inval);
			seed.notNil
		}{
			thread = Routine { |inval| repStream.next.do{
				pattern.embedInStream(inval)
			}};
			thread.randSeed = seed;
			inval = thread.embedInStream(inval);
		};
		^inval
	}

}

/*

Pseed2(Pseq([10, 20, 10, 10]), Prand([1,2,4]), Pseq([2,4, 2, 2])).iter.nextN(550)

*/

// allows to reproduce enclosed randomized pattern
// by setting the random seed of the resulting routine

Pseed3 : FilterPattern {
	var <>randSeed;
	*new { arg randSeed, pattern;
		^super.new(pattern).randSeed_(randSeed)
	}
	storeArgs { ^[randSeed,pattern] }

	embedInStream { arg inval;
		var seedStream;
		var seed, ancienneSeed, thread;
		seedStream = randSeed.asStream;
		thread = Routine { |inval| pattern.embedInStream(inval); ^inval };
		seed = seedStream.next(inval);
		thread.randSeed = seed;
		ancienneSeed=seed;
		inval = thread.next(inval).yield(inval);
		while {
			seed = seedStream.next(inval);
			seed.notNil
		}{
			if(seed != ancienneSeed, {
				thread.randSeed = seed;
			});
			ancienneSeed=seed;
			inval = thread.next(inval).yield(inval);
		};
		^inval
	}

}

/*


Pseed3(
	Pstutter(Pseq([10,10], inf), Pseq([12, 13], inf)),
	Prand([1,2,3], inf)
).iter.nextN(50).clumps([10])

*/

// timed pattern

Pseed4 : Pattern {

	var <>list, <>repeats, <>set;
	
	*new{ arg list, set, time;
		^super.new().list_(list).repeats_(time).set_(set)
	}
	embedInStream { arg inval;
		var seedStream, repStr;
		var seed, repVal, thread;
		seedStream = list.asPattern(inf).asStream;
		repStr=repeats.asPattern.asStream;
		//		setStr=set.asStream;
		thread = Routine { |inval|
			//	thisThread.randSeed.postln;
			Prand(set, inf).embedInStream(inval);
			^inval
		};
		while {
			seed = seedStream.next(inval);
			repVal= repStr.next(inval);
			seed.notNil && repVal.notNil
		}{
			thread.randSeed = seed;
			thisThread.endBeat=thisThread.beats+repVal;
			while{thisThread.beats < thisThread.endBeat}
			{inval = thread.next(inval).yield(inval);}
		};
		^inval
	}
	 
}


/*
 
	Pbind(
	\degree , Pseed4(
	Pwalk( 2.collect{100.rand}, Pseq([0,1,-1,0], inf)), 
	[0,1,2],
	2.5
	),
	\dur, Pseq([0.5,1], inf)
	).trace.play

*/

Pseed5 : Pseed4{
	*new{
		arg forme, set=[-1,1], periodes=4, seed=5000.rand;
		var hasard=
		Pseed( seed, Pxrand((..100), inf)).iter.nextN(forme.asSet.size);
		var f=
		Pwalk(
			hasard,
			forme.convert.differentiate.rotate(-1).pseq(inf)
		);
		^super.new(
			f,
			set, 
			periodes
		)
	}
}



/*


a=Pbind(
	\degree, Pseed5("aaba", [0.5,1], 3,0) ,
	\dur, Pseed5("aaba", [0.5,1], 3,0)
).midi.trace.play

*/