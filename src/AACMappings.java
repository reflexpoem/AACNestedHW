import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import procedures.KeyNotFoundException;
import procedures.NullKeyException;
import procedures.AssociativeArray;

/**
 * This class holds mappings for an AAC, which are two-level mappings. First
 * level is a category, and inside the
 * category, we store images that have text descriptions to speak.
 * 
 * @author Catie Baker & Sunjae Kim
 * 
 */
public class AACMappings implements AACPage {

    private AssociativeArray<String, AACCategory> categoryList; // Stores all categories
    private AssociativeArray<String, String> categoryNames; // Links a category to its name (e.g., "one" -> "fruit")
    private String currentCategory; // Stores the current selected category

    /**
     * Creates an object that loads categories and images from a file.
     * 
     * @param filename the name of the file that has the mappings
     */
    public AACMappings(String filename) {
        categoryList = new AssociativeArray<>(); // Initialize category list
        categoryNames = new AssociativeArray<>(); // Initialize category name map
        currentCategory = ""; // Start with no selected category
        loadMappingsFromFile(filename); // Load from the file
    }

    /**
     * Loads the categories and images from the file.
     * 
     * @param filename The file that has the category and image data
     */
    private void loadMappingsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String newCategoryName = null;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove spaces at the start and end

                if (!line.startsWith(">")) {
                    // This is a new category
                    String[] parts = line.split(" ", 2);
                    if (parts.length == 2) {
                        newCategoryName = parts[0]; // First part is the category name
                        String userFacingName = parts[1]; // Second part is the label like "fruit"
                        categoryNames.put(newCategoryName, userFacingName); // Save category name

                        // If the category doesn't exist, add it
                        if (!categoryList.hasKey(newCategoryName)) {
                            categoryList.put(newCategoryName, new AACCategory(newCategoryName));
                        }
                    }
                } else {
                    // This is an item under the current category
                    if (newCategoryName != null) {
                        line = line.substring(1); // Remove the ">"
                        String[] parts = line.split(" ", 2);
                        if (parts.length == 2) {
                            String imageLocation = parts[0]; // First part is image location
                            String description = parts[1]; // Second part is the description
                            categoryList.get(newCategoryName).addItem(imageLocation, description); // Add image to
                                                                                                   // category
                        }
                    }
                }
            }
        } catch (IOException | NullKeyException | KeyNotFoundException e) {
            // Handle any error during loading
            System.err.println("Error loading mappings: " + e.getMessage());
        }
    }

    /**
     * Adds a new image and its text to the current category.
     * 
     * @param imageLocation The location of the image
     * @param text          The text that should be spoken with the image
     * @throws NullKeyException if there is no selected category
     */
    @Override
    public void addItem(String imageLocation, String text) throws NullKeyException {
        if (currentCategory == null || currentCategory.isEmpty()) {
            // If no category is selected, set it to the image location
            currentCategory = imageLocation;

            // If the category doesn't exist, add it
            if (!categoryList.hasKey(currentCategory)) {
                categoryList.put(currentCategory, new AACCategory(currentCategory));
            }
            return;
        }

        try {
            // Add the image to the selected category
            AACCategory selectedCategory = categoryList.get(currentCategory);
            selectedCategory.addItem(imageLocation, text);
        } catch (KeyNotFoundException e) {
            throw new NoSuchElementException("Category '" + currentCategory + "' does not exist.");
        }
    }

    /**
     * Get the name of the currently selected category.
     * 
     * @return The name of the current category
     */
    @Override
    public String getCategory() {
        try {
            return categoryNames.get(currentCategory); // Get the display name for the category
        } catch (KeyNotFoundException e) {
            return ""; // If no category is found, return an empty string
        }
    }

    /**
     * Get all the images in the current category, or top-level categories if no
     * category is selected.
     * 
     * @return an array of image locations or category names
     */
    public String[] getImageLocs() {
        // If no category is selected, return the top-level categories
        if (currentCategory == null || currentCategory.isEmpty()) {
            return categoryList.keys(); // Get all the category names
        }

        AACCategory selectedCategory;
        try {
            selectedCategory = categoryList.get(currentCategory); // Get the current category
        } catch (KeyNotFoundException e) {
            return new String[] {}; // Return an empty array if no category is found
        }

        return selectedCategory.getImageLocs(); // Return the images in the current category
    }

    /**
     * Reset the current category to none.
     */
    public void reset() {
        currentCategory = ""; // Clear the current category
    }

    /**
     * Choose a category or an image within a category.
     * 
     * @param imageLocation The location of the image or category to select
     * @return If text is associated with the image, return it, otherwise return an
     *         empty string
     * @throws NoSuchElementException if the image or category is not found
     */
    public String select(String imageLocation) {
        if (categoryList.size() == 0) {
            throw new NoSuchElementException("No categories are available.");
        }

        try {
            // Check if the selected location is a top-level category
            if (categoryList.hasKey(imageLocation)) {
                if (currentCategory.equals(imageLocation)) {
                    throw new IllegalStateException("Category '" + imageLocation + "' is already selected.");
                }
                currentCategory = imageLocation; // Set as the current category
                return ""; // No text when selecting a category
            }

            // If no category is selected, throw an error
            if (currentCategory == null || currentCategory.isEmpty()) {
                throw new NoSuchElementException("No category is currently selected.");
            }

            // Get the selected image within the category
            AACCategory selectedCategory = categoryList.get(currentCategory);
            if (selectedCategory.hasImage(imageLocation)) {
                return selectedCategory.select(imageLocation); // Get the text for the image
            } else {
                throw new NoSuchElementException("Image location not found in category: " + imageLocation);
            }

        } catch (KeyNotFoundException e) {
            throw new NoSuchElementException("Category '" + currentCategory + "' does not exist.");
        }
    }

    /**
     * Check if an image is in the current category.
     * 
     * @param imageLocation The location of the image
     * @return true if the image exists, false otherwise
     */
    @Override
    public boolean hasImage(String imageLocation) {
        try {
            if (categoryList.hasKey(currentCategory)) {
                return categoryList.get(currentCategory).hasImage(imageLocation);
            }
        } catch (KeyNotFoundException e) {
            System.err.println("Error: Category not found.");
        }
        return false;
    }

    /**
     * Check if the given location is a category or not.
     * 
     * @param imageLocation The location of the image
     * @return true if it's a category, false if it's not
     */
    public boolean isCategory(String imageLocation) {
        return categoryList.hasKey(imageLocation);
    }

    /**
     * Save the AAC mappings to a file.
     * 
     * @param filename The file to save the mappings to
     */
    public void writeToFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename);
                PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Iterate through each category in categoryList
            for (int i = 0; i < categoryList.size(); i++) {
                String categoryKey = categoryList.getKey(i); // Get the category key (e.g., "one")
                String categoryName = categoryNames.get(categoryKey); // Get the user-friendly category name (e.g.,
                                                                      // "fruit")

                // Write the category line (e.g., "one fruit")
                printWriter.println(categoryKey + " " + categoryName);

                // Get the AACCategory object for this category
                AACCategory category = categoryList.get(categoryKey);
                String[] imageLocations = category.getImageLocs(); // Get all image locations within the category

                // Write each image and its associated description (e.g., ">a apple")
                for (String imageLocation : imageLocations) {
                    String description = category.select(imageLocation); // Get the description for the image
                    printWriter.println(">" + imageLocation + " " + description); // Write the image and description
                }
            }

        } catch (IOException | KeyNotFoundException e) {
            // Handle exceptions during writing
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Get all the top-level categories.
     * 
     * @return an array of category names
     */
    public String[] getTopLevelCategories() {
        return categoryList.keys();
    }
}
