

/**
 * Created by sjain on 10/19/16.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class TestAlgorithm {

    public static void main(String[] args) throws ParseException, IOException {

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("files/indexTips/")));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());

        // Get the preprocessed query terms
        String queryString = "Indian curry sambhar rice dal"; //"airbus subsidies";

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("reviews", analyzer);
        Query query = parser.parse(queryString);

        TopScoreDocCollector collector = TopScoreDocCollector.create(10); searcher.search(query, collector);
        ScoreDoc[] docs = collector.topDocs().scoreDocs;
        for (int i = 0; i < docs.length && i<10; i++) {
            Document doc = searcher.doc(docs[i].doc);

            System.out.println(doc.get("category") + " " + (i+1) + " " +docs[i].score);
        }
        reader.close();
    }

}
