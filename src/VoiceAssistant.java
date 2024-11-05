import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.awt.Desktop;
import java.net.URI;
import java.time.LocalTime;

public class VoiceAssistant {

    private LiveSpeechRecognizer recognizer;
    private Voice voice;

    public VoiceAssistant() {
        // Initialize Speech Recognition (CMU Sphinx)
        try {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("src/resources/en-us");
            configuration.setDictionaryPath("src/resources/cmudict-en-us.dict");
            configuration.setLanguageModelPath("src/resources/en-us.lm.bin");

            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Text-to-Speech (FreeTTS)
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");  // 'kevin16' is a default voice in FreeTTS
        if (voice != null) {
            voice.allocate();
        } else {
            System.out.println("Voice not found.");
        }
    }

    public void listenAndRespond() {
        recognizer.startRecognition(true);
        System.out.println("Listening...");

        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            String command = result.getHypothesis();
            System.out.println("You said: " + command);
            processCommand(command);
        }

        recognizer.stopRecognition();
    }

    public void processCommand(String command) {
        if (command.contains("open browser")) {
            openBrowser();
            speak("Opening the browser.");
        } else if (command.contains("time")) {
            tellTime();
        } else {
            speak("I'm not sure how to respond to that.");
        }
    }

    public void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.google.com"));
        } catch (Exception e) {
            e.printStackTrace();
            speak("I couldn't open the browser.");
        }
    }

    public void tellTime() {
        LocalTime time = LocalTime.now();
        String timeString = "The time is " + time.getHour() + ":" + String.format("%02d", time.getMinute());
        System.out.println(timeString);
        speak(timeString);
    }

    public void speak(String text) {
        if (voice != null) {
            voice.speak(text);
        } else {
            System.out.println("Text-to-speech voice not available.");
        }
    }

    public static void main(String[] args) {
        VoiceAssistant assistant = new VoiceAssistant();
        assistant.listenAndRespond();
    }
}
