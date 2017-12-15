package ca.uoit.csci4100u.workplace_app.inc;

/**
 * A simple company class to store chat related data
 */
public class Company {

    private String companyId;
    private String companyName;


    private String companyLoc;

    /**
     * Constructor to easily create a company
     * @param companyId The company id
     * @param companyName The company name
     */
    public Company(String companyId, String companyName, String companyLoc) {
        setCompanyId(companyId);
        setCompanyName(companyName);
        setCompanyLoc(companyLoc);
    }

    /**
     * Getter for the company id
     * @return The company id as a string
     */
    public String getCompanyId() {
        return companyId;
    }

    /**
     * Setter for the company id
     * @param companyId The company id as a string
     */
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    /**
     * Getter for the company name
     * @return The company name as a string
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Setter for the company name
     * @param companyName The company name as a string
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLoc() {
        return companyLoc;
    }

    public void setCompanyLoc(String companyLoc) {
        this.companyLoc = companyLoc;
    }
    /**
     * Overridden toString() method to use for the Spinner's ArrayAdapter
     * @return The company name as a string
     */
    @Override
    public String toString() {
        return getCompanyId() + " " + getCompanyName() + " " + getCompanyLoc();
    }

}
