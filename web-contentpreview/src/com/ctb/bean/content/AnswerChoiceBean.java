package com.ctb.bean.content; 

import java.io.Serializable;

public class AnswerChoiceBean implements Serializable
{
    private String id;
    private String text;
    private String type;
    
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    } 
} 
