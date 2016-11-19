

/**
 * Created by sjain on 10/19/16.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class QueryTerms {

    public static void main(String[] args) throws ParseException, IOException {


        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("files/indexTips/")));
        IndexSearcher searcher = new IndexSearcher(reader);

//        searcher.setSimilarity(new BM25Similarity());

        // Get the preprocessed query terms
        String queryString = "Indian curry sambhar rice dal";

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("tip", analyzer);
        Query query = parser.parse(queryString);
        // Get the preprocessed query terms
        Set<Term> queryTerms = new LinkedHashSet<Term>();
        searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);

        // Using a map to store values of docid -> score, as it will be most efficient to do a quick lookup on docIds.
        TreeMap<String, ArrayList<ArrayList<String>>> sc = new TreeMap();

        ClassicSimilarity dSimi = new ClassicSimilarity();

        TermsEnum it = MultiFields.getTerms(reader, "tip").iterator();
        BytesRef byteRef = null;

        Terms terms = MultiFields.getTerms(reader, "tip");

        int cntrt = 0;
        while((byteRef = it.next()) != null && cntrt<36501) {
            cntrt++;
            if(cntrt<35000)
                continue;
//            PostingsEnum td = MultiFields.getTermDocsEnum(reader, "tip", it.term());
            BytesRef t = it.term();


//         Iterating over query terms
//        for (Term t : queryTerms) {
//
            int df = reader.docFreq(new Term("tip", t));
            int N = reader.getDocCount("tip");
            System.out.println("DF " + df);
            System.out.println("N " + N);

//            PostingsEnum de = MultiFields.getTermDocsEnum(reader, "tip", new BytesRef(t.text()));

            // Get the segments of the index
            List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
            // Processing each segment

            for (int i = 0; i < leafContexts.size(); i++) {
                // Get document length
                LeafReaderContext leafContext = leafContexts.get(i);
                int startDocNo = leafContext.docBase;

//                PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
//                        "tip", new BytesRef(t.text()));

                PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
                        "tip", t);


                int doc;
                    de.docID();
                    while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {

                        float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
                                .getNormValues("tip").get(de.docID()));

                        float docLeng = 1 / (normDocLeng * normDocLeng);
                        int count = de.freq();
                        int docId = de.docID() + startDocNo;
                        float tf = (float)count / (float) docLeng;
                        double idf = Math.log10(1 + (float)N / (float)df);
                        double fscore = tf * idf;
                        String docNo = searcher.doc(docId).get("category");

                        System.out.println("term " + t.utf8ToString());
                        System.out.println("category " + docNo);
                        System.out.println("docid " + docId);
                        System.out.println("freq " + count);
                        System.out.println("score " + fscore);
                        System.out.println();

                        ArrayList<String> arrayList = new ArrayList();
                        arrayList.add(t.utf8ToString());
                        arrayList.add(String.valueOf(fscore));

                        ArrayList<ArrayList<String>> ar = new ArrayList<>();
                        if (sc.containsKey(docNo)) {
                            ar = sc.get(docNo);
                            ar.add(arrayList);
                            sc.put(docNo, ar);
                        } else{
                            ar.add(arrayList);
                            sc.put(docNo, ar);
                            sc.put(docNo, ar);
                        }
                    }
                }
            }

        HashMap<String, ArrayList<String>> myMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : sc.entrySet()) {
            ArrayList<ArrayList<String>> val = entry.getValue();
            Collections.sort(val, new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                    return o2.get(1).compareTo(o1.get(1));
                }
            });

            ArrayList<String> ar = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                try {
                    ar.add(val.get(i).get(0));
                } catch (Exception e) {}
            }

            myMap.put(entry.getKey(), ar);

        }

        for (Map.Entry<String, ArrayList<String>> entry : myMap.entrySet()) {
            System.out.println(entry);
        }

//        }



//        Analyzer analyzer = new StandardAnalyzer();
//        QueryParser parser = new QueryParser("tip", analyzer);
//        Query query = parser.parse(queryString);
//
//        TopScoreDocCollector collector = TopScoreDocCollector.create(10); searcher.search(query, collector);
//        ScoreDoc[] docs = collector.topDocs().scoreDocs;
//        for (int i = 0; i < docs.length && i<10; i++) {
//            Document doc = searcher.doc(docs[i].doc);
//
//            System.out.println(doc.get("category") + " " + (i+1) + " " +docs[i].score);
//        }
        reader.close();
    }

}
