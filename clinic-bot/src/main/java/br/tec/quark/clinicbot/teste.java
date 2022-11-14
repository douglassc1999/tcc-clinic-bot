package br.tec.quark.clinicbot;

import opennlp.tools.tokenize.SimpleTokenizer;

public class teste {
    public static void main(String[] args) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;;
        String tokens[] = tokenizer.tokenize("Douglas de Souza Carvalho.");
        System.out.println(tokens);
    }
}
