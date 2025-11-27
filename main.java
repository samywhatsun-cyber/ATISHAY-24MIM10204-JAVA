import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Smart Academic Planner & Grade Predictor (Console UI)
 * - Manage multiple courses
 * - Add / list / update / delete assessments
 * - Calculate current grade per course
 * - Calculate required average in remaining assessments
 * - View high-level summary across all courses
 */
class Assessment {
    String name;
    double maxMarks;
    double weightPercent;    // e.g. 20 means 20% of final grade
    Double scoredMarks;      // null if not yet scored

    Assessment(String name, double maxMarks, double weightPercent, Double scoredMarks) {
        this.name = name;
        this.maxMarks = maxMarks;
        this.weightPercent = weightPercent;
        this.scoredMarks = scoredMarks;
    }

    @Override
    public String toString() {
        String scoredText = (scoredMarks == null) ? "N/A" : String.format("%.2f", scoredMarks);
        return String.format("Name: %-15s | Max: %-6.2f | Weight: %-5.2f%% | Scored: %s",
                name, maxMarks, weightPercent, scoredText);
    }
}

class Course {
    String name;
    List<Assessment> assessments = new ArrayList<>();

    Course(String name) {
        this.name = name;
    }

    void addAssessment(Assessment a) {
        assessments.add(a);
    }

    void removeAssessment(int index) {
        assessments.remove(index);
    }

    @Override
    public String toString() {
        return name;
    }
}

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Course> courses = new ArrayList<>();

    // Small helpers to make console look like a basic GUI
    private static final String SEP_MAIN = "============================================================";
    private static final String SEP_SUB = "------------------------------------------------------------";

    public static void main(String[] args) {
        printHeader("SMART ACADEMIC PLANNER & GRADE PREDICTOR");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> addCourse();
                case 2 -> listCourses();
                case 3 -> manageCourse();
                case 4 -> calculateCurrentGradeForCourse();
                case 5 -> calculateRequiredMarksForCourse();
                case 6 -> showSummaryForAllCourses();
                case 7 -> {
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =================== UI / MENUS ===================

    private static void printHeader(String title) {
        System.out.println(SEP_MAIN);
        System.out.println(centerText(title, SEP_MAIN.length()));
        System.out.println(SEP_MAIN);
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println(centerText("MAIN MENU", SEP_SUB.length()));
        System.out.println(SEP_SUB);
        System.out.println("1. Add Course");
        System.out.println("2. List Courses");
        System.out.println("3. Manage Course (assessments)");
        System.out.println("4. Calculate Current Grade for a Course");
        System.out.println("5. Calculate Required Marks (Target Grade)");
        System.out.println("6. Show Summary for All Courses");
        System.out.println("7. Exit");
        System.out.println(SEP_SUB);
    }

    private static void printManageCourseMenu(Course course) {
        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println(centerText("MANAGE COURSE: " + course.name, SEP_SUB.length()));
        System.out.println(SEP_SUB);
        System.out.println("1. Add Assessment");
        System.out.println("2. List Assessments");
        System.out.println("3. Update Scored Marks");
        System.out.println("4. Delete Assessment");
        System.out.println("5. Go Back");
        System.out.println(SEP_SUB);
    }

    // =================== COURSE OPS ===================

    private static void addCourse() {
        System.out.print("Enter course name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Course name cannot be empty.");
            return;
        }
        Course course = new Course(name);
        courses.add(course);
        System.out.println("Course added: " + name);
    }

    private static void listCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses added yet.");
            return;
        }
        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println("Courses:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, courses.get(i).name);
        }
        System.out.println(SEP_SUB);
    }

    private static Course selectCourse() {
        if (courses.isEmpty()) {
            System.out.println("No courses available. Add a course first.");
            return null;
        }
        listCourses();
        int idx = readInt("Select course number: ");
        if (idx < 1 || idx > courses.size()) {
            System.out.println("Invalid course number.");
            return null;
        }
        return courses.get(idx - 1);
    }

    private static void manageCourse() {
        Course course = selectCourse();
        if (course == null) return;

        boolean managing = true;
        while (managing) {
            printManageCourseMenu(course);
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> addAssessmentToCourse(course);
                case 2 -> listAssessments(course);
                case 3 -> updateScoredMarks(course);
                case 4 -> deleteAssessment(course);
                case 5 -> managing = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =================== ASSESSMENT OPS ===================

    private static void addAssessmentToCourse(Course course) {
        System.out.print("Enter assessment name (e.g. Midsem, Quiz 1): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Assessment name cannot be empty.");
            return;
        }

        double maxMarks = readDouble("Enter max marks (e.g. 50): ");
        double weight = readDouble("Enter weight percentage (e.g. 30 for 30%): ");

        System.out.print("Enter scored marks (or press Enter if not yet conducted): ");
        String scoredStr = scanner.nextLine().trim();
        Double scored = null;
        if (!scoredStr.isEmpty()) {
            try {
                scored = Double.parseDouble(scoredStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid scored marks input. It will be treated as not yet scored.");
            }
        }

        Assessment a = new Assessment(name, maxMarks, weight, scored);
        course.addAssessment(a);
        System.out.println("Assessment added to course " + course.name);
    }

    private static void listAssessments(Course course) {
        if (course.assessments.isEmpty()) {
            System.out.println("No assessments for this course yet.");
            return;
        }
        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println("Assessments for course: " + course.name);
        for (int i = 0; i < course.assessments.size(); i++) {
            Assessment a = course.assessments.get(i);
            System.out.printf("%d. %s%n", i + 1, a.toString());
        }
        System.out.println(SEP_SUB);
    }

    private static void updateScoredMarks(Course course) {
        if (course.assessments.isEmpty()) {
            System.out.println("No assessments to update.");
            return;
        }
        listAssessments(course);
        int idx = readInt("Select assessment number to update: ");
        if (idx < 1 || idx > course.assessments.size()) {
            System.out.println("Invalid assessment number.");
            return;
        }
        Assessment a = course.assessments.get(idx - 1);
        System.out.print("Enter new scored marks (or press Enter to clear): ");
        String scoredStr = scanner.nextLine().trim();
        if (scoredStr.isEmpty()) {
            a.scoredMarks = null;
            System.out.println("Scored marks cleared.");
        } else {
            try {
                a.scoredMarks = Double.parseDouble(scoredStr);
                System.out.println("Scored marks updated.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. No changes made.");
            }
        }
    }

    private static void deleteAssessment(Course course) {
        if (course.assessments.isEmpty()) {
            System.out.println("No assessments to delete.");
            return;
        }
        listAssessments(course);
        int idx = readInt("Select assessment number to delete: ");
        if (idx < 1 || idx > course.assessments.size()) {
            System.out.println("Invalid assessment number.");
            return;
        }
        Assessment removed = course.assessments.get(idx - 1);
        course.removeAssessment(idx - 1);
        System.out.println("Deleted assessment: " + removed.name);
    }

    // =================== GRADE CALCULATIONS ===================

    private static void calculateCurrentGradeForCourse() {
        Course course = selectCourse();
        if (course == null) return;

        if (course.assessments.isEmpty()) {
            System.out.println("No assessments for this course.");
            return;
        }

        double totalWeightedScore = 0.0;
        double totalCompletedWeight = 0.0;

        for (Assessment a : course.assessments) {
            if (a.scoredMarks != null) {
                double scorePercent = (a.scoredMarks / a.maxMarks) * 100.0;
                double contribution = (scorePercent * a.weightPercent) / 100.0;
                totalWeightedScore += contribution;
                totalCompletedWeight += a.weightPercent;
            }
        }

        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println("Current Grade Report");
        System.out.println("Course: " + course.name);
        if (totalCompletedWeight == 0.0) {
            System.out.println("No completed assessments yet.");
        } else {
            double currentGrade = (totalWeightedScore / totalCompletedWeight) * 100.0;
            System.out.printf("Completed Weight: %.2f %%\n", totalCompletedWeight);
            System.out.printf("Weighted Score so far: %.2f / 100.00\n", totalWeightedScore);
            System.out.printf("Current Grade (based on completed assessments): %.2f %%\n", currentGrade);
        }
        System.out.println(SEP_SUB);
    }

    private static void calculateRequiredMarksForCourse() {
        Course course = selectCourse();
        if (course == null) return;

        if (course.assessments.isEmpty()) {
            System.out.println("No assessments for this course.");
            return;
        }

        double targetGrade = readDouble("Enter target final percentage (e.g. 85): ");

        double totalWeightedScore = 0.0;
        double totalCompletedWeight = 0.0;
        double remainingWeight = 0.0;

        for (Assessment a : course.assessments) {
            if (a.scoredMarks != null) {
                double scorePercent = (a.scoredMarks / a.maxMarks) * 100.0;
                double contribution = (scorePercent * a.weightPercent) / 100.0;
                totalWeightedScore += contribution;
                totalCompletedWeight += a.weightPercent;
            } else {
                remainingWeight += a.weightPercent;
            }
        }

        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println("Required Marks Analysis");
        System.out.println("Course: " + course.name);
        System.out.printf("Target Final Grade: %.2f %%\n", targetGrade);
        System.out.printf("Completed Weight: %.2f %%\n", totalCompletedWeight);
        System.out.printf("Remaining Weight: %.2f %%\n", remainingWeight);

        if (remainingWeight <= 0.0) {
            System.out.println("No remaining assessments. Final grade is already determined.");
            System.out.printf("Current weighted score: %.2f / 100.00\n", totalWeightedScore);
            System.out.println(SEP_SUB);
            return;
        }

        // Equation:
        // totalWeightedScore + (avgRemainingPercent * remainingWeight / 100) = targetGrade
        // -> avgRemainingPercent = (targetGrade - totalWeightedScore) * 100 / remainingWeight
        double requiredAveragePercent = (targetGrade - totalWeightedScore) * 100.0 / remainingWeight;

        if (requiredAveragePercent > 100.0) {
            System.out.printf("It is NOT possible to reach the target grade.\n");
            System.out.printf("You would need an average of %.2f %% in remaining assessments (> 100%%).\n",
                    requiredAveragePercent);
        } else if (requiredAveragePercent < 0.0) {
            System.out.println("Target already achieved.");
            System.out.println("Even scoring 0 in remaining assessments, you will stay above the target.");
        } else {
            System.out.printf("You need an average of %.2f %% in the remaining assessments to reach the target.\n",
                    requiredAveragePercent);
        }
        System.out.println(SEP_SUB);
    }

    // =================== SUMMARY ACROSS COURSES ===================

    private static void showSummaryForAllCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        System.out.println();
        System.out.println(SEP_SUB);
        System.out.println("SEMESTER SUMMARY (per-course current status)");
        System.out.println(SEP_SUB);

        for (Course course : courses) {
            double totalWeightedScore = 0.0;
            double totalCompletedWeight = 0.0;

            for (Assessment a : course.assessments) {
                if (a.scoredMarks != null) {
                    double scorePercent = (a.scoredMarks / a.maxMarks) * 100.0;
                    double contribution = (scorePercent * a.weightPercent) / 100.0;
                    totalWeightedScore += contribution;
                    totalCompletedWeight += a.weightPercent;
                }
            }

            System.out.println("Course: " + course.name);
            if (course.assessments.isEmpty()) {
                System.out.println("  No assessments defined.");
            } else if (totalCompletedWeight == 0.0) {
                System.out.println("  No completed assessments yet.");
            } else {
                double currentGrade = (totalWeightedScore / totalCompletedWeight) * 100.0;
                System.out.printf("  Completed Weight: %.2f %%\n", totalCompletedWeight);
                System.out.printf("  Current Grade (completed only): %.2f %%\n", currentGrade);
            }
            System.out.println(SEP_SUB);
        }
    }

    // =================== INPUT HELPERS ===================

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
}
    