package clear.model;

import java.util.Objects;

/**
 * A generic implementation of {@see PatternToken}.
 */
public class Word implements PatternToken {

	/**
	 * The {@see String} constant for the asterisk.
	 */
	private static final String ASTERISK = "*";

	/**
	 * The text of the word.
	 */
	private final String text;
	/**
	 * The POS tag of the word.
	 */
	private final String posTag;
	/**
	 * The lemma of the word.
	 */
	private final String lemma;
	/**
	 * The position of the original text where this word starts.
	 */
	private final int start;
	/**
	 * The position of the original text where this word ends.
	 */
	private final int end;

	/**
	 * Default constructor.
	 *
	 * @param text   the text of the word
	 * @param posTag the POS tag of the word
	 * @param lemma  the lemma of the word
	 * @param start  the position of the original text where this word starts
	 * @param end    the position of the original text where this word ends
	 */
	public Word(String text, String posTag, String lemma, int start, int end) {
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
			throw new IllegalArgumentException("'start' is lower than 0: " + start);
		}
		if (end < start) {
			throw new IllegalArgumentException("'end' is lower than 'start' (" + start + "): " + end);
		}

		this.text = text;
		this.posTag = posTag;
		this.lemma = lemma;
		this.start = start;
		this.end = end;
		checkState();
	}

	@Override
	public String getText() {
		checkState();
		return text;
	}

	@Override
	public String getPOSTag() {
		checkState();
		return posTag;
	}

	@Override
	public String getLemma() {
		checkState();
		return lemma;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return text;
	}

	/**
	 * Asserts that this class' internal state is consistent.
	 */
	private void checkState() {
		assert textIsNotNullAndNotEmpty() : "'text' is null or empty: " + text;
		assert posTagIsNotNullAndNotEmpty() : "'posTag' is null or empty: " + posTag;
		assert lemmaIsNotNullAndNotEmpty() : "'lemma' is null or empty: " + lemma;
		assert startIsGreaterOrEqualsToZero() : "'start' is lower than 0: " + start;
		assert endIsGreaterOrEqualsToStart() : "'end' is lower than 'start' (" + start + "): " + end;
	}

	private boolean textIsNotNullAndNotEmpty() {
		return !text.isEmpty();
	}

	private boolean posTagIsNotNullAndNotEmpty() {
		return !posTag.isEmpty();
	}

	private boolean lemmaIsNotNullAndNotEmpty() {
		return !lemma.isEmpty();
	}

	private boolean startIsGreaterOrEqualsToZero() {
		return start >= 0;
	}

	private boolean endIsGreaterOrEqualsToStart() {
		return end >= start;
	}

	@Override
	public boolean match(String posTag, String... lemmas) {
		Objects.requireNonNull(posTag);
		if ((posTag = posTag.trim()).isEmpty()) {
			throw new IllegalArgumentException("'posTag' is empty");
		}
		Objects.requireNonNull(lemmas);

		boolean hasAsterisk = posTag.endsWith(ASTERISK);
		if (hasAsterisk) {
			posTag = posTag.substring(0, posTag.length() - 1);
		}
		if (matchesPosTag(posTag, !hasAsterisk)) {
			if (lemmas.length == 0 || hasLemma(lemmas)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the posTag of this word exactly matches or conversely starts with the specified posTag.
	 *
	 * @param posTag  the posTag to match
	 * @param exactly a flag for exact match or start with
	 * @return {@code true} if the posTag of this word exactly matches or starts with the specified posTag,
	 * {@code false} otherwise
	 */
	private boolean matchesPosTag(String posTag, boolean exactly) {
		String ref = getPOSTag();
		return (exactly && ref.equals(posTag))
				|| (!exactly && ref.startsWith(posTag));
	}

	/**
	 * Checks whether the lemma of this word matches a lemma of the specified list of admissible lemmas.
	 *
	 * @param lemmas the list of admissible lemmas
	 * @return {@code true} if the lemma of this word is included in the specified list of lemmas, {@code false}
	 * otherwise
	 */
	private boolean hasLemma(String[] lemmas) {
		String ref = getLemma();
		for (String lemma : lemmas) {
			if (ref.equalsIgnoreCase(lemma)) {
				return true;
			}
		}
		return false;
	}

}
