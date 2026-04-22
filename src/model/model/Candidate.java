package model.model;

public class Candidate {
    private String candidateID;
    private String name;

    public Candidate(String candidateID, String name) {
        this.candidateID = candidateID;
        this.name = name;
    }

    public String getCandidateID() {
        return candidateID;
    }

    public String getName() {
        return name;
    }
}
