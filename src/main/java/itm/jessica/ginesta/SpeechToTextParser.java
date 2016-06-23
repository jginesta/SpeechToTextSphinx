package itm.jessica.ginesta;

public class SpeechToTextParser {
	
    public SpeechToTextParser(String word, float accuracy, int startTime, int endTime) {
        this.word = word;
        this.accuracy = accuracy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

		private String word;
	    private float accuracy;
	    private int startTime;
	    private int endTime;	
}
