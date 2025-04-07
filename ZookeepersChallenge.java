import java.io.*;
import java.util.*;

public class ZookeepersChallenge {

    // Method to generate the birth date from the provided data
    public static String genBirthDay(int age, String birthSeason, String arrivalDate) {
        // Use the current date and subtract the age of the animal to get the birth year
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(java.sql.Date.valueOf(arrivalDate));
        calendar.add(Calendar.YEAR, -age);

        // Adjust the birth date based on the season
        if (birthSeason.equalsIgnoreCase("spring")) {
            calendar.set(Calendar.MONTH, Calendar.MARCH); // March 21st
            calendar.set(Calendar.DAY_OF_MONTH, 21);
        } else if (birthSeason.equalsIgnoreCase("summer")) {
            calendar.set(Calendar.MONTH, Calendar.JUNE); // June 21st
            calendar.set(Calendar.DAY_OF_MONTH, 21);
        } else if (birthSeason.equalsIgnoreCase("autumn")) {
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER); // September 21st
            calendar.set(Calendar.DAY_OF_MONTH, 21);
        } else if (birthSeason.equalsIgnoreCase("winter")) {
            calendar.set(Calendar.MONTH, Calendar.DECEMBER); // December 21st
            calendar.set(Calendar.DAY_OF_MONTH, 21);
        } else {
            // Default to March 21st if season is unknown
            calendar.set(Calendar.MONTH, Calendar.MARCH);
            calendar.set(Calendar.DAY_OF_MONTH, 21);
        }

        return java.sql.Date.valueOf(calendar.getTime()).toString();
    }

    // Method to generate a unique ID for each animal
    public static String genUniqueID(String species, int count) {
        String speciesPrefix = species.substring(0, 2).toUpperCase(); // First two letters of the species
        return speciesPrefix + String.format("%02d", count);
    }

    // Animal class to hold individual animal details
    static class Animal {
        String id;
        String name;
        String species;
        int age;
        String sex;
        String color;
        double weight;
        String origin;
        String birthDate;
        String arrivalDate;
        String habitat;

        public Animal(String name, String species, int age, String sex, String color, double weight, String origin, String birthSeason, String arrivalDate, String habitat, int idCount) {
            this.name = name;
            this.species = species;
            this.age = age;
            this.sex = sex;
            this.color = color;
            this.weight = weight;
            this.origin = origin;
            this.arrivalDate = arrivalDate;
            this.birthDate = genBirthDay(age, birthSeason, arrivalDate);
            this.id = genUniqueID(species, idCount);
            this.habitat = habitat;
        }

        @Override
        public String toString() {
            return String.format("%s; %s; birth date: %s; %s color; %s; %.2f pounds; from %s; arrived %s", id, name, birthDate, color, sex, weight, origin, arrivalDate);
        }
    }

    // Zoo class to manage animals and generate reports
    static class Zoo {
        Map<String, List<Animal>> habitats = new HashMap<>();
        Map<String, Integer> speciesCount = new HashMap<>();

        // Add animal to the zoo
        public void addAnimal(Animal animal) {
            speciesCount.put(animal.species, speciesCount.getOrDefault(animal.species, 0) + 1);
            int count = speciesCount.get(animal.species);
            Animal newAnimal = new Animal(animal.name, animal.species, animal.age, animal.sex, animal.color, animal.weight, animal.origin, animal.birthDate, animal.arrivalDate, animal.habitat, count);
            habitats.putIfAbsent(animal.habitat, new ArrayList<>());
            habitats.get(animal.habitat).add(newAnimal);
        }

        // Generate a report of the zoo population
        public void generateReport() {
            try (PrintWriter writer = new PrintWriter(new FileWriter("zooPopulation.txt"))) {
                for (String habitat : habitats.keySet()) {
                    writer.println(habitat + ":");
                    for (Animal animal : habitats.get(habitat)) {
                        writer.println(animal);
                    }
                    writer.println();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method to drive the program
    public static void main(String[] args) {
        Zoo zoo = new Zoo();

        try (BufferedReader animalFile = new BufferedReader(new FileReader("arrivingAnimals.txt"));
             BufferedReader nameFile = new BufferedReader(new FileReader("animalNames.txt"))) {

            String line;
            int nameIndex = 0;
            while ((line = animalFile.readLine()) != null) {
                String[] parts = line.split(", ");
                // Extracting details
                int age = Integer.parseInt(parts[0].split(" ")[0]);
                String sex = parts[1].split(" ")[0];
                String species = parts[2];
                String birthSeason = parts[3].split(" ")[3];
                String color = parts[4].split(" ")[0];
                double weight = Double.parseDouble(parts[5].split(" ")[0]);
                String origin = parts[6];

                // Get animal name from animalNames.txt
                String name = nameFile.readLine();

                // Creating a new animal and adding to the zoo
                Animal animal = new Animal(name, species, age, sex, color, weight, origin, birthSeason, "2024-03-26", species + " Habitat", nameIndex);
                zoo.addAnimal(animal);
                nameIndex++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Generate the zoo population report
        zoo.generateReport();
    }
}


