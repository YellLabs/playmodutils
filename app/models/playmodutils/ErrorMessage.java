package models.playmodutils;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

public class ErrorMessage {
    private String code;
    private String description;
    private String fieldName;
    private URI link;
    
    public ErrorMessage(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public ErrorMessage(String code, String description, String fieldName) {
        this.code = code;
        this.description = description;
        this.fieldName = fieldName;
    }
    
    public ErrorMessage() {
    }
    
    @XmlElement
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    @XmlElement
    public String getCode() {
        return code;
    }
    
    @XmlElement
    public String getDescription() {
        return description;
    }
    
    @XmlElement
    public URI getLink() {
        return link;
    }
    
    public ErrorMessage setLink(URI uri) {
        this.link = uri;
        return this;
    }
}
