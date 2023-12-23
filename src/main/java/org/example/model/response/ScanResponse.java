package org.example.model.response;

import org.example.model.Scan;

import java.util.List;

public class ScanResponse {
    private Scan scan;
    private boolean success;
    private List<Error> errors;

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
