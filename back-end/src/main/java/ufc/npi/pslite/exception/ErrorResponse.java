package ufc.npi.pslite.exception;

import java.util.List;

public class ErrorResponse {

    private String error;
    private String detail;
    private List<String> messages;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String detail) {
        this.error = error;
        this.detail = detail;
    }

    public ErrorResponse(String error, String detail, List<String> messages) {
        this.error = error;
        this.detail = detail;
        this.messages = messages;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
