package clear;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import clear.model.Phrase;
import clear.model.Tokenizer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	private static final Tokenizer tokenizer = Tokenizer.getInstance();

	private static final String template = "Hello, %s!";

	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/process")
	public List<Phrase> process(@RequestParam(value = "content", defaultValue = "") String content) {

		return tokenizer.process(content);
	}

}
