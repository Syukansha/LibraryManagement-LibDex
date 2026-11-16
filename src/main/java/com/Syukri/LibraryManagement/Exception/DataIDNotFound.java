package com.Syukri.LibraryManagement.Exception;

import java.util.UUID;

public class DataIDNotFound extends RuntimeException {
    public DataIDNotFound(UUID dataId) {

        super("Data not found with ID: "+ dataId);
    }

}
