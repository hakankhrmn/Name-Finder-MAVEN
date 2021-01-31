import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public Main(String url) throws IOException {

        List<String> people = new ArrayList<String>();
        //String url=args[0];

        /** jsoup parse paragraph*/
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String paragraph="";

        Element body = document.body();
        Elements paragraphs1 = body.getElementsByClass("sect1");

        for (Element line : paragraphs1) {
            paragraph+=line.text()+". ";
        }

        /**sentence detection*/
        SentenceModel model=null;
        try (InputStream modelIn = new FileInputStream("src/main/resources/en-sent.bin")) {
            model = new SentenceModel(modelIn);
        }
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String sentences[] = sentenceDetector.sentDetect(paragraph);

        /** tokenizer*/
        InputStream inputStream2 = new FileInputStream("src/main/resources/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStream2);

        TokenizerME tokenizer = new TokenizerME(tokenModel);

        for (String sentence :sentences){
            String tokens[] = tokenizer.tokenize(sentence);

            /** name finder*/

            InputStream inputStreamNameFinder = new
                    FileInputStream("src/main/resources/en-ner-person.bin");
            TokenNameFinderModel model2 = new TokenNameFinderModel(inputStreamNameFinder);


            NameFinderME nameFinder = new NameFinderME(model2);

            Span nameSpans[] = nameFinder.find(tokens);

            String[] spanns = Span.spansToStrings(nameSpans, tokens);
            for (int i = 0; i < spanns.length; i++) {
                people.add(spanns[i]);
            }

        }

        for(int i=0;i<people.size();i++){
            System.out.println(people.get(i));
        }

    }

    public static void main(String[] args) throws IOException {
        new Main(args[0]);

    }
}
