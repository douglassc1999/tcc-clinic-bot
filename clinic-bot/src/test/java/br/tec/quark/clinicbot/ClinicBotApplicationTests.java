package br.tec.quark.clinicbot;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ClinicBotApplicationTests {

	@Test
	void contextLoads() throws IOException {
//		SimpleTokenizer tokenizer = new SimpleTokenizer();;
//		String tokens[] = tokenizer.tokenize("Douglas de Souza Carvalho.");
//		System.out.println(tokens);


		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		String[] tokens = tokenizer
				.tokenize("John is 26 years old. His best friend's "
						+ "name is Leonard. He has a sister named Penny.");

		InputStream inputStreamNameFinder = getClass()
				.getResourceAsStream("/models/en-ner-person.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(
				inputStreamNameFinder);
		NameFinderME nameFinderME = new NameFinderME(model);
		List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

		Assertions.assertThat(spans.toString())
				.isEqualTo("[[0..1) person, [13..14) person, [20..21) person]");
	}

}
