package clear.model;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * The ClearNLP tokenizer implemented as a step of a strategy.
 */
public class Tokenizer {

	/**
	 * The array of semantic components to analyse text.
	 */
	private final AbstractComponent[] components;

	/**
	 * The tokenizer to split text into atomic terms.
	 */
	private final AbstractTokenizer tokenizer;

	/**
	 * Private constructor.
	 * Prevents instantiation from other classes.
	 */
	private Tokenizer() {
		List<String> paths = new ArrayList<>();
		paths.add("brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt.xz");
		GlobalLexica.initDistributionalSemanticsWords(paths);
		GlobalLexica.initNamedEntityDictionary("general-en-ner-gazetteer.xz");
		components = new AbstractComponent[4];
		components[0] = NLPUtils.getPOSTagger(TLanguage.ENGLISH, "general-en-pos.xz");
		components[1] = NLPUtils.getMPAnalyzer(TLanguage.ENGLISH);
		components[2] = NLPUtils.getDEPParser(TLanguage.ENGLISH, "general-en-dep.xz", new DEPConfiguration("root"));
		components[3] = NLPUtils.getNERecognizer(TLanguage.ENGLISH, "general-en-ner.xz");
		tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH);
	}

	/**
	 * Returns the singleton instance of the {@code Tokenizer} class.
	 *
	 * @return the singleton instance of the {@code Tokenizer} class
	 */
	public static Tokenizer getInstance() {
		return ClearNLPTokenizerHolder.INSTANCE;
	}

	public List<Phrase> process(String content) {
		Objects.requireNonNull(content);

		// Finding sentences
		int pos = 0;
		List<Phrase> result = new ArrayList<>();
		for (List<String> sentence : tokenizer.segmentize(new ByteArrayInputStream(content.getBytes()))) {
			DEPTree tree = new DEPTree(sentence);
			for (AbstractComponent component : components) {
				component.process(tree);
			}
			// Counting nodes and finding bounds of a sentence
			int size = 0;
			int last = pos;
			int first = -1;
			for (DEPNode node : tree) {
				size += 1;
				String text = node.getWordForm();
				if (first < 0) {
					first = content.indexOf(text, pos);
				}
				last = content.indexOf(text, last) + text.length();
			}
			if (first < 0) {
				first = pos;
			}
			// Converting nodes into a phrase
			int i = 0;
			Phrase phrase = new Phrase(content.substring(first, last), size);
			for (DEPNode node : tree) {
				String text = node.getWordForm();
				int start = content.indexOf(text, pos);
				int end = start + text.length();
				phrase.setWord(i++, text, node.getPOSTag(), node.getLemma(), start, end);
				pos = end;
			}
			result.add(phrase.compress());
		}
		return result;
	}

	/**
	 * Initializes singleton.
	 * <p>
	 * {@code ClearNLPTokenizerHolder} is loaded on the first execution of {@code Tokenizer.getInstance()} or
	 * the first access to {@code ClearNLPTokenizerHolder.INSTANCE}, not before.
	 */
	private static class ClearNLPTokenizerHolder {
		/**
		 * Singleton instance of the {@code Tokenizer} class.
		 */
		private static final Tokenizer INSTANCE = new Tokenizer();
	}

}
