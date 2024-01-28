CREATE TABLE Election (
    Election_ID NUMBER  PRIMARY KEY,
    Election_Name VARCHAR2(255) ,
    Election_Date DATE 
);

CREATE TABLE Candidates (
    Candidate_ID NUMBER PRIMARY KEY,
    Candidate_Name VARCHAR2(100) ,
    Candidate_party VARCHAR2(100),
    Election_ID  NUMBER,
    FOREIGN KEY (Election_ID) REFERENCES Election(Election_ID)
);

CREATE TABLE Voters (
    Voter_ID NUMBER PRIMARY KEY,
    Voter_Name VARCHAR2(200) ,
    Voter_Address VARCHAR2(255) ,
    Voter_gender VARCHAR2(6),
   CONSTRAINT check_voter_id_length CHECK (LENGTH(TO_CHAR(VoterID)) = 12)
);


CREATE TABLE Voters_address (
    Voter_ID INT PRIMARY KEY,
    DetailedAddress VARCHAR2(500),
    Constituency VARCHAR2(255),
    Mobil_No VARCHAR2(15),
    Email_Address VARCHAR2(255),
    Reason_For_Online_Voting VARCHAR2(500),
    FOREIGN KEY (Voter_ID) REFERENCES  Voters(Voter_ID)
);

CREATE TABLE Voting (
    Voter_ID  NUMBER PRIMARY KEY, 
    Candidate_ID NUMBER,
    Election_ID NUMBER,
    FOREIGN KEY (Voter_ID) REFERENCES Voters(Voter_ID),
    FOREIGN KEY (CandidateID) REFERENCES Candidates(CandidateID),
    FOREIGN KEY (Election_ID) REFERENCES Election(Election_ID)
);

Relationships:
- Election (ElectionID) to Candidate (ElectionID) is a one-to-many relationship.
- Election (ElectionID) to Voting (ElectionID) is a one-to-many relationship.
- Candidate (CandidateID) to Voting (CandidateID) is a one-to-many relationship.
- Voter (VoterID) to Voting (VoterID) is a one-to-many relationship.
- Voter (VoterID) to Voters_address (Voter_ID) is a one-to-one relationship.

Election (ElectionID) to Candidate (ElectionID): This is a one-to-many relationship, indicating that one election can have many candidates, but each candidate belongs to only one election.

Election (ElectionID) to Voting (ElectionID): This is another one-to-many relationship, meaning that one election can have multiple votes associated with it, but each vote corresponds to a single election.

Candidate (CandidateID) to Voting (CandidateID): Again, a one-to-many relationship, signifying that one candidate can receive multiple votes, but each vote is for a specific candidate.

Voter (VoterID) to Voting (VoterID): Similar to the others, this one-to-many relationship indicates that one voter can cast multiple votes, but each vote is tied to a specific voter.

Voter (VoterID) to Voters_address (Voter_ID): This is a one-to-one relationship, meaning that each voter has one and only one set of detailed information in the Voters_address table. The Voter_ID in Voters_address is both the primary key and a foreign key referencing the Voter table, ensuring a unique link between a voter and their address details.

