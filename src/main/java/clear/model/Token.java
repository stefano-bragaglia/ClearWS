package clear.model;

/**
 * TODO Add some meaningful class description...
 */
public class Token {

	private int start;

	private int end;

	private int index;

	private String text;

	private String posTag;

	private String chunkTag;

	private String namedTag;

	private String lemma;

	public Token(int start, int end, int index, String text, String posTag, String chunkTag, String namedTag, String
			lemma) {
		this.start = start;
		this.end = end;
		this.index = index;
		this.text = text;
		this.posTag = posTag;
		this.chunkTag = chunkTag;
		this.namedTag = namedTag;
		this.lemma = lemma;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	public String getPosTag() {
		return posTag;
	}

	public String getChunkTag() {
		return chunkTag;
	}

	public String getNamedTag() {
		return namedTag;
	}

	public String getLemma() {
		return lemma;
	}

	@Override
	public String toString() {
		return "Token{" +
				"start=" + start +
				", end=" + end +
				", index=" + index +
				", text='" + text + '\'' +
				", posTag='" + posTag + '\'' +
				", chunkTag='" + chunkTag + '\'' +
				", namedTag='" + namedTag + '\'' +
				", lemma='" + lemma + '\'' +
				'}';
	}
}
