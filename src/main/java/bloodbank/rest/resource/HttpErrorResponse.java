/**
 * File: HttpErrorResponse.java
 * Course materials (21W) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * Note: students do NOT need to change anything in this class
 *
 */
package bloodbank.rest.resource;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int statusCode;
    private final String reasonPhrase;

    public HttpErrorResponse(int code, String reasonPhrase) {
        this.statusCode = code;
        this.reasonPhrase = reasonPhrase;
    }
    
    @JsonProperty("status-code")
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty("reason-phrase")
    public String getReasonPhrase() {
        return reasonPhrase;
    }

}