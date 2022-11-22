package br.tec.quark.clinicbot.service;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class OpenNLPService {

    public String contextLoads(String text) throws IOException {

        // Sentence detection
        String[] sents = detectSentences(text);

        // Tokenization
        String[] tokens = tokenize(text);

        // POS Tagging
        String[] posTags = posTag(tokens);

        // Lemmatization
        String[] lemmas = lemmatize(tokens, posTags);

        DoccatModel model = trainsCategorizerModel();

        //Categorizing/Chucking
        return categorize(model, lemmas);
    }

    public static String[] detectSentences(String sentence) throws IOException {

        InputStream input = new FileInputStream("/home/douglas/Área de Trabalho/tcc-clinic-bot/clinic-bot/pt-sent.bin");

        SentenceModel model = new SentenceModel(input);

        SentenceDetectorME detector = new SentenceDetectorME(model);

        String sentences[] = detector.sentDetect(sentence);

        return sentences;
    }

    public static String[] tokenize(String text) throws IOException {

        InputStream input = new FileInputStream("/home/douglas/Área de Trabalho/tcc-clinic-bot/clinic-bot/pt-token.bin");

        TokenizerModel model = new TokenizerModel(input);

        TokenizerME tokenizer = new TokenizerME(model);

        String[] tokens = tokenizer.tokenize(text);

        return tokens;
    }

    public static String[] posTag(String[] tokens) throws IOException {

        InputStream input = new FileInputStream("/home/douglas/Área de Trabalho/tcc-clinic-bot/clinic-bot/pt-pos-maxent.bin");

        POSModel model = new POSModel(input);

        POSTaggerME tagger = new POSTaggerME(model);

        String[] tags = tagger.tag(tokens);

        return tags;
    }

    public static String[] lemmatize(String[] tokens, String[] posTags) throws IOException {

        InputStream input = new FileInputStream("/home/douglas/Área de Trabalho/tcc-clinic-bot/clinic-bot/en-lemmatizer.bin");

        LemmatizerModel model = new LemmatizerModel(input);

        LemmatizerME categorizer = new LemmatizerME(model);

        String[] lemmaTokens = categorizer.lemmatize(tokens, posTags);

        return lemmaTokens;
    }

    public static DoccatModel trainsCategorizerModel() throws IOException {

        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("categorias.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator()} );

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 1);

        DoccatModel model = DocumentCategorizerME.train("pt", sampleStream, params, factory);

        return model;
    }

    public static String categorize(DoccatModel model, String[] tokens) {

        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

        double[] probabilitiesOfOutcomes = myCategorizer.categorize(tokens);

        final var isAllEquals = Arrays.stream(probabilitiesOfOutcomes).distinct().count() == 1;

        if (isAllEquals) {
            return "NAO_IDENTIFICADO";
        }

        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        System.out.println("Category: " + category);

        return category;
    }
}
