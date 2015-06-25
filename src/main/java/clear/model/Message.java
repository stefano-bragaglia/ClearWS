package clear.model;

import java.util.List;

/**
 * TODO Add some meaningful class description...
 */
public class Message {

	private final long id;

	private final List<Sentence> sentences;

	public Message(final long id, final List<Sentence> sentences) {
		this.id = id;
		this.sentences = sentences;
	}

	public long getId() {
		return id;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", sentences=" + sentences +
				'}';
	}

}
