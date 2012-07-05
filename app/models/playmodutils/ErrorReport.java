package models.playmodutils;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "errors")
public class ErrorReport {
    
    private ErrorMessage[] errors;
    
    public ErrorReport(ErrorMessage... errors) {
        this.errors = errors;
    }
    
    public ErrorReport(List<? extends ErrorMessage> errors) {
        this(errors.toArray(new ErrorMessage[errors.size()]));
    }
    
    public ErrorReport() {
        this(new ErrorMessage[0]);
    }
    
    @XmlElement(name = "error")
    public ErrorMessage[] getErrors() {
        return errors;
    }
}