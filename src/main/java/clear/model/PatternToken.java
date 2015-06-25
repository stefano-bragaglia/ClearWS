package clear.model;

/**
 * An abstraction for tokens. Each token is a word that consists of some text, its POS tag and the equivalent lemma.
 * In the future, it may contain other features (such as the position within the original text).
 */
public interface PatternToken {

	/**
	 * Returns the text of this word.
	 *
	 * @return the text fo this word
	 */
	String getText();

	/**
	 * Returns the POS tag of this word.
	 *
	 * @return the POS tag of this word
	 */
	String getPOSTag();

	/**
	 * Returns the lemma of this word.
	 *
	 * @return the lemma of this word
	 */
	String getLemma();

	/**
	 * Returns the position of the original text where this word starts.
	 *
	 * @return the position of the original text where this word starts
	 */
	int getStart();

	/**
	 * Returns the position of the original text where this word ends.
	 *
	 * @return the position of the original text where this word ends
	 */
	int getEnd();

	/**
	 * Tries to understand if the current token matches the specified complex pattern.
	 * <p>
	 * The pattern consists of a mandatory {@code posTag} filter and an optional list of {@code lemmas}.
	 * <p>
	 * The {@code posTag} is a string addressing either a specific POS tag (like "JJS", "," or "VBD") or a family of
	 * similar POS tags (like "NN*", which stands for "NN", "NNS", "NNP" and "NNPS"). In the former case, the method
	 * verifies if the {@code posTag} of the token is exactly equals to the filter. In the latter case, the method
	 * checks whether the {@code posTag} of the token starts with the filter amended of the ending asterisk, which is
	 * used to distinguish this case from the other.
	 * <p>
	 * If the {@code posTag} of the token matches the specified POS tag filter and the {@code lemmas} is not empty,
	 * the method asserts that the {@code lemma} of the token is equals to one of the strings in the specified
	 * {@code lemmas} (case insensitive).
	 * <p>
	 * A call to {@code match("CC", "and", "or")}, for instance, checks verifies that the token is either an "and" or
	 * "or" connective. {@code match("JJ*")}, instead, verifies that the token is either a superlative, comparative or
	 * a plain adjective.
	 *
	 * @param posTag the POS tag filter to match
	 * @param lemmas the lemmas to match, if any
	 * @return {@code true} if the token matches the specified complex pattern, {@code false} otherwise
	 */
	boolean match(String posTag, String... lemmas);

}
