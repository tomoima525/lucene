package lucene;

import java.io.IOException;
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

	public LuceneTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		WordCount wc=new WordCount();
		String str="これはある精神病院の患者、――第二十三号がだれにでもしゃべる話である。彼はもう三十を越しているであろう。が、一見したところはいかにも若々しい狂人である。狂人だった。狂人すぎる。狂人なのか。";
		try{
			wc.WordFreq(str);
			//wc.WordTokenize(str);
				
		}catch(IOException e){
			e.printStackTrace();
		}
		
		}

}
