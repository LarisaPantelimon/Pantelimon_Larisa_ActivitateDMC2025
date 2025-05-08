package com.example.Login;

public class SaveEmail {
    private String myemail;
    private String emailToBeSaved;

    public SaveEmail(String myemail, String emailToBeSaved) {
        this.myemail = myemail;
        this.emailToBeSaved = emailToBeSaved;
    }

    public String getMyemail() {
        return myemail;
    }

    public void setMyemail(String myemail) {
        this.myemail = myemail;
    }

    public String getEmailToBeSaved() {
        return emailToBeSaved;
    }

    public void setEmailToBeSaved(String emailToBeSaved) {
        this.emailToBeSaved = emailToBeSaved;
    }
}
