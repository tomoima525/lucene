package lucene;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.icu.ICUNormalizer2Filter;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.ja.JapanesePartOfSpeechStopFilter;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer.Mode;
import org.apache.lucene.util.Version;

public class LuceneTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		WordCount wc=new WordCount();
		
		//String str="これはある精神病院の患者、――第二十三号がだれにでもしゃべる話である。彼はもう三十を越しているであろう。が、一見したところはいかにも若々しい狂人である。狂人だった。狂人すぎる。狂人なのか。";
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(new FileInputStream("/home/tomoaki/workspace/owntwit.log")));			
			
			wc.WordFreqFile(buffer);
			//wc.WordFreqString(str);
			//wc.WordTokenize(buffer);
				
		}catch(IOException e){
			e.printStackTrace();
		}
		
		}

}
