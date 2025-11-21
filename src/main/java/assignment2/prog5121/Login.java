package assignment2.prog5121;

/**
 * Login class implements the required methods for Part 1.
 * - checkUserName()
 * - checkPasswordComplexity()
 * - checkCellPhoneNumber()
 * - registerUser()
 * - loginUser()
 * - returnLoginStatus()
 *
 * Messages match assignment wording exactly.
 */
public class Login {
    private String storedUsername;
    private String storedPassword;
    private String storedCellNumber;
    private String firstName;
    private String lastName;

    public Login() {
        // default empty constructor
    }

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Username must contain underscore and be no more than five characters.
     * @param username
     * @return 
     */
    public boolean checkUserName(String username) {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Password complexity:
     * - At least 8 characters
     * - At least one uppercase
     * - At least one digit
     * - At least one special character
     * @param password
     * @return 
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Cell phone must start with +27 followed by 9 digits (e.g. +27838968976)
     * regex uses double-backslashes for Java string.
     * @param cellNumber
     * @return 
     */
    public boolean checkCellPhoneNumber(String cellNumber) {
        if (cellNumber == null) return false;
        return cellNumber.matches("^\\+27\\d{9}$");
    }

    /**
     * Register user returns messages exactly as required.
     * @param username
     * @param password
     * @param cellNumber
     * @param firstName
     * @param lastName
     * @return 
     */
    public String registerUser(String username, String password, String cellNumber, String firstName, String lastName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        this.storedUsername = username;
        this.storedPassword = password;
        this.storedCellNumber = cellNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        return "User successfully registered.";
    }

    /**
     * loginUser returns boolean for tests.
     * @param username
     * @param password
     * @return 
     */
    public boolean loginUser(String username, String password) {
        return storedUsername != null && storedUsername.equals(username)
                && storedPassword != null && storedPassword.equals(password);
    }

    /**
     * returnLoginStatus returns messages matching the rubric.
     * @param username
     * @param password
     * @return 
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + " " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Getters used by tests or UI
    public String getStoredUsername() { return storedUsername; }
    public String getStoredCellNumber() { return storedCellNumber; }
}
