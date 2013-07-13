symbolrank
==========

Symbol-ranking compression implementation in Java.

I wrote this to refresh my Java knowledge when volunteering at my
local community college, since most of the students were learning
Java.

The idea behind symbol ranking compression is that for a "context" of
recently seen symbols in the input, you can try to predict a few
symbols which are likely to occur next. If you're right, you don't
need to encode the symbol itself, you can just encode which of your
guesses it was. If you're wrong, you have to spend some extra storage
to encode the symbol literally, but can then update your prediction
table for that context so that the next time around you're more likely
to make an accurate guess.

Decoding the file works in the same way. Near the start of the file,
the decoder will see a lot of literal encodings, since the compressor
was doing a lot of "learning" at that point about the likelihood of
symbols in different contexts. The decompressor follows that trail of
breadcrumbs, "learning" to make exactly the same predictions that the
compressor did, so that when it sees an encoding for "second best
guess" it can produce the correct symbol.

In this case, symbols are bytes, contexts are three bytes long, and
the cache of likely predictions is also three bytes long for any given
context. That said, the concept of symbol ranking could be applied to
any definition of symbol, such as a word, color, or sound, and any
length of context or cache. I probably should have written this with
generics to support -- maybe interfaces for IContext<TSymbol> and
ICache<TSymbol>.




