package installer.utils;

public enum InstallationStatus {
    // State of the installation status
    INTRODUCTION("Welcome!"),
    SELECT_DIRECTORY("Installation Direction"),
    INSTALLING("Installing Reader Advisor..."),
    COMPLETED("Thank you!");

    private String statusCode;

    private InstallationStatus(String s) {
        statusCode = s;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
