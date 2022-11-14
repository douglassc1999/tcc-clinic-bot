package br.tec.quark.clinicbot;

import opennlp.tools.tokenize.SimpleTokenizer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClinicBotApplicationTests {

	@Test
	void contextLoads() {
		SimpleTokenizer tokenizer = new SimpleTokenizer();;
		String tokens[] = tokenizer.tokenize("Douglas de Souza Carvalho.");
		System.out.println(tokens);
	}

}
