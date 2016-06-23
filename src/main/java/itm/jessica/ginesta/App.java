package itm.jessica.ginesta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.demo.transcriber.TranscriberDemo;
import edu.cmu.sphinx.result.WordResult;

/**
 * Hello world!
 *
 */
public class App 
{
	private static FileReader fr;
	private static BufferedReader br;
	private static StringBuilder sbu;
    private static FileWriter fw = null;
	static List<List<String>> array = new ArrayList<List<String>>();
	static List<SpeechToTextParser> parser = new ArrayList<SpeechToTextParser>();
	
	public static void main(String args[]) throws Exception {
		videoToAudio();
		audioToText();
		readData(); 
		printData();
		writeData();
		writeTranscript();
	}

	public static void videoToAudio() throws IOException, InterruptedException {
		Runtime r = Runtime.getRuntime();
		StringBuilder sbuilder = new StringBuilder(
				//"/usr/local/bin/ffmpeg -i /Users/swipesense/Downloads/569-3_5minute.avi -acodec pcm_s16le -ac 1 -ar 16000 /Users/swipesense/Downloads/test_3.wav");
				"/usr/local/bin/ffmpeg -i /Users/swipesense/Documents/Bluemix/myvideo.mp4 -acodec pcm_s16le -ac 1 -ar 16000 /Users/swipesense/Downloads/ThisisIllinoisTech1.wav");
		Thread.sleep(1000);
		r.exec(sbuilder.toString());
	}
	
	private static void audioToText () throws Exception{

		String outputFilePath ="/Users/swipesense/Downloads/transcriptSphinx.txt";
		SpeechResult speechResult;
		PrintWriter pw = null;

		try {

			System.out.println("Loading models...");

			Configuration configuration = new Configuration();
			configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
			configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
			configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

			StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
			InputStream stream = new FileInputStream("/Users/swipesense/Downloads/ThisisIllinoisTech1.wav");
					//TranscriberDemo.class.getResourceAsStream("/Users/swipesense/Downloads/test_2.wav");
			stream.skip(44);

			recognizer.startRecognition(stream);

			try {
				pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath, false)));
				while ((speechResult = recognizer.getResult()) != null) {
					System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
					for (WordResult res : speechResult.getWords()) {
						if (res.toString().contains("<sil>")) {
							System.out.println(" ");
							pw.println(" ");
						} else {
							System.out.print(res);
							pw.print(res);
						}
					}

					System.out.println("Best hypothesis:");
					for (String s : speechResult.getNbest(1)) {
						System.out.println(s);

						// Get individual words and their times.
//					for (WordResult r : speechResult.getWords()) {
//						System.out.println(r);
//					}	
						 
					
					}
				}
			} catch (IOException ex) {

			} finally {
				if (pw != null) {
					pw.close();
					pw.flush();
				}
			}

			recognizer.stopRecognition();
			
			try {

				File finalText = new File("/Users/swipesense/Downloads/transcriptSphinx.txt");
				fr = new FileReader(finalText);
				br = new BufferedReader(fr);
				sbu = new StringBuilder();

				String line;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
					sbu.append(line);
					sbu.append("\n");
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != fr) {
						fr.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			System.out.println(sbu.toString());

		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}
	
	public static void readData() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File("/Users/swipesense/Downloads/transcriptSphinx.txt")));

			String line;
			
			while ((line = br.readLine()) != null) {
				String tempWord = null;
				float tempAccuracy = 0;
				int tempStartTime = 0;
				int tempEndTime=0;
				SpeechToTextParser tempObj = null;
				
				line = line.replace('[', ' ');
				line = line.replace(']', ' ');
				line = line.replace('}', ','); 
				line = line.replace('{', ' '); 
				line = line.replaceAll("\\s+","");
				
				
			    String[] tokenize = line.split(",");

	            for (int i = 0; i < tokenize.length; i++) {
	                if ((i % 3) == 0) {
	                	  tempWord = tokenize[i];
	                }
	                else if ((i % 3) == 1) {  
	                	  tempAccuracy = Float.parseFloat(tokenize[i]);
	                }
	                else if ((i % 3) == 2) {
	                	  String[] time = tokenize[i].split(":");
	                	  tempStartTime = Integer.parseInt(time[0]);
	                	  tempEndTime = Integer.parseInt(time[1]);
	                	  tempObj = new SpeechToTextParser(tempWord, tempAccuracy, tempStartTime, tempEndTime);  
	                	  parser.add(tempObj);
	                }

	              }
	           
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}
    
	}
	
	
	public static void printData() {
		 System.out.format("%12s%15s%15s%15s","Word","Accuracy","StartTime","EndTime");
		System.out.println(parser.size());

	      for (SpeechToTextParser eachWord : parser)
	        {
	         
	            System.out.format("%12s%15.2f%15d%15d", eachWord.getWord(),eachWord.getAccuracy(), eachWord.getEndTime(), eachWord.getEndTime() );
	            System.out.print("\n");
	        }

	}
	
	public static void writeData() {
	try {
		
		fw = new FileWriter("data.txt");
	 	fw.write(String.format("%20s%15s%15s", "Word",  "StartTime", "EndTime"));
	 	fw.write("\n"); 
	     for (SpeechToTextParser eachWord : parser)
	        {
	    	    String formatStr = "%20s%15d%15d";
	    	    fw.write(String.format(formatStr, eachWord.getWord(),eachWord.getStartTime(),eachWord.getEndTime()));
	            fw.write("\n"); 
	        }
	 	fw.close();
	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	}

	public static void writeTranscript() {
	try {
		
		fw = new FileWriter("transcript.txt"); 
	     for (SpeechToTextParser eachWord : parser)
	        {
	    	 	if (eachWord.getWord()== eachWord.getWord().toUpperCase()){
	    	 		
	    	 	}
	    	 	else{
	    	 		fw.write(eachWord.getWord());
	    	 		fw.write(" "); 
	    	 	}
	          
	        }
	 	fw.close();
	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	}
}
