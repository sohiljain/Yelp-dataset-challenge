import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
public class generateIndex {

    public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, JSONException {

        //Set below three variables docDir, indexDir, analyzer according to the analyzer to be used and path settings.
        Path indexDir = Paths.get("files/indexTips/");
        Analyzer analyzer = new StandardAnalyzer();

        Directory fsDir = FSDirectory.open(indexDir);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(fsDir, iwc);

        HashMap<String, String> map = JsonParsing.tips();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String category = entry.getKey();
            String tips = entry.getValue();

            //Create a new document
            Document d = new Document();

            //Write all the fields to the Document
            d.add(new StringField("category", category, Field.Store.YES));
            d.add(new TextField("tip", tips, Field.Store.YES));

            //Write the document into indexWriter
            indexWriter.addDocument(d);

        }

        //Print number of documents in the generated index
        int numDocs = indexWriter.numDocs();
        System.out.println(numDocs);

        indexWriter.forceMerge(1);
        indexWriter.commit();
        indexWriter.close();

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
