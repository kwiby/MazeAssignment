// -=-  IMPORTS  -=-
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;


// -=-  MAIN CLASS  -=-
public class MazeAssignment {
    // -=- GLOBAL VARIABLES -=-
    // -=-  Start & Exit Positions  -=-
    static int startPositionCol, startPositionRow;
    static int exitPositionCol, exitPositionRow;

    // -=- Used Maze Characters -=-
    /*
     * Index 0 --> Barrier
     * Index 1 --> Open Path
     * Index 2 --> Start
     * Index 3 --> Exit
     * Index 4 --> Exit Path
     */
    static char[] usedMazeCharacters = {'B', 'O', 'S', 'X', '+'}; // This char array will be used to store the used characters in the maze.

   
    // -=-  MAIN METHOD  -=-
    public static void main(String[] args) throws IOException {
        new GuiFrame();
    }


    // -=-  AUXILIARY METHODS  -=-
    /**
     * This method generates a completely random maze and returns it.
     * 
     * @param numOfRows, numOfCols 2 integer variables that stores the number of rows/columns.
     * @return char[][] The completely random maze
     */
    public static char[][] generateCompletelyRandomMaze(int numOfRows, int numOfCols) {
        resetMazeCharactersToDefault();

        char[][] maze = generateRandomMazeWithNoExit(numOfRows, numOfCols);

        generateCompletelyRandomExit(maze);

        return maze;
    }


    /**
     * This method generates a random valid maze and returns it.
     * 
     * @param numOfRows, numOfCols 2 integer variables that stores the number of rows/columns.
     * @return char[][] The random valid maze
     */
    public static char[][] generateValidMaze(int numOfRows, int numOfCols) {
        resetMazeCharactersToDefault();

        char[][] maze = generateRandomMazeWithNoExit(numOfRows, numOfCols);

        generateValidExit(maze);

        return maze;
    }


    /**
     * This method will generate and return a maze by reading from a file.
     * 
     * @return A 2D character array that represents the read from file maze.
     * @throws IOException.
     */
    public static char[][] generateMazeFromFile(File file) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));

        // Reading the dimensions of the maze from file, and creating the 2D array that will be returned.
        int numOfRows = Integer.parseInt(fileReader.readLine());
        int numOfColumns = Integer.parseInt(fileReader.readLine());

        char[][] maze = new char[numOfRows][numOfColumns];

        // Reading and setting the new maze characters from the file.
        char newBarrierChar = fileReader.readLine().charAt(0);
        char newOpenPathChar = fileReader.readLine().charAt(0);
        char newStartChar = fileReader.readLine().charAt(0);
        char newExitChar = fileReader.readLine().charAt(0);

        setNewMazeCharacters(newBarrierChar, newOpenPathChar, newStartChar, newExitChar);

        // Reading each row and column from the maze in the file, and setting the characters in the 2D array to the respective characters.
        for (int row = 0; row < numOfRows; row++) {
            String line = fileReader.readLine();

            for (int col = 0; col < numOfColumns; col++) {
                maze[row][col] = line.charAt(col);

                if (maze[row][col] == usedMazeCharacters[2]) { // If the just set character is the start.
                    startPositionCol = col;
                    startPositionRow = row;
                } else if (maze[row][col] == usedMazeCharacters[3]) { // If the just set character is the exit.
                    exitPositionCol = col;
                    exitPositionRow = row;
                }
            }
        }

        fileReader.close();

        return maze;
    }


    /**
     * This method randomly creates and sets the position of a valid OR invalid exit.
     * 
     * @param maze A 2D char array that represents to maze.
     * @return void
     */
    public static void generateRandomExit(char[][] maze) {
        while (true) {
            int[] randomPositionOnBorder = getPositionOnBorder(maze);

            int rowIndex = randomPositionOnBorder[0]; // Index 0 is the row
            int colIndex = randomPositionOnBorder[1]; // Indes 1 is the column

            if (!( // If NOT the following:
                ((rowIndex == 0) && (colIndex == 0)) || // Position is at TOP LEFT corner.
                ((rowIndex == maze.length - 1) && (colIndex == 0)) || // Position is at BOTTOM LEFT corner.
                ((rowIndex == 0) && (colIndex == maze[0].length - 1)) || // Position is at TOP RIGHT corner.
                ((rowIndex == maze.length - 1) && (colIndex == maze[0].length - 1)) // Position is at BOTTOM RIGHT corner.
            )) {
                exitPositionCol = colIndex;
                exitPositionRow = rowIndex;
                    
                break;
            }
        }
    }


    /**
     * This method randomly creates and sets the position of the exit (could be invalid exit).
     * 
     * @param maze
     * @return void
     */
    public static void generateCompletelyRandomExit(char[][] maze) {
        generateRandomExit(maze);
        maze[exitPositionRow][exitPositionCol] = usedMazeCharacters[3]; // Set the completely random position along the border as the exit.
    }


    /**
     * This method randomly creates and sets the position of a VALID exit.
     * 
     * @param maze
     * @return void
     */
    public static void generateValidExit(char[][] maze) {
        while (true) {
            generateRandomExit(maze);

            int rowIndex = exitPositionRow;
            int colIndex = exitPositionCol;

            if ( // rowIndex = 0, colIndex = 1
                ((colIndex == maze[0].length - 1) && ((maze[rowIndex][colIndex - 1] == usedMazeCharacters[1]) || 
                                        (maze[rowIndex][colIndex - 1] == usedMazeCharacters[2]))) || // If position is on right border and left is an open path or start.
                ((colIndex == 0) && ((maze[rowIndex][colIndex + 1] == usedMazeCharacters[1]) || 
                                                        (maze[rowIndex][colIndex + 1] == usedMazeCharacters[2]))) || // If position is on left border and right is an open path or start.
                ((rowIndex == maze.length - 1) && ((maze[rowIndex - 1][colIndex] == usedMazeCharacters[1]) || 
                                        (maze[rowIndex - 1][colIndex] == usedMazeCharacters[2]))) || // If position is on bottom border and above is an open path or start.
                ((rowIndex == 0) && ((maze[rowIndex + 1][colIndex] == usedMazeCharacters[1]) || 
                                                    (maze[rowIndex + 1][colIndex] == usedMazeCharacters[2]))) // If position is on top border and below is an open path or start.
            ) {
                maze[rowIndex][colIndex] = usedMazeCharacters[3]; // Set the valid random position along the border as the exit.

                exitPositionCol = colIndex;
                exitPositionRow = rowIndex;
                
                break;
            }
        }
    }


    /**
     * This method generates and returns a random maze WITHOUT an exit positon set.
     * 
     * @param numOfRows, numOfCols 2 integer variables that stores the number of rows/columns.
     * @return char[][] The random maze without an exit position set.
     */
    public static char[][] generateRandomMazeWithNoExit(int numOfRows, int numOfCols) {
        char[][] maze = new char[numOfRows][numOfCols];
        fillMazeWithBarriers(maze);

        int randomStartRow = getIndexWithinBorder(maze.length);
        int randomStartCol = getIndexWithinBorder(maze[0].length);
    
        generateMazeBase(maze, randomStartRow, randomStartCol);
        maze[randomStartRow][randomStartCol] = usedMazeCharacters[2];

        startPositionCol = randomStartCol;
        startPositionRow = randomStartRow;

        return maze;
    }


    /**
     * This method will fill a given maze with all barriers.
     *
     * @param A 2D character array that holds the characters of a maze.
     * @return void
     */
    public static void fillMazeWithBarriers(char[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                maze[i][j] = usedMazeCharacters[0];
            }
        }
    }


    /**
     * This method will set the new maze characters in the global "usedMazeCharacters" 2D char array (used when generating mazes from file reading).
     * 
     * @param newBarrierChar, newOpenPathChar, newStartChar, newExitChar 4 character parameters that will be used to set the new maze characters.
     * @return void
     */
    public static void setNewMazeCharacters(char newBarrierChar, char newOpenPathChar, char newStartChar, char newExitChar) {
        usedMazeCharacters[0] = newBarrierChar;
        usedMazeCharacters[1] = newOpenPathChar;
        usedMazeCharacters[2] = newStartChar;
        usedMazeCharacters[3] = newExitChar;
    }


    /**
     * This method will reset the maze characters in the global "usedMazeCharacters" 2D char array to their default values.
     * 
     * @return void
     */
    public static void resetMazeCharactersToDefault() {
        usedMazeCharacters[0] = 'B';
        usedMazeCharacters[1] = 'O';
        usedMazeCharacters[2] = 'S';
        usedMazeCharacters[3] = 'X';
    }

   
    /**
     * This method will randomly generate a maze without a start or exit position. These 2 positions will be randomly generated in a different method.
     *
     * @return A 2D character array that holds the characters of a maze.
     * @param numOfRows, numOfColumns, rowIndex, colIndex 4 integers that help with generating a random maze.
     */
    public static void generateMazeBase(char[][] maze, int rowIndex, int colIndex) {
        int numOfRows = maze.length;
        int numOfCols = maze[0].length;

        int[][] directions = { // The possible directions that the pathways can branch out into.
            {-2, 0}, // Up (up is negative because as you go up, the indexes approach 0/decrease)
            {2, 0}, // Down (down is positive because as you go down, the indexes approach the max number of rows/increase)
            {0, -2}, // Left
            {0, 2} // Right
        };
        Collections.shuffle(Arrays.asList(directions)); // Randomly shuffles the order of the directions array, such that we can randomly branch out in different directions.

        for (int[] direction : directions) { // Iterates through each possible direction randomly and checks the position 2 indexes away from the current position, in said direction.
            int nextRow = rowIndex + direction[0]; // The ROW index of the position 2 indexes away from the current.
            int nextCol = colIndex + direction[1]; // The COLUMN index of the position 2 indexes away from the current.
            int midRow = rowIndex + direction[0] / 2; // The ROW index of the position 1 index away from the current.
            int midCol = colIndex + direction[1] / 2; // The COLUMN index of the position 1 index away from the current.

            if (isWithinBorders(true, nextRow, nextCol, numOfRows, numOfCols) && maze[nextRow][nextCol] == usedMazeCharacters[0]) { // Checking if the position 2 away from the current
                                                                                                                                           // is within the borders of the maze, and is a barrier.
                maze[midRow][midCol] = usedMazeCharacters[1]; // Sets the position 1 index away from the current to an open pathway.
                maze[nextRow][nextCol] = usedMazeCharacters[1]; // Sets the position 2 indexes away from the current to an open pathway.
                generateMazeBase(maze, nextRow, nextCol); // Recursively calls this method to check the position 2 indexes away from the current.
            } else if (isWithinBorders(false, nextRow, nextCol, numOfRows, numOfCols) && maze[nextRow][nextCol] == usedMazeCharacters[0]) {
                if (Math.random() > 0.4) { // This is not needed, however to make maze look better, it's used to set the positions beside the border to an 
                                            // open path 60% of the time (this is needed for when the size of the maze is an even value, due to how we check 
                                            // 2 positions away instead of 1 position away).
                    maze[midRow][midCol] = usedMazeCharacters[1];
                }
            }
        }
    }

   
    /**
     * This method will check if the given row and column index is within the borders of the maze.
     *
     * @param option, rowIndex, colIndex A boolean that is based on how far the program is away from the border, 2 ints being the row and column indexes, and
     *                                   another 2 ints that are the number of rows/columns.
     * @return boolean A boolean that tells the program whether the given row and column indexes produce a position that are within the borders of the maze.
     */
    public static boolean isWithinBorders(boolean option, int rowIndex, int colIndex, int numOfRows, int numOfCols) {
        if (option) { // If 'option' is TRUE, then the next position (2 away from current) is NOT a border.
            return (rowIndex > 0) && (rowIndex < numOfRows - 1) && (colIndex > 0) && (colIndex < numOfCols - 1); // Returns true if within borders, and false if not.
        } else { // If 'option' is FALSE, then the next position (2 away from current) IS a border.
            return (rowIndex >= 0) && (rowIndex <= numOfRows - 1) && (colIndex >= 0) && (colIndex <= numOfCols - 1); // Returns true if within borders, and false if not.
        }
    }


    /**
     * This method will generate a random row or column index within the borders of the maze.
     *
     * @param maze A 2D integer array that stores the maze.
     * @return void
     */
    public static int getIndexWithinBorder(int length) {
        return (int) (Math.random() * (length - 2) + 1);
    }

   
    /**
     * This method will generate a random exit position along the border of the maze.
     *
     * @param maze A 2D integer array that stores the maze.
     * @return void
     */
    public static int[] getPositionOnBorder(char[][] maze) {
        if ((int) Math.round(Math.random()) == 0) { // If 0, then the position will be along a row.
            int randomColIndex = (int) (Math.random() * maze[0].length); // A random column index that will be used to set the potential exit position.
            
            if ((int) Math.round(Math.random()) == 0) { // If 0, then the position will be along row index 0;
                return new int[] {0, randomColIndex};
            } else { // If 1, then the position will be along row index maze.length - 1;
                return new int[] {maze.length - 1, randomColIndex};
            }
        } else { // If 1, then the position will be along a column.
            int randomRowIndex = (int) (Math.random() * maze.length); // A random row index that will be used to set the potential exit position.
            
            if ((int) Math.round(Math.random()) == 0) { // If 0, then the position will be along column index 0;
                return new int[] {randomRowIndex, 0};
            } else { // If 1, then the position will be along column index maze[0].length - 1;
                return new int[] {randomRowIndex, maze[0].length - 1};
            }
        }
    }


    /**
     * This method calls upon the DFS method to solve the maze and updates the visual maze in the gui frame to show the shortest path to the exit.
     * 
     * @param maze A 2d char array of the maze.
     * @return boolean Returns whether or not the maze is solveable.
     */
    public static boolean solveMaze(char[][] maze) {
        // Initialize variables to track info
        List<Integer> shortestPathX = new ArrayList<>();
        List<Integer> shortestPathY = new ArrayList<>();
        List<Integer> currentPathX = new ArrayList<>();
        List<Integer> currentPathY = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        
        // Start DFS from the start position
        solveMazeDFS(maze, visited, shortestPathX, shortestPathY, currentPathX, currentPathY, startPositionCol, startPositionRow);
        
        if (!shortestPathX.isEmpty()) { // If the shortest path arraylists are NOT empty
            // Creates a copy of the original maze that would include the solved pathways marked with a '+'
            GuiFrame.solvedMaze = new char[maze.length][maze[0].length];
            for (int i = 0; i < maze.length; i++) {
                GuiFrame.solvedMaze[i] = maze[i].clone();
            }

            // Mark the shortest pathways with '+'
            for (int i = 0; i < shortestPathX.size(); i++) {
                int x = shortestPathX.get(i);
                int y = shortestPathY.get(i);

                // This if statement is to make sure the program won't overwrite the start or exit positions
                if (GuiFrame.solvedMaze[y][x] != usedMazeCharacters[2] && GuiFrame.solvedMaze[y][x] != usedMazeCharacters[3]) {
                    GuiFrame.solvedMaze[y][x] = usedMazeCharacters[4]; // Sets the current pathway tile to '+'
                }
            }

            return true; // If there is a path saved in the shortestPath arraylists, meaning there IS a valid path to the exit, then return true.
        }

        return false; // If there is no path in saved in the shortestPath arraylists, meaning there is NOT a valid path to the exit, then return false.
    }

    
    /**
     * This method is the recursive DFS implementation utilized to solve the maze.
     * 
     * @param maze, visited, shortestPathX, shortestPathY, currentPathX, currentPathY, currentX, currentY 8 values required to solve the maze with depth-first-search.
     * @return void
     */
    private static void solveMazeDFS(char[][] maze, boolean[][] visited, List<Integer> shortestPathX, List<Integer> shortestPathY, List<Integer> currentPathX, List<Integer> currentPathY, int currentX, int currentY) {
        // Mark the current position temporarily
        visited[currentY][currentX] = true;
        currentPathX.add(currentX);
        currentPathY.add(currentY);
    
        // Check if the exit has been reached
        if (currentX == exitPositionCol && currentY == exitPositionRow) {
            if (currentPathX.size() < shortestPathX.size() || shortestPathX.isEmpty()) {
                shortestPathX.clear();
                shortestPathY.clear();
                shortestPathX.addAll(currentPathX);
                shortestPathY.addAll(currentPathY);
            }

            // Backtrack
            currentPathX.remove(currentPathX.size() - 1);
            currentPathY.remove(currentPathY.size() - 1);
            visited[currentY][currentX] = false; // Unmark when leaving
            return;
        }
    
        // Explore neighbors in consistent order (helps with visualization)
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}}; // Right, Down, Left, Up
        for (int[] dir : directions) {
            int newX = currentX + dir[0];
            int newY = currentY + dir[1];
            
            // Condition to check if we can move to a neighbouring tile
            if (newX >= 0 && newX < maze[0].length && newY >= 0 && newY < maze.length && !visited[newY][newX] && (maze[newY][newX] == usedMazeCharacters[1] || maze[newY][newX] == usedMazeCharacters[3])) { 
                solveMazeDFS(maze, visited, shortestPathX, shortestPathY, currentPathX, currentPathY, newX, newY); // Recursively call on the next tile
            }
        }
    
        // Backtrack - Unmark current position
        currentPathX.remove(currentPathX.size() - 1);
        currentPathY.remove(currentPathY.size() - 1);
        visited[currentY][currentX] = false; // Crucial: allow other paths through this node
    }
}


// -=-  GUI CLASS  -=-
class GuiFrame extends JFrame implements ActionListener {
    // -=-  GLOBAL VARIABLES  -=-
    // -=-  Variables Required For Maze Generation  -=-
    static char[][] maze;
    static char[][] solvedMaze;
    final int maxHeight = 30;
    final int maxWidth = 100;
    static File file = new File("");
    static int numOfRows = 0;
    static int numOfCols = 0;
    static final int tileSize = 15;

    // -=- Constant Colour Values
    static final Color backgroundColour = new Color(25, 25, 30);
    static final Color buttonColour = new Color(100, 100, 130);
    static final Color textColour = new Color(230, 230, 250);
    static final Color errorMsgColour = new Color(200, 70, 70);

    static final Color barrierTileColour = new Color(90, 70, 60);
    static final Color openPathTileColour = new Color(180, 160, 80);
    static final Color startTileColour = new Color(130, 130, 130);
    static final Color exitTileColour = new Color(90, 120, 80);
    static final Color exitPathTileColour = new Color(60, 80, 140);

    // -=-  Component Initializations  -=-
    static JPanel mainPanel = new JPanel();
    JLabel title = new JLabel("MAZE ASSIGNMENT");
    JLabel authors = new JLabel("By: Moxin Guo & Victor Kwong");
    JPanel buttonsPanel = new JPanel();
    JPanel inputFieldsPanel = new JPanel();
    JLabel widthInputLabel = new JLabel("Width(3-" + maxWidth + "):");
    JTextField widthInputField = new JTextField(3);
    JLabel heightInputLabel = new JLabel("Height(3-" + maxHeight + "):");
    JTextField heightInputField = new JTextField(3);
    JButton browseFilesButton = new JButton("Select Maze Text File");
    static JLabel errorMsg = new JLabel("No messages!");
    static JPanel mazeVisualPanel = new JPanel();


    // -=-  GuiFrame Constructor  -=-
    public GuiFrame() { // All GUI components are set up and added here.
        // GUI Frame Settings
        setTitle("Maze Assignment (By: Moxin Guo & Victor Kwong)");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Main Panel (Vertical Axis)
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColour);
        add(mainPanel);

        // Padding above title
        mainPanel.add(Box.createVerticalStrut(20));

        // Title Label
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Monospaced", Font.BOLD, 50));
        title.setForeground(textColour);
        mainPanel.add(title);

        // Authors Label
        authors.setAlignmentX(Component.CENTER_ALIGNMENT);
        authors.setFont(new Font("Monospaced", Font.ITALIC, 20));
        authors.setForeground(textColour);
        mainPanel.add(authors);

        // Padding below title
        mainPanel.add(Box.createVerticalStrut(15));


        // Buttons Panel (Horizontal Axis)
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.setBackground(backgroundColour);
        buttonsPanel.setMaximumSize(new Dimension(1000, 40));
        mainPanel.add(buttonsPanel);

        // "Generate Completely Random Maze" Button
        createButton(buttonsPanel, "Generate Completely Random Maze");

        // Padding between "Generate Completely Random Maze" button and "Generate Random Valid Maze" button.
        addHorizontalButtonPadding(buttonsPanel);

        // "Generate Random Valid Maze" Button
        createButton(buttonsPanel, "Generate Random Valid Maze");

        // Padding between "Generate Random Valid Maze" button and "Generate Maze From File" button.
        addHorizontalButtonPadding(buttonsPanel);

        // "Generate Maze From File" Button
        createButton(buttonsPanel, "Generate Maze From File");

        // Padding between "Generate Maze From File" button and "Solve Maze" button.
        addHorizontalButtonPadding(buttonsPanel);

        // "Solve Maze" Button
        createButton(buttonsPanel, "Solve Maze");

        // Padding below buttons
        mainPanel.add(Box.createVerticalStrut(5));


        // Input Fields Panel (Horizontal Axis)
        inputFieldsPanel.setLayout(new FlowLayout());
        inputFieldsPanel.setBackground(backgroundColour);
        inputFieldsPanel.setMaximumSize(new Dimension(1000, 40));
        mainPanel.add(inputFieldsPanel);

        // Maze Width Input Field
        widthInputLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        widthInputLabel.setForeground(textColour);
        inputFieldsPanel.add(widthInputLabel);

        widthInputField.setBackground(textColour);
        widthInputField.setActionCommand("widthInputField");
        widthInputField.addActionListener(this);
        inputFieldsPanel.add(widthInputField);

        // Padding between the height and width input fields
        addHorizontalInputFieldPadding(inputFieldsPanel);

        // Maze Height Input Field
        heightInputLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        heightInputLabel.setForeground(textColour);
        inputFieldsPanel.add(heightInputLabel);

        heightInputField.setBackground(textColour);
        heightInputField.setActionCommand("heightInputField");
        heightInputField.addActionListener(this);
        inputFieldsPanel.add(heightInputField);

        // Padding between the height and width input fields
        addHorizontalInputFieldPadding(inputFieldsPanel);

        // Text File Path Browser Button
        browseFilesButton.setFont(new Font("Monospaced", Font.BOLD, 13));
        browseFilesButton.setForeground(textColour);
        browseFilesButton.setBackground(buttonColour);
        browseFilesButton.setFocusPainted(false);
        browseFilesButton.addActionListener(this);
        inputFieldsPanel.add(browseFilesButton);


        // Error Message
        errorMsg.setFont(new Font("Monospaced", Font.BOLD, 15));
        errorMsg.setForeground(errorMsgColour);
        errorMsg.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(errorMsg);


        // Padding between the error message and maze visual
        mainPanel.add(Box.createVerticalStrut(30));


        // Adding the maze visual panel to the main panel
        mazeVisualPanel.setBackground(backgroundColour);
        mainPanel.add(mazeVisualPanel);

        // Setting the visibility of everything to true
        setVisible(true);
    }


    // -=-  ACTION LISTENER  -=-
    /**
     * This method listens to any actions/events that were performed related to the GUI and performs the respective code.
     * 
     * @param event An action that was performed.
     * @return void
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        switch (command) {
            case "Generate Completely Random Maze": // Code that executues when the "Generate Completely Random Maze" button is pressed
                if (isWithinRange(3, maxHeight, numOfRows) && isWithinRange(3, maxWidth, numOfCols)) {
                    maze = MazeAssignment.generateCompletelyRandomMaze(numOfRows, numOfCols);
                    mazeVisualSetup(maze);

                    setErrorMsg("A completely random maze was generated!");
                } else {
                    setErrorMsg("The width or length is not valid!");
                }

                break;
            case "Generate Random Valid Maze": // Code that executues when the "Generate Random Valid Maze" button is pressed
                if (isWithinRange(3, maxHeight, numOfRows) && isWithinRange(3, maxWidth, numOfCols)) {
                    maze = MazeAssignment.generateValidMaze(numOfRows, numOfCols);
                    mazeVisualSetup(maze);

                    setErrorMsg("A random valid maze was generated!");
                } else {
                    setErrorMsg("The width or length is not valid!");
                }

                break;
            case "Generate Maze From File": // Code that executues when the "Generate Maze From File" button is pressed
                if (file.exists()) {
                    try {
                        maze = MazeAssignment.generateMazeFromFile(file);
                        mazeVisualSetup(maze);
                        setErrorMsg("A maze from file was generated!");
                    } catch (Exception e) {
                        setErrorMsg("The format of the selected text file is incorrect!");
                    }
                } else {
                    setErrorMsg("The selected file path is not valid!");
                }

                break;
            case "Solve Maze": // Code that executues when the "Solve Maze" button is pressed
                try {
                    if (MazeAssignment.solveMaze(maze)) { // If the maze IS solveable
                        mazeVisualSetup(solvedMaze);
                        setErrorMsg("The maze was solved! :D");
                    } else { // If the maze is NOT solveable
                        setErrorMsg("The maze is not solveable! D:");
                    }
                } catch (Exception e) {
                    setErrorMsg("A maze was not generated yet!");
                }

                break;
            case "widthInputField": // Code that executes when the user presses ENTER in the width input field
                try {
                    int widthInput = Integer.parseInt(widthInputField.getText());

                    if (isWithinRange(3, maxWidth, widthInput)) { // Checking if the width is within the dimensions range
                        numOfCols = widthInput;
                        setErrorMsg("The width has been set to " + widthInput + "!");
                    } else {
                        setErrorMsg("The width must be between 3 and " + maxWidth + "!");
                    }
                } catch (NumberFormatException e) {
                    setErrorMsg("The width must be an integer between 3 and " + maxWidth + "!");
                }

                break;
            case "heightInputField": // Code that executes when the user presses ENTER in the height input field
                try {
                    int heightInput = Integer.parseInt(heightInputField.getText());

                    if (isWithinRange(3, maxHeight, heightInput)) { // Checking if the height is within the dimensions range.
                        numOfRows = heightInput;
                        setErrorMsg("The height has been set to " + heightInput + "!");
                    } else {
                        setErrorMsg("The height must be between 3 and " + maxHeight + "!");
                    }
                } catch (NumberFormatException e) {
                    setErrorMsg("The height must be an integer between 3 and " + maxHeight + "!");
                }

                break;
            case "Select Maze Text File": // Code that executues when the "Select Maze From Text File" button is pressed
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false); // Prevents the user from choosing a different file format.
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt")); // Sets the file filter to text files.
                
                int userActionCode = fileChooser.showOpenDialog(null); // Opens an external file selector.

                if (userActionCode == JFileChooser.APPROVE_OPTION) { // If the user clicked "Open".
                    File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

                    if (selectedFile.exists()) {
                        setErrorMsg("The maze text file was successfully set to \"" + selectedFile.toString() + "\"!");
                        file = selectedFile;
                    } else {
                        setErrorMsg("The selected text file does not exist!");
                    }
                }

                break;
        }
    }


    // -=-  AUXILIARY GUI METHODS  -=-
    /**
     * This method creates a button for the buttons panel.
     * 
     * @param panel, text A JPanel and a String that helps with knowing how to create the button.
     * @return void
     */
    public void createButton(JPanel panel, String text) {
        JButton solveMazeButton = new JButton(text);
        solveMazeButton.setFont(new Font("Monospaced", Font.BOLD, 13));
        solveMazeButton.setForeground(textColour);
        solveMazeButton.setBackground(buttonColour);
        solveMazeButton.setFocusPainted(false);
        solveMazeButton.addActionListener(this);
        panel.add(solveMazeButton);
    }


    /**
     * This method creates horizontal padding between the buttons in the buttons panel.
     * 
     * @param panel A JPanel so that we can add the padding to the specific panel we want.
     * @return void
     */
    public static void addHorizontalButtonPadding(JPanel panel) {
        panel.add(Box.createHorizontalStrut(3));
    }


    /**
     * This method creates horizontal padding between the input fields in the input fields panel.
     * 
     * @param panel A JPanel so that we can add the padding to the specific panel we want.
     * @return void
     */
    public static void addHorizontalInputFieldPadding(JPanel panel) {
        panel.add(Box.createHorizontalStrut(20));
    }


    /**
     * This methods sets the error message that the user will see.
     * 
     * @param msg A string being the message the program wants to display to the user.
     * @return void
     */
    public static void setErrorMsg(String msg) {
        errorMsg.setText(msg);
    }


    /**
     * This methods returns true or false based on if the given value is within the given min and max value.
     * 
     * @param minValue, maxValue, value 3 integer values being the minimum value, the maximum value, and the value to check for.
     * @return boolean True or false if the given value is within the given min and max value.
     */
    public static boolean isWithinRange(int minValue, int maxValue, int value) {
        if (value >= minValue && value <= maxValue) { // If the given value IS within the range given.
            return true;
        } else { // If the given value is NOT within the range given.
            return false;
        }
    }


    /**
     * This method will set up all the settings and visuals of the maze visual.
     * 
     * @param mazeVisualPanel
     * @return void
     */
    public static void mazeVisualSetup(char[][] maze) {
        mazeVisualPanel.removeAll(); // This line is to make sure that any previously created mazes don't visually mix with the newly created one.

        mazeVisualPanel.setLayout(new GridLayout(maze.length, maze[0].length));
        mazeVisualPanel.setMaximumSize(new Dimension(maze[0].length * tileSize, maze.length * tileSize));
        
        for (char[] row : maze) {
            for (char col : row) {
                JLabel tile = new JLabel();
                tile.setFont(new Font("Monospaced", Font.BOLD, 8));
                tile.setForeground(textColour);
                tile.setOpaque(true); // Setting it so that we can have a background colour for each tile.
                tile.setBorder(BorderFactory.createLineBorder(backgroundColour)); // Adding lines between each tile

                tile.setHorizontalAlignment(SwingConstants.CENTER);
                tile.setVerticalAlignment(SwingConstants.CENTER);

                if (col == MazeAssignment.usedMazeCharacters[0]) { // If the character in the maze is a barrier.
                    tile.setText(MazeAssignment.usedMazeCharacters[0] + "");
                    tile.setBackground(barrierTileColour);
                } else if (col == MazeAssignment.usedMazeCharacters[1]) { // If the character in the maze is a open path.
                    tile.setText(MazeAssignment.usedMazeCharacters[1] + "");
                    tile.setBackground(openPathTileColour);
                } else if (col == MazeAssignment.usedMazeCharacters[2]) { // If the character in the maze is the start position.
                    tile.setText(MazeAssignment.usedMazeCharacters[2] + "");
                    tile.setBackground(startTileColour);
                } else if (col == MazeAssignment.usedMazeCharacters[3]) { // If the character in the maze is the exit position.
                    tile.setText(MazeAssignment.usedMazeCharacters[3] + "");
                    tile.setBackground(exitTileColour);
                } else if (col == MazeAssignment.usedMazeCharacters[4]) {
                    tile.setText(MazeAssignment.usedMazeCharacters[4] + "");
                    tile.setBackground(exitPathTileColour);
                }

                mazeVisualPanel.add(tile);
            }
        }

        // These 2 lines are to make sure that the maze visual panel gets updated properly
        mazeVisualPanel.revalidate();
        mazeVisualPanel.repaint();
    }
}
