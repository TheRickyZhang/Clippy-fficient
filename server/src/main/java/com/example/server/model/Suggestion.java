package com.example.server.model;

import jakarta.persistence.*;

import com.example.shared.AppAction;

@Entity
public class Suggestion {
    @Id @GeneratedValue
    private Long id;

    private String sequence; // Codified string for ease of storage
    private String hint;

    @Enumerated(EnumType.STRING)
    private AppAction action;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id", nullable = false)
    User user;

    protected Suggestion() {}
    public Suggestion(String s, String h, AppAction a) {
        sequence = s;
        hint = h;
        action = a;
    }

    public String getSequence()      { return sequence; }
    public void   setSequence(String s) { sequence = s; }

    public String getHint()        { return hint; }
    public void   setHint(String h){ hint = h; }

    public AppAction getAction()          { return action; }
    public void      setAction(AppAction a) { action = a; }

    public User getUser()          { return user; }
    public void setUser(User u)    { user = u; }

}
