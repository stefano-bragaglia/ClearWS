package clear;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import clear.model.Message;
import clear.model.Sentence;
import clear.model.Token;
import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.lang.TLanguage;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	private static final AbstractComponent[] components;

	private static final AbstractTokenizer tokenizer;

	private static final Chunker chunker;

	private final AtomicLong counter = new AtomicLong();

	private static Chunker getChunker() {
		Chunker chunker = null;
		InputStream stream = Controller.class.getResourceAsStream("/en-chunker.bin");
		try {
			chunker = new ChunkerME(new ChunkerModel(stream));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return chunker;
	}

	@RequestMapping("/process")
	public Message process(@RequestParam(value = "content", defaultValue = "") String content) {

		AtomicInteger inc = new AtomicInteger(0);
		int pos = 0;
		List<Sentence> sentences = new ArrayList<>();
		for (List<String> phrase : tokenizer.segmentize(new ByteArrayInputStream(content.getBytes()))) {
			DEPTree tree = new DEPTree(phrase);
			for (AbstractComponent component : components) {
				component.process(tree);
			}

			int last = pos;
			int first = -1;
			int size = tree.size();
			String[] texts = new String[size - 1];
			String[] posTags = new String[size - 1];
			String[] chunkTags = new String[size - 1];
			String[] lemmas = new String[size - 1];
			for (int i = 1; i < size; i++) {
				DEPNode node = tree.get(i);
				String text = node.getWordForm();
				if (first < 0) {
					first = content.indexOf(text, pos);
				}
				last = content.indexOf(text, last) + text.length();
				texts[i - 1] = text;
				posTags[i - 1] = node.getPOSTag();
				chunkTags[i - 1] = posTags[i - 1];
				lemmas[i - 1] = node.getLemma();
			}
			for (Span span : chunker.chunkAsSpans(texts, posTags)) {
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					chunkTags[i] = span.getType();
				}
			}
			if (first < 0) {
				first = pos;
			}

			List<Token> tokens = new ArrayList<>();
			for (int i = 0; i < texts.length; i++) {
				int start = content.indexOf(texts[i], pos);
				int end = start + texts[i].length();
				tokens.add(new Token(start, end, inc.getAndIncrement(), texts[i], posTags[i], chunkTags[i], lemmas[i]));
				pos = end;
			}
			sentences.add(new Sentence(first, last, content.substring(first, last), tokens));
		}

		return new Message(counter.incrementAndGet(), sentences);
	}

	static {
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
		chunker = getChunker();
	}
}
