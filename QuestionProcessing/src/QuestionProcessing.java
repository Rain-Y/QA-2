import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.semgraph.SemanticGraph;
//import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
//import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/*import edu.stanford.nlp.ling.CoreAnnotations.ChineseSegAnnotation;
 import edu.stanford.nlp.ling.CoreAnnotations.ChineseOrigSegAnnotation;
 import edu.stanford.nlp.ling.CoreAnnotations.ChineseIsSegmentedAnnotation;
 import edu.stanford.nlp.ling.CoreAnnotations.ChineseCharAnnotation;*/

public class QuestionProcessing {
	private static StanfordCoreNLP pipeline;
	private static Properties props;

	/**
	 * setProperties: set the properties of the pipeline
	 */
	public static void setProperties() {
		props = new Properties();

		props.setProperty("customAnnotatorClass.segment",
				"edu.stanford.nlp.pipeline.ChineseSegmenterAnnotator");

		props.setProperty("pos.model",
				"edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
		props.setProperty("parse.model",
				"edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz");

		props.setProperty("segment.model",
				"edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
		props.setProperty("segment.serDictionary",
				"edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
		props.setProperty("segment.sighanCorporaDict",
				"edu/stanford/nlp/models/segmenter/chinese");
		props.setProperty("segment.sighanPostProcessing", "true");

		props.setProperty("ssplit.boundaryTokenRegex", "[.]|[!?]+|[。]|[！？]+");

		props.setProperty("ner.model",
				"edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz");
		props.setProperty("ner.applyNumericClassifiers", "false");
		props.setProperty("ner.useSUTime", "false");

		// props.put("annotators",
		// "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		// props.put("annotators",
		// "segment, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("annotators", "segment, ssplit, pos, ner, parse");
	}

	/**
	 * Process: To get the key words and analyze the question type.
	 * 
	 * @param text
	 *            The question String
	 */
	public static void Process(String text) {
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as
		// keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		String taggedFileName = "tagged.txt";
		String taggedFilePath = "src";
		File taggedFile = new File(taggedFilePath, taggedFileName);
		try {
			//BufferedWriter fout = new BufferedWriter(new FileWriter(taggedFile));
			//有BUG！！！每写一行都会覆盖前一行，输出结果只有最后一行！
			BufferedWriter fout = new BufferedWriter(new FileWriter(taggedFile, true));
			//可以把所有结果写回，但每次执行都在文件尾继续写，不合适！
			// System.out.println("word\tpos\tlemma\tner");
			System.out.println("word\tpos\tner");
			for (CoreMap sentence : sentences) {
				// traversing the words in the current sentence
				// a CoreLabel is a CoreMap with additional
				// token-specific methods
				String taggedLine = "";
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					// this is the text of the token
					String word = token.get(TextAnnotation.class);
					// this is the POS tag of the token
					String pos = token.get(PartOfSpeechAnnotation.class);
					// this is the NER label of the token
					String ne = token.get(NamedEntityTagAnnotation.class);
					// String lemma = token.get(LemmaAnnotation.class);

					// System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
					System.out.println(word + "\t" + pos + "\t" + ne);
					
					taggedLine = taggedLine + word + ":" + pos + ":" + ne + " ";
					//fout.write(word + ":" + pos + ":" + ne + " ");
					//fout.flush();
				}
				System.out.println(taggedLine);
				fout.write(taggedLine);
				fout.newLine();
				//fout.write("\r\n");  //NOTE: UNIX should be "\n"
				fout.flush();
				//fout.newLine();
				//fout.flush();
				// this is the parse tree of the current sentence
				Tree tree = sentence.get(TreeAnnotation.class);
				// tree.pennPrint();

				// this is the Stanford dependency graph of the current
				// sentence
				// /SemanticGraph dependencies =
				// sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			}
			fout.close();
			// This is the coreference link graph
			// Each chain stores a set of mentions that link to each
			// other,
			// along with a method for getting the most representative
			// mention
			// Both sentence and token offsets start at 1!
			// /Map<Integer, CorefChain> graph =
			// document.get(CorefChainAnnotation.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		setProperties();
		pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text;
		// text =
		// "Stanford University is located in California. It is a great university.";
		// text = "阿根廷国家足球队赢得过多少次美洲杯冠军？";
		// text = "1916年至1927年，北京大学的校长是？";

		String questionDir = "src";
		String questionName = "question.txt";
		File questionFile = new File(questionDir, questionName);
		if (!questionFile.exists()) {
			System.out.println("question file doesn't exist!");
		} else {
			BufferedReader din;
			try {
				din = new BufferedReader(new FileReader(questionFile));
				text = din.readLine();
				while (text != null) {
					String[] qaStr = text.split("\t");
					// System.out.println(text);
					System.out.println(qaStr[0]);
					System.out.println(qaStr[1]);
					Process(qaStr[0]);
					text = din.readLine();
				}
				din.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}