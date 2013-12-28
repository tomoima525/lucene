package lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.icu.ICUNormalizer2Filter;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.ja.JapanesePartOfSpeechStopFilter;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer.Mode;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.omg.CORBA.PUBLIC_MEMBER;


public class WordCount {
	public static final String CONTENT="hoge";
    public static final FieldType TYPE_STORED = new FieldType();

    static {
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        //reader value can not be stored
        //TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }
	public WordCount() {
		// TODO Auto-generated constructor stub
	}
	
	public void WordTokenize(String string) throws IOException{
		Reader reader=new StringReader(string);
		TokenStream stream = new JapaneseTokenizer(reader,null,true,Mode.NORMAL);	
		//stream= new ICUNormalizer2Filter(stream);
		stream= new StopFilter(Version.LUCENE_46, stream, JapaneseAnalyzer.getDefaultStopSet());
		stream = new JapaneseBaseFormFilter(stream);
		stream = new JapanesePartOfSpeechStopFilter(Version.LUCENE_46, stream, JapaneseAnalyzer.getDefaultStopTags());
		stream = new JapaneseKatakanaStemFilter(stream);
		try{
			stream.reset();
			while(stream.incrementToken()){
				//System.out.println("token:"+stream.toString());
				System.out.println("token:"+stream.getAttribute(CharTermAttribute.class));
				
			}	
		}catch(IOException e){
			e.printStackTrace();
			stream.end();
		}finally{
			stream.close();
			reader.close();
		}
		
	}
	
	public void WordFreq(String string) throws IOException{

	Reader reader=new StringReader(string);	
	Analyzer analyzer=new JapaneseAnalyzer(Version.LUCENE_46, null, Mode.NORMAL, JapaneseAnalyzer.getDefaultStopSet(), JapaneseAnalyzer.getDefaultStopTags());
	IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LUCENE_46, analyzer);
	RAMDirectory index=new RAMDirectory();
	//document
	Document doc=new Document();
	
	//make index
	//textfield
	Field textField=new Field(CONTENT, reader, TYPE_STORED);
	//clear index config
	indexWriterConfig.setOpenMode(OpenMode.CREATE);
	//increase the max heap size to the JVM (eg add -Xmx512m or -Xmx1g):
	//indexWriterConfig.setRAMBufferSizeMB(256.0);
	//indexwriter
	IndexWriter indexWriter=new IndexWriter(index, indexWriterConfig);
	try{
	
	doc.add(textField);
	if(indexWriter.getConfig().getOpenMode()==OpenMode.CREATE){
		System.out.println("adding ");
		indexWriter.addDocument(doc);
	}else{
		System.out.println("updating "+textField.readerValue().toString());
		indexWriter.updateDocument(new Term("hoge2"), doc);
	}
	}catch(IOException te){
		te.printStackTrace();
	}
	
	 //Need to close indexWriter http://stackoverflow.com/questions/3802021/no-segments-file-found
	indexWriter.close();
	

	//get frequency
	IndexReader directoryReader=DirectoryReader.open(index);
	//DirectoryReader directoryReader=DirectoryReader.open(index);

	try{

		Map<String, Integer> termMap=getTermFrequencies(directoryReader, 0, CONTENT);
		Map<String, Integer> sortMap=sortTermFrequencies(termMap);

		for(Map.Entry<String, Integer> s:sortMap.entrySet()){
			System.out.println("term:"+s.getKey()+" value:"+s.getValue());
		}
	
		
	}catch(IOException e){
		e.printStackTrace();
	}

	directoryReader.close();
	reader.close();
	
	}
	
	Map<String, Integer> getTermFrequencies(IndexReader indexReader,int docID,String field)throws IOException{
		Terms vector=indexReader.getTermVector(docID, field);
		Map<String, Integer> termFrequencies=new HashMap<>();
		TermsEnum termsEnum=null;
		
		termsEnum=vector.iterator(termsEnum);
		BytesRef text=null;
		while((text=termsEnum.next())!=null){
			String term=text.utf8ToString();
			int freq=(int)termsEnum.totalTermFreq();
			termFrequencies.put(term, freq);
		}
		return termFrequencies;
		
	}
	

	Map<String, Integer> sortTermFrequencies(Map<String, Integer> termMap){
		List<Map.Entry<String, Integer>> entries=new ArrayList<Map.Entry<String,Integer>>(termMap.entrySet());
		Map<String, Integer> result=new HashMap<String, Integer>(); 
		
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>(){
			public int compare(Entry<String, Integer> o1,Entry<String,Integer> o2){

				return((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
			}
		});
		for(Entry<String,Integer> entry:entries){
		result.put(entry.getKey(),entry.getValue());	
		//System.out.println("t:"+entry.getKey()+"val:"+entry.getValue());
		}
		return  result;
		
	}

}
