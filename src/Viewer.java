import java.util.ArrayList;
import java.io.IOException;

public class Viewer {

    private static ArrayList<Integer> list = new ArrayList<>();
    private static Image[] images;
    private static int current = 0;
    private static int digit = -1;
    private static Gui gui;


    public static Image next() {
        if (list.size() > 0) {
            if (current < list.size()) {
                return images[list.get(current++)];
            }

        } else if (digit >= 0) {
            while (current < images.length) {
                if (images[current].digit() == digit) {
                    return images[current++];
                }
                current++;
            }

        } else if (current < images.length) {
            return images[current++];
        }

        return null;
    }


    public static Image prev() {
        if (list.size() > 0) {
            if (current > 1) {
                current -= 2;
                return images[list.get(current++)];
            }

        } else if (digit >= 0) {
            if (current > 1) current -= 2;
            while (current > 0) {
                if (images[current].digit() == digit) {
                    return images[current++];
                }
                current--;
            };

        } else if (current > 1) {
            current -= 2;
            return images[current++];
        }

        return null;
    }


    public static int checkValue(String arg, int lo, int hi) {
        int value = Integer.parseInt(arg);
        if (value < 0 || value > hi) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static void main(String[] args) {
        String imageFileName = "train-images.idx3-ubyte";
        String labelFileName = "train-labels.idx1-ubyte";
        int threshold = 0;
        int pixels = 10;
        int errors = 0;
        String option = "";

        for (String arg : args) {
            try {
                switch (option) {
                    case "-images":
                        imageFileName = arg;
                        option = "";
                        continue;

                    case "-labels":
                        labelFileName = arg;
                        option = "";
                        continue;

                    case "-digit":
                        digit = checkValue(arg, 0, 9);
                        option = "";
                        continue;

                    case "-pixels":
                        pixels = checkValue(arg, 0, 25);
                        option = "";
                        continue;

                    case "-threshold":
                        threshold = checkValue(arg, 0, 255);
                        option = "";
                        continue;
                }
            } catch (IllegalArgumentException e) {
                System.err.print("Invalid value for " + option + ": " + arg);
                option = "";
                errors++;
                continue;
            }

            switch (arg) {
                case "-test":
                    imageFileName = "t10k-images.idx3-ubyte";
                    labelFileName = "t10k-labels.idx1-ubyte";
                    continue;

                case "-train":
                    imageFileName = "train-images.idx3-ubyte";
                    labelFileName = "train-labels.idx1-ubyte";
                    continue;

                case "-images":
                case "-labels":
                case "-digit":
                case "-pixels":
                case "-threshold":
                    option = arg;
                    continue;

                default:
                    if (arg.charAt(0) == '-') {
                        System.err.println("Invalid option: " + arg);
                        errors++;
                        continue;
                    }
            }

            try {
                int index = checkValue(arg, 0, Integer.MAX_VALUE);
                list.add(index);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid image index: " + arg);
                errors++;
                continue;
            }
        }

        if (errors > 0) return;

        try {
            Reader reader = new Reader(imageFileName, labelFileName);
            images = reader.read();
        } catch (IOException e) {
            System.err.println("Could not read dataset: " + e.getMessage());
            return;
        }

        if (threshold > 0) {
            for (Image image : images) {
                image.reduce(threshold);
            }
        }

        Canvas canvas = new Canvas(28, 28);
        gui = new Gui(canvas, pixels);
        gui.draw(next());
    }
}
