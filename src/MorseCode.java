import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.sound.sampled.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MorseCode extends JFrame {
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton playButton;
    private JRadioButton textToMorse, morseToText;
    private HashMap<Character, String> morseMap = new HashMap<>();
    private HashMap<String, Character> reverseMorseMap = new HashMap<>();

    public MorseCode() {
        setTitle("Morse Code Translator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Input Field
        inputField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(inputField, gbc);

        // Radio Buttons
        textToMorse = new JRadioButton("Text to Morse", true);
        morseToText = new JRadioButton("Morse to Text");
        ButtonGroup group = new ButtonGroup();
        group.add(textToMorse);
        group.add(morseToText);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(textToMorse, gbc);

        gbc.gridx = 1;
        add(morseToText, gbc);

        // Output Area
        outputArea = new JTextArea(5, 30);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Play Button
        playButton = new JButton("Play Morse Sound");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(playButton, gbc);

        initializeMorseMap();

        // **Use DocumentListener instead of KeyListener**
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { convertText(); }
            @Override
            public void removeUpdate(DocumentEvent e) { convertText(); }
            @Override
            public void changedUpdate(DocumentEvent e) { convertText(); }
        });

        // Play Morse Sound when button is clicked
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playMorseSound(outputArea.getText());
            }
        });

        setVisible(true);
    }

    private void initializeMorseMap() {
        String[][] morseArray = {
                {"A", ".-"}, {"B", "-..."}, {"C", "-.-."}, {"D", "-.."},
                {"E", "."}, {"F", "..-."}, {"G", "--."}, {"H", "...."},
                {"I", ".."}, {"J", ".---"}, {"K", "-.-"}, {"L", ".-.."},
                {"M", "--"}, {"N", "-."}, {"O", "---"}, {"P", ".--."},
                {"Q", "--.-"}, {"R", ".-."}, {"S", "..."}, {"T", "-"},
                {"U", "..-"}, {"V", "...-"}, {"W", ".--"}, {"X", "-..-"},
                {"Y", "-.--"}, {"Z", "--.."}, {"0", "-----"}, {"1", ".----"},
                {"2", "..---"}, {"3", "...--"}, {"4", "....-"}, {"5", "....."},
                {"6", "-...."}, {"7", "--..."}, {"8", "---.."}, {"9", "----."},
                {" ", "   "} // 3 spaces for word separation
        };

        for (String[] pair : morseArray) {
            morseMap.put(pair[0].charAt(0), pair[1]);
            reverseMorseMap.put(pair[1], pair[0].charAt(0));
        }
    }

    private void convertText() {
        String input = inputField.getText().trim();
        String output = textToMorse.isSelected() ? convertToMorse(input) : convertToText(input);
        outputArea.setText(output);
    }

    private String convertToMorse(String text) {
        StringBuilder morseCode = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (morseMap.containsKey(c)) {
                morseCode.append(morseMap.get(c)).append(" ");
            }
        }
        return morseCode.toString().trim();
    }

    private String convertToText(String morse) {
        StringBuilder text = new StringBuilder();
        String[] morseWords = morse.split("   "); // 3 spaces between words

        for (String word : morseWords) {
            for (String letter : word.split(" ")) {
                if (reverseMorseMap.containsKey(letter)) {
                    text.append(reverseMorseMap.get(letter));
                }
            }
            text.append(" ");
        }
        return text.toString().trim();
    }

    // Play Morse Code Sound
    private void playMorseSound(String morseCode) {
        new Thread(() -> {
            for (char c : morseCode.toCharArray()) {
                if (c == '.') {
                    beep(200, 1000); // Short beep
                } else if (c == '-') {
                    beep(500, 1000); // Long beep
                }
                try {
                    Thread.sleep(200); // Pause between sounds
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    // Beep function with frequency control
    private void beep(int duration, int frequency) {
        try {
            float sampleRate = 44100;
            byte[] buffer = new byte[(int) (duration * (sampleRate / 1000))];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) (Math.sin(2 * Math.PI * frequency * i / sampleRate) * 127);
            }

            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MorseCode();
    }
}