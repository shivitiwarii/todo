package com.assignment.todo;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "todos")
public class TodoValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private boolean done;

    public Long getId() {
        return id;
    }
    
    public String getContent() {
        return text;
    }
    
    public boolean isCompleted() {
        return done;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setContent(String content) {
        this.text = content;
    }
    
    public void setCompleted(boolean completed) {
        this.done = completed;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public User getUser(){
        return user;
    }
     
    public Long getUserId() {
        return userId;
    }

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}