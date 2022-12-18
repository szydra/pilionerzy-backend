package pl.pilionerzy.model;

public enum Lifeline {
    ASK_THE_AUDIENCE,
    FIFTY_FIFTY,
    PHONE_A_FRIEND;

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", "-");
    }
}
