package models;


public class Transaction {
    private int id;
    private int accountId;
    private double amount;
    private String type; // "deposit" or "withdraw"
    private String date;

    public Transaction(int id, int accountId, double amount, String type, String date) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDate() { return date; }

    // Setters (optional)
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setDate(String date) { this.date = date; }
}
