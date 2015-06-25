package clear.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import opennlp.tools.util.Span;

/**
 * An abstraction that represents a {@code Phrase} comprising one or more {@PatternToken} as words.
 */
public class Phrase {

	/**
	 * The signature of the common root for proper nouns.
	 */
	private static final String NNP = "NNP";

	/**
	 * The signature for noun parts.
	 */
	private static final String NP = "NP";

	/**
	 * The signature for preposition parts.
	 */
	private static final String PP = "PP";

	/**
	 * The content of the whole sentence to which this {@code Phrase} refers.
	 */
	private final String sentence;

	/**
	 * The size of this {@code Phrase} in {@see PatternToken}s (words).
	 */
	private final int size;

	/**
	 * The array of tokens of this {@code Phrase}.
	 */
	private final String[] tokens;

	/**
	 * The array of POS tags of this {@code Phrase}.
	 */
	private final String[] posTags;

	/**
	 * The array of lemmas of this {@code Phrase}.
	 */
	private final String[] lemmas;

	/**
	 * The array of start indexes of the {@see PatternToken}s (words) in this {@code Phrase}.
	 */
	private final int[] starts;

	/**
	 * The array of end indexes of the {@see PatternToken}s (words) in this {@code Phrase}.
	 */
	private final int[] ends;

	/**
	 * Private constructor for cloning.
	 *
	 * @param sentence the content of the whole sentence to which this {@code Phrase} refers
	 * @param tokens   the array of tokens of this {@code Phrase}
	 * @param posTags  the array of POS tags of this {@code Phrase}
	 * @param lemmas   the array of lemmas of this {@code Phrase}
	 * @param starts   the array of start indexes of the {@see PatternToken}s (words) in this {@code Phrase}
	 * @param ends     the array of end indexes of the {@see PatternToken}s (words) in this {@code Phrase}
	 */
	private Phrase(String sentence, String[] tokens, String[] posTags, String[] lemmas, int[] starts, int[] ends) {
		Objects.requireNonNull(sentence);
		Objects.requireNonNull(tokens);
		Objects.requireNonNull(posTags);
		if (posTags.length != tokens.length) {
			throw new IllegalArgumentException("'posTags' has a different length than 'tokens' (" + tokens.length +
													   "): " + posTags.length);
		}
		Objects.requireNonNull(lemmas);
		if (lemmas.length != tokens.length) {
			throw new IllegalArgumentException("'lemmas' has a different length than 'tokens' (" + tokens.length +
													   "): " + lemmas.length);
		}
		Objects.requireNonNull(starts);
		if (starts.length != tokens.length) {
			throw new IllegalArgumentException("'starts' has a different length than 'tokens' (" + tokens.length +
													   "): " + starts.length);
		}
		Objects.requireNonNull(ends);
		if (ends.length != tokens.length) {
			throw new IllegalArgumentException("'ends' has a different length than 'tokens' (" + tokens.length +
													   "): " + ends.length);
		}

		this.sentence = sentence;
		this.size = tokens.length;
		this.tokens = tokens;
		this.posTags = posTags;
		this.lemmas = lemmas;
		this.starts = starts;
		this.ends = ends;
	}

	/**
	 * Default constructor.
	 * Subsequent calls to {@see setWord(int, String, String, String, int, int)} will fill the arrays of this object.
	 *
	 * @param sentence the content of the whole sentence to which this {@code Phrase} refers
	 * @param size     the size of this {@code Phrase} in {@see PatternToken}s (words)
	 */
	public Phrase(String sentence, int size) {
		Objects.requireNonNull(sentence);
		if (size < 0) {
			throw new IllegalArgumentException("'size' must be greater or equals to zero: " + size);
		}

		this.sentence = sentence;
		this.size = size;
		this.tokens = new String[size];
		this.posTags = new String[size];
		this.lemmas = new String[size];
		this.starts = new int[size];
		this.ends = new int[size];
	}

	/**
	 * Checks whether the specified POS tag refers to a proper noun (singular or plural) or not.
	 *
	 * @param posTag the POS tag to check
	 * @return {@code true} if the specified POS tag refers to a proper noun, {@code false} otherwise
	 */
	private static boolean isProperNoun(String posTag) {
		Objects.requireNonNull(posTag);

		return posTag.startsWith(NNP);
	}

	/**
	 * Checks whether the specified {@see Span} is either a Noun Part ({@code NP}) or a Preposition Part ({@code PP})
	 * or not.
	 *
	 * @param span the {@see Span} to check
	 * @return {@code true} if the specified {@see Span} is either a {@code NP} or {@code PP}, {@code false} otherwise
	 */
	private static boolean isNounOrPrepositionPart(Span span) {
		Objects.requireNonNull(span);

		final String type = span.getType();
		return type.equals(NP) || type.equals(PP);
	}

	/**
	 * Checks whether the specified array of {@code spans} has another {@see Span} after the specified position
	 * {@code p} and if such {@see Span} is contiguous to the {@code p-th} one or not.
	 *
	 * @param spans the {@code spans} to check
	 * @param p     the position in the {@code spans} to check
	 * @return {@code true} if the {@code spans} has another {@see Span} after the position {@code p} and if such
	 * {@see Span} is contiguous to the {@code p-th} one, {@code false} otherwise
	 */
	private static boolean isNextSpanConsecutiveIfAny(Span[] spans, int p) {
		Objects.requireNonNull(spans);
		if (p < 0 || p >= spans.length) {
			throw new IllegalArgumentException("'p' is out of bounds [0," + spans.length + "): " + p);
		}

		return p < spans.length - 1 && spans[p].getEnd() == spans[p + 1].getStart();
	}

	/**
	 * Checks whether the {@code p-th} {@see Span} is the last {@see Span} of the specified {@code spans}.
	 *
	 * @param spans the {@code spans} to check
	 * @param p     the position in the {@code spans} to check
	 * @return {@code true} if the specified {@code spans} has another {@see Span} after the specified position
	 * {@code p}, {@code false} otherwise
	 */
	private static boolean hasNext(Span[] spans, int p) {
		Objects.requireNonNull(spans);
		if (p < 0 || p >= spans.length) {
			throw new IllegalArgumentException("'p' is out of bounds [0," + spans.length + "): " + p);
		}

		return p < spans.length;
	}

	/**
	 * Returns the content of the whole sentence to which this {@code Phrase} refers.
	 *
	 * @return the content of the whole sentence to which this {@code Phrase} refers
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * Returns the list of {@see PatternToken}s in this {@code phrase} whose indexes are between the {@code start}
	 * (inclusive) and {@code end} (exclusive).
	 *
	 * @param start the {@code start} index (inclusive) of the {@see PatternToken} of the {@code phrase} to return
	 * @param end   the {@code start} index (exclusive) of the {@see PatternToken} of the {@code phrase} to return
	 * @return the list of {@see PatternToken}s in this {@code phrase} whose indexes are between the {@code start}
	 * (inclusive) and {@code end} (exclusive)
	 */
	public List<PatternToken> getPatternTokens(int start, int end) {
		if (start < 0 || start > size) {
			throw new IllegalArgumentException("'start' must be in [0,size:" + size + "): " + start);
		}
		if (end < start || end > size) {
			throw new IllegalArgumentException("'end' must be in [start:" + start + ",size:" + size + "): " + end);
		}

		List<PatternToken> result = new ArrayList<>();
		for (int i = start; i < end; i++) {
			result.add(new Word(tokens[i], posTags[i], lemmas[i], starts[i], ends[i]));
		}
		return result;
	}

	/**
	 * Returns the array of tokens of this {@code Phrase}.
	 *
	 * @return the array of tokens of this {@code Phrase}
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Returns the array of POS tags of this {@code Phrase}.
	 *
	 * @return the array of POS tags of this {@code Phrase}
	 */
	public String[] getPosTags() {
		return posTags;
	}

	/**
	 * Returns the size of this {@code Phrase} in {@see PatternToken}s (words).
	 *
	 * @return the size of this {@code Phrase} in {@see PatternToken}s (words)
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the {@code pos-th} element of the object's arrays to the specified {@code text}, {@code posTag},
	 * {@code lemma}, {@code start} and {@code end}.
	 *
	 * @param pos    the position to set
	 * @param text   the text of the {@code pos-th} element
	 * @param posTag the posTag of the {@code pos-th} element
	 * @param lemma  the lemma of the {@code pos-th} element
	 * @param start  the start index of the {@code pos-th} element
	 * @param end    the end index of the {@code pos-th} element
	 */
	public void setWord(int pos, String text, String posTag, String lemma, int start, int end) {
		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException("'pos' is not in [0," + size + "): " + pos);
		}
		Objects.requireNonNull(text);
		if ((text = text.trim()).isEmpty()) {
			throw new IllegalArgumentException("'text' is empty");
		}
		Objects.requireNonNull(posTag);
		if ((posTag = posTag.trim()).isEmpty()) {
			throw new IllegalArgumentException("'posTag' is empty");
		}
		Objects.requireNonNull(lemma);
		if ((lemma = lemma.trim()).isEmpty()) {
			throw new IllegalArgumentException("'lemma' is empty");
		}
		if (start < 0) {
			throw new IllegalArgumentException("'start' must be greater or equals to zero: " + start);
		}
		if (end < start) {
			throw new IllegalArgumentException("'end' must be greater or equals to 'start' (" + start + "): " + end);
		}

		this.tokens[pos] = text;
		this.posTags[pos] = posTag;
		this.lemmas[pos] = lemma;
		this.starts[pos] = start;
		this.ends[pos] = end;
	}

	/**
	 * Iterates over the {@see PatternToken}s in this {@code phrase} merging together any consecutive proper noun.
	 * For instance, any sequence like ['Basel'_NNP, 'Accords'_NNPS] becomes ['Basel Accords'_NNPS].
	 * The compressed list of {@see PatternToken}s is eventually returned as a new {@see Phrase}.
	 *
	 * @return the {@see Phrase} containing the list of {@see PatternToken}s in this {phrase} with merged proper nouns
	 */
	public Phrase compress() {
		String[] tokens = Arrays.copyOf(this.tokens, size);
		String[] posTags = Arrays.copyOf(this.posTags, size);
		String[] lemmas = Arrays.copyOf(this.lemmas, size);
		int[] starts = Arrays.copyOf(this.starts, size);
		int[] ends = Arrays.copyOf(this.ends, size);

		int r = 0;
		int w = 0;
		while (r < size) {
			String text = tokens[r];
			String posTag = posTags[r];
			String lemma = lemmas[r];
			int start = starts[r];
			int end = ends[r];
			r += 1;

			if (isProperNoun(posTag)) {
				while (r < size && isProperNoun(posTags[r])) {
					text += " " + tokens[r];
					posTag = posTags[r];
					lemma += " " + lemmas[r];
					end = ends[r];
					r += 1;
				}
			}

			if (w < r - 1) {
				tokens[w] = text;
				posTags[w] = posTag;
				lemmas[w] = lemma;
				starts[w] = start;
				ends[w] = end;
			}
			w += 1;
		}

		if (w < size) {
			tokens = Arrays.copyOf(tokens, w);
			posTags = Arrays.copyOf(posTags, w);
			lemmas = Arrays.copyOf(lemmas, w);
			starts = Arrays.copyOf(starts, w);
			ends = Arrays.copyOf(ends, w);
		}

		return new Phrase(sentence, tokens, posTags, lemmas, starts, ends);
	}

}
