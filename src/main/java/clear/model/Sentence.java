package clear.model;

import java.util.List;

/**
 * TODO Add some meaningful class description...
 */
public class Sentence {

	private int start;

	private int end;

	private String content;

	private List<Token> tokens;

	private int size;

	public Sentence(int start, int end, String content, List<Token> tokens) {
		this.content = content;
		this.tokens = tokens;
		this.size = tokens.size();
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getContent() {
		return content;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public int getSize() {
		return size;
	}
}
