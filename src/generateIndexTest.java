import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//import java.io.IOException;

/**
 * Created by sjain on 9/27/16.
 */
public class generateIndexTest {

    public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, JSONException, ParseException {

        //Set below three variables docDir, indexDir, analyzer according to the analyzer to be used and path settings.
        Path indexDir = Paths.get("files/indexTest/");
//        Analyzer analyzer = new StandardAnalyzer();
        CustomAnalyzer analyzer = CustomAnalyzer.builder(Paths.get("files")).withTokenizer("standard").addTokenFilter("standard").addTokenFilter("lowercase").addTokenFilter("stop", "ignoreCase", "false").addTokenFilter("keepWord","words","/Users/sjain/keepwords.txt","ignoreCase","false").addTokenFilter("PorterStem").build();

        Directory fsDir = FSDirectory.open(indexDir);

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(fsDir, iwc);

        HashMap<String, String> map = new HashMap<>(); //JsonParsing.tips();
        map.put("Indian", "fantastic egg bhurji at Taj Mahal, egg bhurji is speciality");
        map.put("Subway", "best sandwitch. Try egg sandwitch");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String category = entry.getKey();
            String tips = entry.getValue();

            System.out.println(tips);
            System.out.println(tips.split(" ").length);
            System.out.println();

            for (String word: tips.split(" ")) {
                //Create a new document
                Document d = new Document();

                //Write all the fields to the Document
                d.add(new StringField("word", word, Field.Store.YES));
                d.add(new StringField("category", category, Field.Store.YES));

                //Write the document into indexWriter
                indexWriter.addDocument(d);
            }
        }

        //Print number of documents in the generated index
        int numDocs = indexWriter.numDocs();
        System.out.println(numDocs);

        indexWriter.forceMerge(1);
        indexWriter.commit();
        indexWriter.close();

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("files/indexTest/")));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());

        QueryParser parser = new QueryParser("word", analyzer);
        String queryString = "i like bhurji";
        Query query = parser.parse(queryString);

//        TopScoreDocCollector collector = TopScoreDocCollector.create(10);
        TopDocs td1 = searcher.search(query, 2);

        ScoreDoc[] d1 = td1.scoreDocs;

//        ScoreDoc[] docs = collector.topDocs().scoreDocs;
        for (int i = 0; i < d1.length && i<10; i++) {
            Document doc = searcher.doc(d1[i].doc);

            System.out.println(doc.get("word") + " " + (i+1) + " " + d1[i].score);
        }

    }

//    private static String tokenizeStopStem(String input) throws IOException {
//
//        StringBuilder result = new StringBuilder();
//        Analyzer analyzer = new PorterStemAnalyzer();
//        TokenStream tokenStream = analyzer.tokenStream(input, new StringReader(keywords));
//
//        while(tokenStream.incrementToken()) {
//
//            result.append(tokenStream.getAttribute(TermAttribute.class).term());
//        }
//
//    }
}
