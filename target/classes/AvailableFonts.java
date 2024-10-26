import java.awt.GraphicsEnvironment;

public class AvailableFonts {

    public static void main(String[] args) {
        // Get the local graphics environment
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Get all available font family names
        String[] fonts = ge.getAvailableFontFamilyNames();

        // Print each font family name
        for (String font : fonts) {
            System.out.println(font);
        }
    }
}
